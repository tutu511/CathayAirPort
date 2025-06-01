package com.example.airport.ui.flight.type

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.airport.base.BaseViewModel
import com.example.airport.data.FlightInfo
import com.example.airport.request.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ArrivalViewModel(application: Application) : BaseViewModel(application) {

    private val _isSearching = MutableLiveData<Boolean>()
    val isSearching: LiveData<Boolean> get() = _isSearching

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isEnd = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isEnd: LiveData<Boolean> get() = _isEnd

    private val _flightInfoList = MutableLiveData<List<FlightInfo>>()
    val flightInfoList: LiveData<List<FlightInfo>> get() = _flightInfoList

    private val compositeDisposable = CompositeDisposable()

    // 每次加载的条数
    private val limit = 10

    fun loadFlightInfo(flyType: String, airPortID: String) {
        val disposable = Repository.getFlightInfo(flyType, airPortID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    _isSearching.value = true
                    response?.let { appendData(it) }
                    _isLoading.value = false
                },
                { error ->
                    Log.e("ApiError", "Error occurred while fetching flight info")
                    _isLoading.value = false
                }
            )
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    private fun appendData(newData: List<FlightInfo>) {
        val currentList = _flightInfoList.value?.toMutableList() ?: mutableListOf()
        val startIndex = currentList.size
        val endIndex = startIndex + limit

        if (startIndex < newData.size) {
            val resultToAdd = newData.subList(startIndex, minOf(endIndex, newData.size))
            currentList.addAll(resultToAdd)
            _flightInfoList.value = currentList
        }
        _isEnd.value = currentList.size >= newData.size
    }

    fun clearFlightInfo() {
        _flightInfoList.value = emptyList()
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}

