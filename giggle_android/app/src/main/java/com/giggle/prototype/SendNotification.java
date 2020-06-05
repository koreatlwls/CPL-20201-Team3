package com.giggle.prototype;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendNotification {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void sendNotification(final String regToken, final String title, final String messsage, final String shopname, final String shopposition) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... parms) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    JSONObject data = new JSONObject();
                    data.put("shopname",shopname);
                    data.put("shopposition",shopposition);
                    dataJson.put("body", messsage);
                    dataJson.put("title", title);
                    json.put("data",data);
                    json.put("notification", dataJson);
                    json.put("to", regToken);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=" + "AAAAHmDlvQw:APA91bF2NAojaKQdkxpsRJiuuOUG6EP8XIdJ60EIjBxhx-gQLllYXAiY_Iyt0eQFXKXlSrDGFyqwnFD6579mST2Joy-U1Y1-YuHE_ojKB2GUKQclv51SzozhoKG6V6S65qXF6n6Xxvrh")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                } catch (Exception e) {
                    Log.d("error", e + "");
                }
                return null;
            }
        }.execute();
    }
}
