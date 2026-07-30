#!/system/bin/sh
# Runs in late_start service mode

MODDIR=${0%/*}

# Keep config file permissions correct
while true; do
    if [ -f "/data/adb/wifi_spoof/config.properties" ]; then
        chmod 644 /data/adb/wifi_spoof/config.properties
    fi
    sleep 30
done
