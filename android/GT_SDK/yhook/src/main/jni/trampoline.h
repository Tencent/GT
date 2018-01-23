//
// Created by liuruikai756 on 05/07/2017.
//

#ifndef YAHFA_TAMPOLINE_H
#define YAHFA_TAMPOLINE_H

extern unsigned int hookCap; // capacity for trampolines
extern unsigned int hookCount; // current count of used trampolines

extern unsigned char trampoline1[];
extern unsigned char trampoline2[];

int doInitHookCap(unsigned int cap);

void *genTrampoline1(void *hookMethod);

void *genTrampoline2(void *originMethod, void *entryPoint);

#define DEFAULT_CAP 64 //size of each trampoline area would be no more than 4k Bytes(one page)

#endif //YAHFA_TAMPOLINE_H
