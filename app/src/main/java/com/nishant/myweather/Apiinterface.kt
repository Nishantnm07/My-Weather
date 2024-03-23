package com.nishant.myweather

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// ye sabh API interface ka kam hogya hai ya pr
interface Apiinterface {
 @GET("weather")
 fun getWeatherData(
     @Query("q") city:String,
     @Query("appid") appid :String,
     @Query("units") units :String
 ) : Call<MyWeather>
}