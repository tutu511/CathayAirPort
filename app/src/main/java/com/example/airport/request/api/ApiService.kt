package com.example.airport.request.api

import com.example.airport.data.FlightInfo
import com.example.airport.request.BaseResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET(ApiAddress.FLIGHT_INFO + "{flyType}/{airPortID}")
    fun getFlightInfo(
        @Path("flyType") flyType: String,
        @Path("airPortID") airPortID: String
    ): Single<List<FlightInfo>>


    // 匯率API：取得指定幣別的匯率資料
    @GET("v1/latest")
    fun getLatestRates(
        @Query("apikey") apiKey: String,
        @Query("base_currency") baseCurrency: String? = null,
        @Query("currencies") currencies: String? = null
    ): Single<BaseResponse<Map<String, Double>>>
}