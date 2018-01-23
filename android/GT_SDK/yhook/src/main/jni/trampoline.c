//
// Created by liuruikai756 on 05/07/2017.
//
#include <sys/mman.h>
#include <string.h>

#include "common.h"
#include "env.h"
#include "trampoline.h"

static unsigned char *trampolineCode; // place where trampolines are saved
static unsigned int trampolineCodeSize; // total size of trampoline code area
static unsigned int trampolineSize; // trampoline size required for each hook

unsigned int hookCap = 0;
unsigned int hookCount = 0;

// trampoline1: set eax/r0/x0 to the hook ArtMethod addr and then jump into its entry point
#if defined(__i386__)
// b8 78 56 34 12 ; mov eax, 0x12345678
// 66 c7 40 12 00 00 ; mov word [eax + 0x12], 0
// ff 70 20 ; push dword [eax + 0x20]
// c3 ; ret
unsigned char trampoline1[] = {
        0xb8, 0x78, 0x56, 0x34, 0x12,
        0x66, 0xc7, 0x40, 0x12, 0x00, 0x00,
        0xff, 0x70, 0x20,
        0xc3
};
static unsigned int t1Size = roundUpToPtrSize(sizeof(trampoline1)); // for alignment

#elif defined(__arm__)
    //------------------------------> 10 00 9f e5 ; ldr r0, [pc, #20]
    //------------------------------> 04 40 2d e5 ; push {r4}
    //------------------------------> 00 40 a0 e3 ; mov r4, #0
    //------------------------------> b2 41 c0 e1 ; strh r4, [r0, #18]
    //------------------------------> 04 40 9d e4 ; pop {r4}
// 00 00 9F E5 ; ldr r0, [pc]
// 20 F0 90 E5 ; ldr pc, [r0, 0x20]
// 78 56 34 12 ; 0x12345678
unsigned char trampoline1[] = {
        0x14, 0x00, 0x9f, 0xe5,
        0x04, 0x40, 0x2d, 0xe5,
        0x00, 0x40, 0xa0, 0xe3,
        0xb2, 0x41, 0xc0, 0xe1,
        0x04, 0x40, 0x9d, 0xe4,
        0x00, 0x00, 0x9f, 0xe5,
        0x20, 0xf0, 0x90, 0xe5,
        0x78, 0x56, 0x34, 0x12
};
static unsigned int t1Size = sizeof(trampoline1);

#elif defined(__aarch64__)
// 60 00 00 58 ; ldr x0, 12    ------->// 80 00 00 58 ; ldr x0, 16  解决同dex时hotness的问题
                                       // 1f 24 00 79 ; strh wzr, [x0, #18]

// 10 18 40 f9 ; ldr x16, [x0, #48]
// 00 02 1f d6 ; br x16
// 78 56 34 12
// 89 67 45 23 ; 0x2345678912345678
unsigned char trampoline1[] = {
        0x80, 0x00, 0x00, 0x58,
        0x1f, 0x24, 0x00, 0x79,
//        0x60, 0x00, 0x00, 0x58,
        0x10, 0x18, 0x40, 0xf9,
        0x00, 0x02, 0x1f, 0xd6,
        0x78, 0x56, 0x34, 0x12,
        0x89, 0x67, 0x45, 0x23
};
static unsigned int t1Size = roundUpToPtrSize(sizeof(trampoline1));
#endif

// trampoline2:
// 1.1 set eax/r0/x0 to the copy of origin ArtMethod addr,
// 2. clear hotness_count of the copy origin ArtMethod(only after Android N)
// 3. jump into origin's real entry point
#if defined(__i386__)
// b8 21 43 65 87 ; mov eax, 0x87654321
// 66 c7 40 12 00 00 ; mov word [eax + 0x12], 0
// 68 78 56 34 12 ; push 0x12345678
// c3 ; ret
unsigned char trampoline2[] = {
        0xb8, 0x21, 0x43, 0x65, 0x87,
        0x66, 0xc7, 0x40, 0x12, 0x00, 0x00,
        0x68, 0x78, 0x56, 0x34, 0x12,
        0xc3
};
static unsigned int t2Size = roundUpToPtrSize(sizeof(trampoline2)); // for alignment

#elif defined(__arm__)
// 10 00 9f e5 ; ldr r0, [pc, #16]
// 04 40 2d e5 ; push {r4}
// 00 40 a0 e3 ; mov r4, #0
// b2 41 c0 e1 ; strh r4, [r0, #18]
// 04 40 9d e4 ; pop {r4}
// 00 f0 9f e5 ; ldr pc, [pc, #0]
// 21 43 65 87 ; 0x87654321
// 78 56 34 12 ; 0x12345678
unsigned char trampoline2[] = {
        0x10, 0x00, 0x9f, 0xe5,
        0x04, 0x40, 0x2d, 0xe5,
        0x00, 0x40, 0xa0, 0xe3,
        0xb2, 0x41, 0xc0, 0xe1,
        0x04, 0x40, 0x9d, 0xe4,
        0x00, 0xf0, 0x9f, 0xe5,
        0x21, 0x43, 0x65, 0x87,
        0x78, 0x56, 0x34, 0x12
};
static unsigned int t2Size = sizeof(trampoline2);

