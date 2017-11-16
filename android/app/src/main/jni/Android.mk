LOCAL_PATH:= $(call my-dir) 

include $(CLEAR_VARS)
#LOCAL_ARM_MODE := arm
LOCAL_MODULE := mem_fill_tool
LOCAL_SRC_FILES := com_tencent_wstt_gt_api_utils_MemFillTool.c
include $(BUILD_SHARED_LIBRARY)

