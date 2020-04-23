package com.krnventures.covid_19_2.network

import com.krnventures.covid_19_2.dto.EssentialsListDTO
import com.krnventures.covid_19_2.dto.TravelHistoryListDTO
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

interface ApiInterface {
    @GET("resources/resources.json")
    fun getEssentials(): retrofit2.Call<EssentialsListDTO>

    @GET("travel_history.json")
    fun getTravelHistory(): retrofit2.Call<TravelHistoryListDTO>


    companion object {

        var BASE_URL = "https://api.covid19india.org/"


        fun create(): ApiInterface {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiInterface::class.java)

        }
    }
}