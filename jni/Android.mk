LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
OPENCV_LIB_TYPE:=STATIC
OPENCV_CAMERA_MODULES:=off
OPENCV_INSTALL_MODULES:=on
include ../../opencv/OpenCV-2.4.6-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE := exec_text_detect
LOCAL_SRC_FILES := text_detect.cpp exec_text_detect.cpp
LOCAL_LDLIBS += -landroid -llog -ldl

include $(BUILD_SHARED_LIBRARY)





