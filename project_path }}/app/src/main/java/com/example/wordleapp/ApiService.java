package com.example.wordleapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("word")
    Call<String> getWordOfTheDay(@Query("api_key") String apiKey);
}