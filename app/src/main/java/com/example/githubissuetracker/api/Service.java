package com.example.githubissuetracker.api;

import com.example.githubissuetracker.model.Item;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Service {
    @GET("/repos/ashutosh1919/react-awesome-loaders/issues")
//    Call<ItemResponse> getItems();
    Call<List<Item>> getItems();

    @GET("/repos/{user}/{repo}/issues")
    Call<List<Item>> getDynamicItems(@Path("user") String user, @Path("repo") String repo);
}
