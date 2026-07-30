#include <jni.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <net/if.h>
#include <unistd.h>
#include <fcntl.h>
#include <dlfcn.h>
#include <string>
#include <fstream>
#include <sstream>
#include "zygisk.hpp"
#include "dobby.h"

using zygisk::Api;
using zygisk::AppSpecializeArgs;

struct SpoofConfig {
    std::string wifi_ssid   = "Original_WiFi";
    std::string bssid       = "00:11:22:33:44:55:66:77";
    std::string bt_mac      = "AA:BB:CC:DD:EE:FF:11:22";
    std::string drm_id      = "A1B2C3D4E5F67890A1B2C3D4E5F67890";
    std::string system_id   = "4012";
} g_cfg;

static void load_config() {
    std::ifstream f("/data/adb/wifi_spoof/config.properties");
    if (!f.is_open()) return;
    std::string line;
    while (std::getline(f, line)) {
        std::istringstream ss(line);
        std::string k;
        if (!std::getline(ss, k, '=')) continue;
        std::string v;
        if (!std::getline(ss, v)) continue;
        if (k == "wifi_ssid")           g_cfg.wifi_ssid  = v;
        else if (k == "bssid")          g_cfg.bssid      = v;
        else if (k == "bluetooth_mac")  g_cfg.bt_mac     = v;
        else if (k == "widevine_drm_id") g_cfg.drm_id    = v;
        else if (k == "system_id")      g_cfg.system_id  = v;
    }
}

// Hook: redirect MAC address file reads to spoofed files
// open() resolved by -U_FORTIFY_SOURCE in Android.mk
static int (*orig_open)(const char *, int, ...);
static int hook_open(const char *path, int flags, mode_t mode) {
    if (path) {
        std::string p(path);
        if (p == "/sys/class/net/wlan0/address" || p == "/sys/class/net/wlan/address")
            return orig_open("/data/adb/wifi_spoof/spoofed_wifi_mac", flags, mode);
        if (p == "/sys/class/net/bt0/address" || p == "/sys/class/net/bluetooth/address")
            return orig_open("/data/adb/wifi_spoof/spoofed_bt_mac", flags, mode);
    }
    return orig_open(path, flags, mode);
}

// Hook: spoof hardware MAC address for ioctl queries
// ioctl() is __overloadable in NDK; use dlsym at runtime to get real addr
static int (*orig_ioctl)(int, unsigned long, ...);
static int hook_ioctl(int fd, unsigned long req, void *argp) {
    int ret = orig_ioctl(fd, req, argp);
    if (req == SIOCGIFHWADDR && argp) {
        struct ifreq *ifr = (struct ifreq *)argp;
        std::string iface(ifr->ifr_name);
        const std::string *mac_str = nullptr;
        if (iface == "wlan0")
            mac_str = &g_cfg.bssid;
        else if (iface == "bt0" || iface == "bluetooth")
            mac_str = &g_cfg.bt_mac;
        if (mac_str) {
            unsigned int mac[8] = {};
            int n = sscanf(mac_str->c_str(), "%x:%x:%x:%x:%x:%x:%x:%x",
                           &mac[0], &mac[1], &mac[2], &mac[3],
                           &mac[4], &mac[5], &mac[6], &mac[7]);
            for (int i = 0; i < (n == 8 ? 8 : 6); i++)
                ifr->ifr_hwaddr.sa_data[i] = (char)mac[i];
        }
    }
    return ret;
}

class WifiChangerModule : public zygisk::ModuleBase {
public:
    void onLoad(Api *api, JNIEnv *env) override {
        this->api = api;
        this->env = env;
    }

    void preAppSpecialize(AppSpecializeArgs *args) override {
        load_config();
        auto write_file = [](const char *path, const std::string &val) {
            std::ofstream f(path);
            if (f.is_open()) f << val << "\n";
        };
        write_file("/data/adb/wifi_spoof/spoofed_wifi_mac", g_cfg.bssid);
        write_file("/data/adb/wifi_spoof/spoofed_bt_mac",   g_cfg.bt_mac);
    }

    void postAppSpecialize(const AppSpecializeArgs *) override {
        // open() fixed by -U_FORTIFY_SOURCE in Android.mk
        DobbyHook((void *)open, (void *)hook_open, (void **)&orig_open);
        // ioctl() is __overloadable, resolve at runtime via dlsym
        void *real_ioctl = dlsym(RTLD_NEXT, "ioctl");
        if (real_ioctl) {
            DobbyHook(real_ioctl, (void *)hook_ioctl, (void **)&orig_ioctl);
        }
    }

private:
    Api *api;
    JNIEnv *env;
};

REGISTER_ZYGISK_MODULE(WifiChangerModule)
