package com.example.airport.request

import android.util.Log
import com.example.airport.data.FlightInfo
import com.example.airport.request.NetworkModule.airPortApiService
import com.example.airport.request.NetworkModule.exchangeRateApiService
import com.example.airport.request.Response.BaseResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object Repository {

    /**
     * 获取机场飞行数据
     */
    fun getFlightInfo(airFlyLine: Int, airFlyIO: Int): Single<List<FlightInfo>> {
        return airPortApiService.getFlightInfo(airFlyLine, airFlyIO)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response -> response.instantSchedule }
            .doOnError { e -> handleError(e) }
    }

    /**
     * 獲取匯率數據
     */
    fun getAllExchangeRates(
        apiKey: String,
        baseCurrency: String? = null,
        currencies: String? = null
    ): Single<BaseResponse<Map<String, Double>>> {
        return exchangeRateApiService.getLatestRates(apiKey, baseCurrency, currencies)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e -> handleError(e) }
    }


    private fun handleError(e: Throwable) {
        Log.e("ApiError", "Error occurred: ${e.message}")
    }

}