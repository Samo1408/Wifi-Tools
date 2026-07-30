#!/system/bin/sh
# Runs in post-fs-data (early boot, after /data is mounted)

MODDIR=${0%/*}

# Create config directory with world-readable permissions
mkdir -p /data/adb/wifi_spoof
chmod 755 /data/adb/wifi_spoof

CONFIG_FILE="/data/adb/wifi_spoof/config.properties"
if [ ! -f "$CONFIG_FILE" ]; then
    echo "wifi_ssid=Original_WiFi" > "$CONFIG_FILE"
    echo "bssid=00:11:22:33:44:55:66:77" >> "$CONFIG_FILE"
    echo "bluetooth_mac=AA:BB:CC:DD:EE:FF:11:22" >> "$CONFIG_FILE"
    echo "widevine_drm_id=A1B2C3D4E5F67890A1B2C3D4E5F67890" >> "$CONFIG_FILE"
    echo "system_id=4012" >> "$CONFIG_FILE"
    chmod 644 "$CONFIG_FILE"
fi

echo "00:11:22:33:44:55:66:77" > /data/adb/wifi_spoof/spoofed_wifi_mac
echo "AA:BB:CC:DD:EE:FF:11:22" > /data/adb/wifi_spoof/spoofed_bt_mac
chmod 644 /data/adb/wifi_spoof/spoofed_wifi_mac
chmod 644 /data/adb/wifi_spoof/spoofed_bt_mac
