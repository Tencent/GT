LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= nhooklist.c

LOCAL_LDLIBS    := -llog

LOCAL_MODULE:= nhooklist

LOCAL_ARM_MODE := arm

include $(BUILD_SHARED_LIBRARY)
