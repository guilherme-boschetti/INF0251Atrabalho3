package com.example.inf0251atrabalho3.apiservice;

import com.example.inf0251atrabalho3.repository.Currency;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IApiService {

    @GET("json/{currency}")
    Call<List<Currency>> getCurrency(@Path("currency") String currency);
}
