package com.example.airport.request.api

import com.example.airport.request.Response.BaseResponse
import com.example.airport.request.Response.FlightInfoResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(ApiAddress.FLIGHT_INFO)
    fun getFlightInfo(
        @Query("AirFlyLine") airFlyLine: Int? = 2,
        @Query("AirFlyIO") airFlyIO: Int? = 2,
    ): Single<FlightInfoResponse>


    // 匯率API：取得指定幣別的匯率資料
    @GET(ApiAddress.RATE_INFO)
    fun getLatestRates(
        @Query("apikey") apiKey: String,
        @Query("base_currency") baseCurrency: String? = null,
        @Query("currencies") currencies: String? = null
    ): Single<BaseResponse<Map<String, Double>>>
}