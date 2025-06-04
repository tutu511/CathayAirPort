package com.example.airport.ui.flight

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.airport.R
import com.example.airport.BR
import com.example.airport.base.BaseFragment
import com.example.airport.databinding.FragmentFlightBinding
import com.example.airport.ui.flight.type.ArrivalFragment
import com.example.airport.ui.flight.type.DepartureFragment
import com.google.android.material.tabs.TabLayoutMediator

// 保留 Fragment 架構 + xml 來畫 UI
class FlightFragment : BaseFragment<FragmentFlightBinding, FlightViewModel>() {

    // 默認是查詢全部的航班【xxx飛往桃園機場】
    private val _airPortID = MutableLiveData<String>("")
    val airPortID: LiveData<String>
        get() = _airPortID

    override fun getLayoutId() = R.layout.fragment_flight
    override fun getViewModelId() = BR.flightVm
    override fun initView() {
        val pagerAdapter = FlightsPagerAdapter(this)
        binding.flightVp.adapter = pagerAdapter

        TabLayoutMediator(binding.typeTab, binding.flightVp) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_flight_departure)
                1 -> getString(R.string.tab_flight_arrival)
                else -> null
            }
        }.attach()

        binding.imgSearch.setOnClickListener {
            _airPortID.value = binding.editSearch.text.toString().trim()
        }
    }
}