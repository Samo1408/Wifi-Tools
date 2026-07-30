package app.wifi.bssid.changer.managers;

import java.util.Random;

public class BssidManager {

    public static String generateRandomBssid() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            if (i > 0) sb.append(":");
            sb.append(String.format("%02X", r.nextInt(256)));
        }
        return sb.toString();
    }

    public static boolean validateBssid(String bssid) {
        if (bssid == null) return false;
        String regex6 = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
        String regex8 = "^([0-9A-Fa-f]{2}[:-]){7}([0-9A-Fa-f]{2})$";
        return bssid.matches(regex6) || bssid.matches(regex8);
    }
}
