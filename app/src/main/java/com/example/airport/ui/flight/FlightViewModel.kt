package com.example.airport.ui.flight

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.airport.base.BaseViewModel
import com.example.airport.data.FlightInfo
import com.example.airport.data.FlightState
import com.example.airport.request.Repository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FlightViewModel(application: Application) : BaseViewModel(application) {

    private val compositeDisposable = CompositeDisposable()

    // 默認是查詢全部的航班【xxx飛往桃園機場】
    private val _airPortID = MutableLiveData<String>("")
    val airPortID: LiveData<String> = _airPortID

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

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

    private var departureOffset = 0

    private var arrivalOffset = 0

    // 每次加载的条数
    private val limit = 10

    fun loadAllFlightInfo(airFlyLine: Int, airFlyIO: Int) {
        val disposable = Single.zip<List<FlightInfo>, List<FlightInfo>, Pair<List<FlightInfo>, List<FlightInfo>>>(
            getFilteredDepartureFlight(airFlyLine, airFlyIO),
            getFilteredArrivalFlight(airFlyLine, airFlyIO)
        ) { depList, arrList ->
            Pair(depList, arrList)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ (depList, arrList) ->
                departureOffset = 0
                _departureState.value = updateFlightInfo(depList, departureOffset)
                departureOffset += _departureState.value?.list?.size ?: 0
                _departureIsLoading.value = false

                arrivalOffset = 0
                _arrivalState.value = updateFlightInfo(arrList, arrivalOffset)
                arrivalOffset += _arrivalState.value?.list?.size ?: 0
                _arrivalIsLoading.value = false

                _isRefreshing.value = false
            }, { error ->
                Log.e("FlightViewModel", "refreshAllFlightData error", error)
                _departureState.value = FlightState(
                    list = emptyList(),
                    isSuccess = false,
                    isRefresh = true
                )
                _arrivalState.value = FlightState(
                    list = emptyList(),
                    isSuccess = false,
                    isRefresh = true
                )

                _departureIsLoading.value = false
                _arrivalIsLoading.value = false
                _isRefreshing.value = false
            })

        compositeDisposable.add(disposable)
    }

    fun loadDepartureFlights(airFlyLine: Int, airFlyIO: Int, isRefresh: Boolean) {
        val disposable = getFilteredDepartureFlight(airFlyLine, airFlyIO)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ fullList ->
                if (isRefresh) {
                    departureOffset = 0
                }
                _departureState.value = updateFlightInfo(fullList, departureOffset)
                departureOffset += _departureState.value?.list?.size ?: 0
                _departureIsLoading.value = false
            }, { error ->
                Log.e("FlightViewModel", "loadMoreDepartureData error", error)
                _departureState.value = FlightState(
                    list = emptyList(),
                    isSuccess = false,
                    isRefresh = isRefresh
                )
                _departureIsLoading.value = false
            })

        compositeDisposable.add(disposable)
    }

    private fun updateFlightInfo(fullList: List<FlightInfo>, offset: Int) : FlightState{
        // 從 currentSize 開始取 limit 條：drop - 跳過已經顯示的資料數量
        val nextList = fullList.drop(offset).take(limit)
        val isEnd = offset + nextList.size >= fullList.size

        return FlightState(
            list = nextList,
            isEnd = isEnd,
            isRefresh = offset == 0
        )
    }

    fun loadArrivalFlights(airFlyLine: Int, airFlyIO: Int, isRefresh: Boolean) {
        val disposable = getFilteredArrivalFlight(airFlyLine, airFlyIO)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ fullList ->
                // 從 currentSize 開始取 limit 條：drop - 跳過已經顯示的資料數量
                if (isRefresh) {
                    arrivalOffset = 0
                }
                _arrivalState.value = updateFlightInfo(fullList, arrivalOffset)
                arrivalOffset += _arrivalState.value?.list?.size ?: 0
                _arrivalIsLoading.value = false
            }, { error ->
                Log.e("FlightViewModel", "loadMoreDepartureData error", error)
                _arrivalState.value = FlightState(
                    list = emptyList(),
                    isSuccess = false,
                    isRefresh = isRefresh
                )
                _arrivalIsLoading.value = false
            })

        compositeDisposable.add(disposable)
    }

    private fun getFilteredDepartureFlight(airFlyLine: Int, airFlyIO: Int): Single<List<FlightInfo>> {
        return Repository.getFlightInfo(airFlyLine, airFlyIO)
            .subscribeOn(Schedulers.io())
            .map { response ->
                val filtered = if (_airPortID.value.isNullOrBlank()) {
                    response
                } else {
                    response.filter { it.airLineCode == _airPortID.value!!.uppercase() }
                }
                filtered.filter { it.airFlyStatus != "抵達Arrived" }
            }
    }

    private fun getFilteredArrivalFlight(airFlyLine: Int, airFlyIO: Int): Single<List<FlightInfo>> {
        return  Repository.getFlightInfo(airFlyLine, airFlyIO)
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

    fun setIsRefreshing(isRefreshing: Boolean) {
        _isRefreshing.value = isRefreshing
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}