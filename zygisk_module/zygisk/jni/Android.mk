LOCAL_PATH := $(call my-dir)

# Import prebuilt dobby static library
include $(CLEAR_VARS)
LOCAL_MODULE := dobby
LOCAL_SRC_FILES := lib/$(TARGET_ARCH_ABI)/libdobby.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_STATIC_LIBRARY)

# Build our Zygisk module
include $(CLEAR_VARS)
LOCAL_MODULE := wifi_bssid_changer_zygisk
LOCAL_SRC_FILES := main.cpp
LOCAL_STATIC_LIBRARIES := dobby
LOCAL_LDLIBS := -llog
LOCAL_CPPFLAGS := -std=c++17 -Wall -Wextra -fvisibility=hidden -Iinclude -U_FORTIFY_SOURCE

include $(BUILD_SHARED_LIBRARY)
