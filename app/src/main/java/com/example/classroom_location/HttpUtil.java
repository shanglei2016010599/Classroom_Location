package com.example.classroom_location;

import java.nio.charset.StandardCharsets;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

class HttpUtil {
    static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }
    static void sendOkHttpRequestByPost(String address, String key, byte[] Body, Callback callback){
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add(key, new String(Body, StandardCharsets.ISO_8859_1))
                .build();

        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
