package org.dcis.csm.handler;

import okhttp3.*;
import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;

import org.dcis.csm.proto.CSMRequest;

public class CloudHandler {
    public int fetch(CSMRequest.TYPE type, String data)
            throws IOException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);

        OkHttpClient client = builder.build();
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                data);

        Properties appProps = new Properties();
        appProps.load(new FileInputStream("pubsub.properties"));

        String url;
        if(type == CSMRequest.TYPE.BACKUP)
            url = appProps.getProperty("backup");
        else
            url = appProps.getProperty("logs");

        Request.Builder request = new Request.Builder()
                .url(url).post(body);

        Call call = client.newCall(request.build());
        Response response = call.execute();

        return response.code();
    }
}


