package org.dcis.cam.invoker;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CPInvoker {

    // Fetches from the Cloud.
    // tag: Enclosure identifier tag.
    public String fetch(String tag)
            throws IOException, ExecutionException, InterruptedException {
        JSONObject description = new JSONObject();
        String url = "http://localhost:5000/enclosures/" + tag;
        description.put("url", url);

        return fetch(description);
    }

    // Fetches from a given context provider.
    // description: Metadata about the provider.
    public String fetch(JSONObject description)
            throws ExecutionException, InterruptedException, IOException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);

        OkHttpClient client = builder.build();
        ResponseFuture fu_res = new ResponseFuture();

        Request request = null;
        switch(description.getString("protocol")) {
            case "GET" -> {
                request = new Request.Builder()
                        .url(description.getString("url"))
                        .get()
                        .build();
            }
            case "POST" -> {
                RequestBody body = RequestBody.create(
                        MediaType.parse("application/json; charset=utf-8"),
                        description.getString("body"));

                request = new Request.Builder()
                        .url(description.getString("url"))
                        .post(body)
                        .build();
            }
        }

        Call call = client.newCall(request);
        call.enqueue(fu_res);
        Response response = fu_res.future.get();

        if(response.isSuccessful())
            return response.body().string().trim();
        return null;
    }

}
