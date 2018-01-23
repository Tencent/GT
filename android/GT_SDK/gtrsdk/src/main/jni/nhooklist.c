//
// Created by p_svengong on 2017/11/8.
//

#include <stdarg.h>
#include <stdint.h>
#include <stdio.h>
#include <sys/time.h>
#include <unistd.h>
#include <android/log.h>
#include <stdbool.h>
#include <malloc.h>

char *GTR_DATA_TAG = "GTR_DATA_TAG";
char *separator = "_&&GTRFile&_";


char *gtrPath = "sdcard/GTR";

bool isNeedIOFD(char *path);

void addIOFD(int fd);

void removeIOFD(int fd);

bool hasIOFd(int fd);

int indexOf(char *str1, char *str2);


/**  hook open 函数 **/
int (*open_old)(const char *path, int mode, ...)=NULL;

int open_new(const char *path, int mode, ...) {
    if (open_old == NULL) {
        return -1;
    }
    char timeStartStr[40];
    struct timeval timeStart;
    gettimeofday(&timeStart, NULL);
    sprintf(timeStartStr, "%ld%ld", timeStart.tv_sec, timeStart.tv_usec / 1000);
    va_list ap;
    va_start(ap, mode);
    int fd = open_old(path, mode, ap);

    if (isNeedIOFD(path)) {
        addIOFD(fd);
    } else {
        removeIOFD(fd);
    }

    if (hasIOFd(fd)) {
        char tidStr[15];
        sprintf(tidStr, "%d", gettid());
        char fdStr[15];
        sprintf(fdStr, "%d", fd);
        char timeEndStr[40];
        struct timeval timeEnd;
        gettimeofday(&timeEnd, NULL);
        sprintf(timeEndStr, "%ld%ld", timeEnd.tv_sec, timeEnd.tv_usec / 1000);
        __android_log_print(ANDROID_LOG_VERBOSE, GTR_DATA_TAG, "%s%s%s%s%s%s%s%s%s%s%s",
                            "file_open",
                            separator, tidStr,
                            separator, fdStr,
                            separator, path,
                            separator, timeStartStr,
                            separator, timeEndStr);
    }
    return fd;
}

bool isNeedIOFD(char *path) {
    //包含"/sdcard/" 且不包含"/sdcard/GTR/"
    if (indexOf(path, "/sdcard/") == 0 && indexOf(path, "/sdcard/GTR/") == -1) {
        return true;
    }
    if (indexOf(path, "/data/") == 0 && indexOf(path, "/databases/") != -1) {
        return true;
    }
    return false;
}

int indexOf(char *str1, char *str2) {
    char *p = str1;
    int i = 0;
    p = strstr(str1, str2);
    if (p == NULL)
        return -1;
    else {
        while (str1 != p) {
            str1++;
            i++;
        }
    }
    return i;
}

//节点的定义
typedef int Item;//定义数据项类型
typedef struct node *PNode;//定义节点指针
typedef struct node {
    Item item;//数据域
    PNode next;//链域

} Node;

PNode fdList = NULL;//需要保存IO数据的fd列表(有表头的单链表)

void addIOFD(int fd) {
    if (fdList == NULL) {
        fdList = (Node *) malloc(sizeof(Node));
        fdList->item = -1;
        fdList->next = NULL;
    }
    PNode p = fdList;
    while (p->next != NULL) {
        if (p->next->item == fd) {
            return;
        }
        p = p->next;
    }
    PNode newNode = (Node *) malloc(sizeof(Node));
    newNode->item = fd;
    newNode->next = fdList->next;
    fdList->next = newNode;
}

void removeIOFD(int fd) {
    if (fdList == NULL) {
        fdList = (Node *) malloc(sizeof(Node));
        fdList->item = -1;
        fdList->next = NULL;
    }
    PNode p = fdList;
    while (p->next != NULL) {
        if (p->next->item == fd) {
            p->next = p->next->next;
            return;
        }
        p = p->next;
    }
}

bool hasIOFd(int fd) {
    //__android_log_print(ANDROID_LOG_VERBOSE, GTR_DATA_TAG, "hasIOFd: %d",fd);
    if (fdList == NULL) {
        fdList = (Node *) malloc(sizeof(Node));
        fdList->item = -1;
        fdList->next = NULL;
    }
    PNode p = fdList;
    while (p->next != NULL) {
        if (p->next->item == fd) {
            return true;
        }
        p = p->next;
    }
    return false;
}