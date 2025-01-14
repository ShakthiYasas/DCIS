package org.dcis;

import org.dcis.services.AcquisitionServices;
import org.dcis.services.EventServices;
import org.json.JSONObject;

public class ContextCordinator {
    /**
     * A generic method to verify the integration with the developing system.
     * @return A generic message indicating the integration is successful.
     */
    public static String health()  {
        return "Ping from the Context Coordinator.";
    }

    /** Initiates the mobile edge device.
     * @param   config
     *          Visitor configurations and the animal preferences.
     * @return  References to the mobile edge resources.
     **/
    public static JSONObject setupEdge(JSONObject config) {
        return new JSONObject();
    }

    /** Sets visitor's location data into the Siddhi LocStream.
     * @param   location
     *          location of the visitor.
     * @return  None.
     **/
    public static void setLocation(JSONObject location) {
        EventServices.addSiddhiEvent("location", location);
    }

    /** Sets visitor's biometric data and inferred context into the Siddhi streams.
     * @param   biometrics
     *          Temporal biometric data of the visitor.
     * @return  None.
     **/
    public static void setHealth(JSONObject biometrics) {
        EventServices.addSiddhiEvent("health", biometrics);
    }

    /** Indicates to the backend to pre-fetch the metadata related to the enclosure.
     * @param   tag
     *          Tag of the enclosure.
     * @return  A context aware-message about the nearing enclosure.
     **/
    public static JSONObject prefetchMetaData(String tag) {
        return AcquisitionServices.nearingEnclosure(tag);
    }

    /** Verifies the hashkey shared by the BLE with the metadata prefetched from the server.
     * @param   hashkey
     *          The hashkey shared by the BLE.
     * @return  A session key if authorised or an error message.
     **/
    public static JSONObject verifyBluetoothSender(String hashkey) {
        return AcquisitionServices.verifyBLE(hashkey);
    }

    /** Sets the context of the animal shared by the BLE of the enclosure.
     * @param   context
     *          Context of the animal shared by the BLE.
     * @return  None.
     */
    public static void setAnimalContext(JSONObject context) {
        AcquisitionServices.shareWithBackEnd("animal", context);
    }

    public static JSONObject getRecommendation() { return new JSONObject(); }
}