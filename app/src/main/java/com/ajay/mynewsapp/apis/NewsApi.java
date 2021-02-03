package com.ajay.mynewsapp.apis;

import com.ajay.mynewsapp.model.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {
    @GET("top-headlines")
    Call<News> getNews(@Query("language") String lan,
                       @Query("apiKey") String apiKey);

}
