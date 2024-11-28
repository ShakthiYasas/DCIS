package org.dcis.cam.invoker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.FileInputStream;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class GenAIInvoker {

    private String audience;
    private static GenAIInvoker instance;

    final String enclosure_prompt = "Write me a notification message in a %s friendly tone " +
            "that the %s being active at this time is only %.1f percent";

    private GenAIInvoker() {}

    public static synchronized GenAIInvoker getInstance() {
        if(instance == null) {
            instance = new GenAIInvoker();
        }
        return instance;
    }

    public void setAudience (String audience) { this.audience = audience; }

    public String generateNotification(String animal, double probability)
            throws IOException, ExecutionException, InterruptedException {
        Properties appProps = new Properties();
        appProps.load(new FileInputStream("api.properties"));

        JSONObject description = new JSONObject();
        description.put("url", appProps.getProperty("opeai_url"));
        description.put("authorization", appProps.getProperty("openai_apikey"));

        JSONObject body = new JSONObject();
        body.put("model", appProps.getProperty("openai_model"));

        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();

        message.put("role", "user");
        message.put("content", String.format(enclosure_prompt, audience, animal, probability));
        messages.put(0, message);

        body.put("messages", messages);
        description.put("body", body);

        CPInvoker invoker = new CPInvoker();
        JSONObject response = new JSONObject(invoker.fetch(description));

        return response.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }

}
