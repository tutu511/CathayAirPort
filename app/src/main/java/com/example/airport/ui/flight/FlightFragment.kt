package com.example.airport.ui.flight

import android.widget.Toast
import com.example.airport.R
import com.example.airport.BR
import com.example.airport.base.BaseFragment
import com.example.airport.databinding.FragmentFlightBinding
import com.google.android.material.tabs.TabLayoutMediator

// 保留 Fragment 架構 + xml 來畫 UI
class FlightFragment : BaseFragment<FragmentFlightBinding, FlightViewModel>() {

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

        // 點擊搜索按鈕，進行刷新數據（起飛/抵達）
        binding.imgSearch.setOnClickListener {
            viewModel.setAirPortID(binding.editSearch.text.toString().trim())
            // 如果有數據正在加載中，那就彈 toast 提示客戶
            if (viewModel.departureIsLoading.value == true || viewModel.arrivalIsLoading.value == true) {
                Toast.makeText(context, "正在加載數據，等數據加載完再進行搜索", Toast.LENGTH_SHORT).show()
            } else {
                // 刷新數據
                refreshData()
            }
        }

        // SwipeRefreshLayout 下拉刷新事件
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (viewModel.departureIsLoading.value == true || viewModel.arrivalIsLoading.value == true) {
                Toast.makeText(context, "正在加載數據，等數據加載完再進行刷新", Toast.LENGTH_SHORT).show()
                binding.swipeRefreshLayout.isRefreshing = false
            } else {
                binding.swipeRefreshLayout.isRefreshing = true
                refreshData()
            }
        }

        // 監聽載入狀態 控制刷新動畫
        viewModel.departureIsLoading.observe(viewLifecycleOwner) { isLoading ->
            updateLoadingState()
        }
        viewModel.arrivalIsLoading.observe(viewLifecycleOwner) { isLoading ->
            updateLoadingState()
        }

        refreshData()
    }

    private fun updateLoadingState() {
        val isDepartureLoading = viewModel.departureIsLoading.value ?: false
        val isArrivalLoading = viewModel.arrivalIsLoading.value ?: false
        binding.swipeRefreshLayout.isRefreshing = isDepartureLoading && isArrivalLoading
    }

    private fun getAirPortFlyAPI() {
        // 固定：airFlyLine = 2, airFlyIO = 2；
        // 查詢的機場：airPortID
        viewModel.loadFlightInfo(2, 2)
    }

    // 刷新数据，重新获取最新的数据
    private fun refreshData() {
        // 開始加載中
        viewModel.setLoading(true)
        // 將之前的的數據都清除
        viewModel.clearFlightInfo()
        // 獲取 api 機場資訊
        getAirPortFlyAPI()
    }
}