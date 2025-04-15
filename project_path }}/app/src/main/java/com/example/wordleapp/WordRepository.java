package com.example.wordleapp;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WordRepository {

    private static final String API_URL = "https://api.example.com/word?api_key=YOUR_API_KEY";
    private OkHttpClient client = new OkHttpClient();

    public void fetchWordOfTheDay(Callback callback) {
        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        client.newCall(request).enqueue(callback);
    }
}