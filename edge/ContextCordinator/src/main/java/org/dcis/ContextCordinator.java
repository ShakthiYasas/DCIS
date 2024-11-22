package org.dcis;

import org.dcis.services.EventServices;
import org.json.JSONObject;

public class ContextCordinator {
    // Initiates the mobile edge device.
    // Accepts: Visitor configurations and the animal preferences.
    // Returns: References to the mobile edge resources.
    public static JSONObject setupEdge() { return new JSONObject();}

    // Sets visitor's location data into the Siddhi LocStream.
    // Accepts: Temporal location of the visitor.
    // Returns: None.
    public static void setLocation(JSONObject location) {
        EventServices.addSiddhiEvent("location", location);
    }

    // Sets visitor's biometric data and inferred context into the Siddhi streams.
    // Accepts: Temporal biometric data of the visitor.
    // Returns: None.
    public static void setHealth(JSONObject biometrics) {
        EventServices.addSiddhiEvent("health", biometrics);
    }

    public static JSONObject verifyBluetoothSender() { return new JSONObject(); }
    public static JSONObject getRecommendation() { return new JSONObject(); }

    public static String health()  {
        return "Ping from the Context Coordinator.";
    }
}