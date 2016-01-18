/*
 * log_util.h
 *
 *  Created on: 2011-12-7
 *      Author: boyliang
 */

#ifndef LOG_UTIL_H_
#define LOG_UTIL_H_

#include <stdlib.h>
#include <android/log.h>

#ifdef LOG_TAG
#undef LOG_TAG
#endif

#define LOG_TAG "LOGGER"

#ifdef DEBUG

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

#define check_value(x) 														\
	__android_log_print(ANDROID_LOG_INFO, LOG_TAG, "%s: %p", #x, x);		\
	if(x == NULL){ 															\
		__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "%s was NULL", #x);	\
	   	exit(0);															\
	}

#else

#define check_value(x) if(0)

#endif

#endif /* LOG_UTIL_H_ */
