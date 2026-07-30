package app.wifi.bssid.changer.managers;

import java.util.Random;

public class SystemIdManager {

    public static String generateRandomSystemId() {
        Random r = new Random();
        int systemId = r.nextInt(9000) + 1000;
        return String.valueOf(systemId);
    }

    public static boolean validateSystemId(String systemId) {
        if (systemId == null) return false;
        try {
            int id = Integer.parseInt(systemId);
            return id >= 0 && id <= 99999;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
