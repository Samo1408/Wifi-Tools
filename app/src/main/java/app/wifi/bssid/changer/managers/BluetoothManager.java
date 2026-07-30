package app.wifi.bssid.changer.managers;

import java.util.Random;

public class BluetoothManager {

    public static String generateRandomMac() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            if (i > 0) sb.append(":");
            sb.append(String.format("%02X", r.nextInt(256)));
        }
        return sb.toString();
    }

    public static boolean validateMac(String mac) {
        if (mac == null) return false;
        String regex6 = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
        String regex8 = "^([0-9A-Fa-f]{2}[:-]){7}([0-9A-Fa-f]{2})$";
        return mac.matches(regex6) || mac.matches(regex8);
    }
}
