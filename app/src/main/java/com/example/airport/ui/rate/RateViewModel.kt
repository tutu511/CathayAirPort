package com.example.airport.ui.rate

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.airport.BuildConfig
import com.example.airport.R
import com.example.airport.data.LoadMore
import com.example.airport.request.Repository
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RateViewModel(application: Application) : AndroidViewModel(application) {

    // 匯率 Map，例如 {"USD" to 1.0, "JPY" to 155.32}
    private val _rateList = MutableLiveData<Map<String, Double>>()
    val rateList: LiveData<Map<String, Double>> = _rateList

    // 使用者輸入的金額，預設為 1
    private val _baseAmount = MutableLiveData(1.0)
    val baseAmount: LiveData<Double> = _baseAmount

    // 計算匯率的主幣別
    private val _baseCurrency = MutableLiveData("USD")
    val baseCurrency: LiveData<String> = _baseCurrency

    // 数据：全部幣別 Currency List：https://freecurrencyapi.com/docs/currency-list
    private val allCurrencies: String = "EUR,USD,JPY,BGN,CZK,DKK,GBP,HUF,PLN,RON,SEK,CHF,ISK,NOK,HRK,RUB,TRY,AUD,BRL,CAD,CNY,HKD,IDR,ILS,INR,KRW,MXN,MYR,NZD,PHP,SGD,THB,ZAR"
    // 数据：顯示的六種幣別
    private val _selectedCurrencies = MutableLiveData<List<String>>(listOf("EUR", "JPY", "GBP", "AUD", "CNY", "HKD"))
    val selectedCurrencies: LiveData<List<String>> = _selectedCurrencies

    private val compositeDisposable = CompositeDisposable()
    private val context = getApplication<Application>()

    // 錯誤頁重新獲取匯率數據是
    private val _loadMoreState = MutableLiveData(LoadMore())
    val loadMoreState: LiveData<LoadMore> = _loadMoreState


    // 設定輸入金額
    fun setBaseAmount(amount: Double) {
        _baseAmount.value = amount
    }

    // 更新匯率資料
    fun updateRates(newRates: Map<String, Double>) {
        _rateList.value = newRates
    }

    // 更新主幣別
    fun updateBaseCurrency(currency: String) {
        _baseCurrency.value = currency
    }

    // 更新六種幣別
    fun updateSelectedCurrencies(currencyList: List<String>) {
        _selectedCurrencies.value = currencyList
    }

    fun fetchAllRates() {
        // 顯示加載中
        _loadMoreState.value = LoadMore(isLoading = true, message = null)

        val disposable = Repository
            .getAllExchangeRates(
                apiKey = BuildConfig.API_KEY,
                baseCurrency = baseCurrency.value,
                currencies = allCurrencies)
            .timeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (!response.getData().isNullOrEmpty()) {
                    response.getData()?.let {
                        updateRates(it)
                    }
                    Log.d("RateViewModel", "RateViewModel response: ${Gson().toJson(response.getData())}")
                    _loadMoreState.value = LoadMore(isLoading = false, message = null)
                } else {
                    Log.d("RateViewModel", "RateViewModel response data is empty")
                    val errorMsg = context.getString(R.string.tv_rate_empty_error_msg)
                    _loadMoreState.value = LoadMore(isLoading = false, message = errorMsg)
                }
            }, { error ->
                // 顯示錯誤訊息
                Log.d("RateViewModel", "RateViewModel response : error = " + error.message)
                val errorMsg = context.getString(R.string.tv_rate_default_error_msg)
                _loadMoreState.value = LoadMore(isLoading = false, message = errorMsg)
            })
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}