#elif defined(__aarch64__)
// 80 00 00 58 ; ldr x0, [pc, #16]
// 1f 24 00 79 ; strh wzr, [x0, #18]
// 90 00 00 58 ; ldr x16, [pc, #16]
// 00 02 1f d6 ; br x16
// 89 67 45 23
// 78 56 34 12 ; 0x1234567823456789
// 78 56 34 12
// 89 67 45 23 ; 0x2345678912345678
unsigned char trampoline2[] = {
        0x80, 0x00, 0x00, 0x58,
        0x1f, 0x24, 0x00, 0x79,
        0x90, 0x00, 0x00, 0x58,
        0x00, 0x02, 0x1f, 0xd6,
        0x89, 0x67, 0x45, 0x23,
        0x78, 0x56, 0x34, 0x12,
        0x78, 0x56, 0x34, 0x12,
        0x89, 0x67, 0x45, 0x23
};
static unsigned int t2Size = roundUpToPtrSize(sizeof(trampoline2));
#endif

void *genTrampoline1(void *hookMethod) {
    void *targetAddr;
    /*
    if(mprotect(trampolineCode, trampolineCodeSize, PROT_READ | PROT_WRITE) == -1) {
        LOGE("mprotect RW failed");
        return NULL;
    }*/
    targetAddr = trampolineCode + trampolineSize*hookCount;
    memcpy(targetAddr, trampoline1, sizeof(trampoline1)); // do not use t1size since it's a rounded size

    // replace with the hook ArtMethod addr
#if defined(__i386__)
    memcpy(targetAddr+1, &hookMethod, pointer_size);
#elif defined(__arm__)
    memcpy(targetAddr+28, &hookMethod, pointer_size);
//    memcpy(targetAddr+8, &hookMethod, pointer_size);
#elif defined(__aarch64__)
    memcpy(targetAddr+16, &hookMethod, pointer_size);
#endif
/*
    if(mprotect(trampolineCode, trampolineCodeSize, PROT_READ | PROT_EXEC) == -1) {
        LOGE("mprotect RX failed");
        return NULL;
    }
    */
    return targetAddr;
}

void *genTrampoline2(void *originMethod, void *entryPoint) {
    int i;
    void *targetAddr;
    /*
    if(mprotect(trampolineCode, trampolineCodeSize, PROT_READ | PROT_WRITE) == -1) {
        LOGE("mprotect RW failed");
        return NULL;
    }
     */
    targetAddr = trampolineCode + trampolineSize*hookCount + t1Size;
    memcpy(targetAddr, trampoline2, sizeof(trampoline2)); // do not use t2size since it's a rounded size

    // set eax/r0/x0 and the real entrypoint
#if defined(__i386__)
    memcpy(targetAddr+1, &originMethod, pointer_size);
    memcpy(targetAddr+12, &entryPoint, pointer_size);
#elif defined(__arm__)
    memcpy(targetAddr+24, &originMethod, pointer_size);
    memcpy(targetAddr+28, &entryPoint, pointer_size);
#elif defined(__aarch64__)
    memcpy(targetAddr+16, &originMethod, pointer_size);
    memcpy(targetAddr+24, &entryPoint, pointer_size);
#endif
/*
    if(mprotect(trampolineCode, trampolineCodeSize, PROT_READ | PROT_EXEC) == -1) {
        LOGE("mprotect RX failed");
        return NULL;
    }
    */
//    LOGI("trampoline 2 is at %p", targetAddr);
    return targetAddr;
}

int doInitHookCap(unsigned int cap) {
    trampolineSize = t1Size + t2Size;
    if(cap == 0) {
        LOGE("invalid capacity: %d", cap);
        return 1;
    }
    if(hookCap) {
        LOGW("allocating new space for trampoline code");
    }
    unsigned int allSize = trampolineSize*cap;
    unsigned char *buf = mmap(NULL, allSize, PROT_READ | PROT_WRITE | PROT_EXEC, MAP_ANON | MAP_PRIVATE, -1, 0);
    if(buf == MAP_FAILED) {
        LOGE("mmap failed");
        return 1;
    }
    hookCap = cap;
    hookCount = 0;
    trampolineCode = buf;
    trampolineCodeSize = allSize;
    return 0;
}
