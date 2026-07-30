package app.wifi.bssid.changer.managers;

import java.util.Random;

public class DrmIdManager {

    public static String generateRandomDrmId() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append(Integer.toHexString(r.nextInt(16)).toUpperCase());
        }
        return sb.toString();
    }

    public static boolean validateDrmId(String drmId) {
        if (drmId == null) return false;
        return drmId.matches("^[0-9A-Fa-f]{8,64}$");
    }
}
