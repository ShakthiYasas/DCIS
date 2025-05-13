package org.dcis.cam.invoker;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.io.FileInputStream;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;

public class CPInvoker {

    // Fetches from the Cloud or the context provider.
    // tag: Enclosure identifier tag.
    // returns: JSON string containing the context information.
    public String fetch(String tag)
            throws IOException, ExecutionException, InterruptedException {

        Properties appProps = new Properties();
        appProps.load(new FileInputStream("api.properties"));

        JSONObject description = new JSONObject();
        description.put("protocol", "GET");

        if(tag.equals("weather")) {
            String url = appProps.getProperty("weather");
            description.put("url", url);
            return fetch(description);
        }
        else {
            String url = appProps.getProperty("enclosure") + tag;
            description.put("url", url);
            return fetch(description);
        }
    }

    // Fetches backed up context from the Cloud.
    // tag: Enclosure identifier tag.
    // returns: JSON containing the context information.
    public String fetchBackup(String tag)
            throws IOException, ExecutionException, InterruptedException {

        Properties appProps = new Properties();
        appProps.load(new FileInputStream("api.properties"));

        JSONObject description = new JSONObject();
        description.put("protocol", "GET");
        String url = appProps.getProperty("backedup_context") + tag;
        description.put("url", url);
        return fetch(description);
    }

    // Fetches the nessecary situation models from the Cloud.
    // identifier: Name of the situation.
    // returns: Situation model in JSON format.
    public String fetchSituation(String identifier)
            throws IOException, ExecutionException, InterruptedException {

        Properties appProps = new Properties();
        appProps.load(new FileInputStream("api.properties"));

        JSONObject description = new JSONObject();
        description.put("protocol", "GET");
        String url = appProps.getProperty("situation_definition") + identifier;
        description.put("url", url);
        return fetch(description);
    }

    // Fetches from a given context provider.
    // description: Metadata about the provider.
    // returns: JSON object containing metadata regarding the BLE context provider.
    public String fetch(JSONObject description)
            throws ExecutionException, InterruptedException, IOException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);

        OkHttpClient client = builder.build();
        ResponseFuture fu_res = new ResponseFuture();

        Request.Builder request = null;
        switch(description.getString("protocol")) {
            case "GET" -> {
                request = new Request.Builder()
                        .url(description.getString("url"))
                        .get();
            }
            case "POST" -> {
                RequestBody body = RequestBody.create(
                        MediaType.parse("application/json; charset=utf-8"),
                        description.getString("body"));

                request = new Request.Builder()
                        .url(description.getString("url"))
                        .post(body);

                if(description.has("authorization")) {
                    request.addHeader("Authorization",
                            "Bearer " + description.getString("authorization"));
                }
            }
        }

        Call call = client.newCall(request.build());
        call.enqueue(fu_res);
        Response response = fu_res.future.get();

        if(response.isSuccessful())
            return response.body().string().trim();
        return null;
    }
}
