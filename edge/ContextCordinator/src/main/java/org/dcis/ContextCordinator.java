package org.dcis;

import org.json.JSONObject;

public class ContextCordinator {
    public static JSONObject setLocation() { return new JSONObject(); }
    public static JSONObject setHealth() { return new JSONObject(); }
    public static JSONObject verifyBluetoothSender() { return new JSONObject(); }
    public static JSONObject getRecommendation() { return new JSONObject(); }

    public static String health()  {
        return "Ping from the library.";
    }
}