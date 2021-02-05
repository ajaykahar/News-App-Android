package com.ajay.mynewsapp.apis;

import com.ajay.mynewsapp.model.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {

    // before paging, used this method
//    @GET("top-headlines")
//    Call<News> getNews(@Query("language") String lan,
//                       @Query("apiKey") String apiKey);


    @GET("top-headlines")
    Call<News> getNews(@Query("language") String lan,
                       @Query("page") int pageNumber,
                       @Query("pageSize") int pageSize,
                       @Query("apiKey") String apiKey);





    @GET("everything")
    Call<News> getSearchedNews(@Query("q") String searchString,
                               @Query("sortBy") String sortBy,
                               @Query("apiKey") String apiKey);

}
