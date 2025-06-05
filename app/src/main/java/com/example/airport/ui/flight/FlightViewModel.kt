package com.example.airport.ui.flight

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.airport.base.BaseViewModel
import com.example.airport.data.FlightState
import com.example.airport.request.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FlightViewModel(application: Application) : BaseViewModel(application) {

    private val compositeDisposable = CompositeDisposable()

    // 默認是查詢全部的航班【xxx飛往桃園機場】
    private val _airPortID = MutableLiveData<String>("")
    val airPortID: LiveData<String> = _airPortID

    // 抵達
    private val _arrivalState = MutableLiveData<FlightState>()
    val arrivalState: LiveData<FlightState> = _arrivalState

    // 起飛： departureList + IsEnd（數據是否到底）
    private val _departureState = MutableLiveData<FlightState>()
    val departureState: LiveData<FlightState> = _departureState

    // Departure 狀態 ：加載中
    private val _departureIsLoading = MutableLiveData(false)
    val departureIsLoading: LiveData<Boolean> = _departureIsLoading

    // Arrival 狀態 ： 加載中
    private val _arrivalIsLoading = MutableLiveData(false)
    val arrivalIsLoading: LiveData<Boolean> = _arrivalIsLoading

    // 每次加载的条数
    private val limit = 10

    fun loadFlightInfo(airFlyLine: Int, airFlyIO: Int) {
        loadDepartureFlights(airFlyLine, airFlyIO)
        loadArrivalFlights(airFlyLine, airFlyIO)
    }

    fun loadDepartureFlights(airFlyLine: Int, airFlyIO: Int) {
        val currentSize = _departureState.value?.list?.size ?: 0
        val currentList = _departureState.value?.list ?: emptyList()
        val disposable = Repository.getFlightInfo(airFlyLine, airFlyIO)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                val filtered = if (_airPortID.value.isNullOrBlank()) {
                    response
                } else {
                    response.filter { it.airLineCode == _airPortID.value!!.uppercase() }
                }
                filtered.filter { it.airFlyStatus != "抵達Arrived" }
            }
            .subscribe({ fullList ->
                // 從 currentSize 開始取 limit 條：drop - 跳過已經顯示的資料數量
                val nextList = fullList.drop(currentSize).take(limit)
                val updatedList = currentList + nextList
                val isEnd = updatedList.size >= fullList.size

                _departureState.value = FlightState(
                    list = updatedList,
                    isEnd = isEnd
                )
                _departureIsLoading.value = false
            }, { error ->
                Log.e("FlightViewModel", "loadMoreDepartureData error", error)
                _departureIsLoading.value = false
            })

        compositeDisposable.add(disposable)
    }

    fun loadArrivalFlights(airFlyLine: Int, airFlyIO: Int) {
        val currentSize = _arrivalState.value?.list?.size ?: 0
        val currentList = _arrivalState.value?.list ?: emptyList()
        val disposable = Repository.getFlightInfo(airFlyLine, airFlyIO)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                val filtered = if (_airPortID.value.isNullOrBlank()) {
                    response
                } else {
                    response.filter { it.airLineCode == _airPortID.value!!.uppercase() }
                }
                filtered.filter { it.airFlyStatus == "抵達Arrived" }
            }
            .subscribe({ fullList ->
                // 從 currentSize 開始取 limit 條：drop - 跳過已經顯示的資料數量
                val nextList = fullList.drop(currentSize).take(limit)
                val updatedList = currentList + nextList
                val isEnd = updatedList.size >= fullList.size

                _arrivalState.value = FlightState(
                    list = updatedList,
                    isEnd = isEnd
                )
                _arrivalIsLoading.value = false
            }, { error ->
                Log.e("FlightViewModel", "loadMoreDepartureData error", error)
                _arrivalIsLoading.value = false
            })

        compositeDisposable.add(disposable)
    }

    fun clearFlightInfo() {
        _arrivalState.value = FlightState()
        _departureState.value = FlightState()
    }

    fun setLoading(isLoading: Boolean) {
        _departureIsLoading.value = isLoading
        _arrivalIsLoading.value = isLoading
    }

    fun setDepartureLoading(isLoading: Boolean) {
        _departureIsLoading.value = isLoading
    }

    fun setArrivalLoading(isLoading: Boolean) {
        _arrivalIsLoading.value = isLoading
    }

    fun setAirPortID(airPortID: String) {
        _airPortID.value = airPortID
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}