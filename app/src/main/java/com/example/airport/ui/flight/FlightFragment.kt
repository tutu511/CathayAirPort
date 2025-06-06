package com.example.airport.ui.flight

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.airport.R
import com.example.airport.BR
import com.example.airport.base.BaseFragment
import com.example.airport.databinding.FragmentFlightBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            // 收起鍵盤
            hideKeyboard()
            // 清除 EditText 焦點
            binding.editSearch.clearFocus()
            // 刷新數據
            refreshData()
        }

        binding.editSearch.setOnEditorActionListener { _, actionId, event ->
            val isEnterPressed = (actionId == EditorInfo.IME_ACTION_SEARCH)
                    || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            if (isEnterPressed) {
                viewModel.setAirPortID(binding.editSearch.text.toString().trim())
                // 收起鍵盤
                hideKeyboard()
                // 清除 EditText 焦點
                binding.editSearch.clearFocus()
                // 刷新數據
                refreshData()
                true
            } else {
                false
            }
        }

        // SwipeRefreshLayout 下拉刷新事件
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

        // 監聽載入狀態 控制刷新動畫
        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (true) {
                    refreshData()
                    delay(10000)
                }
            }
        }
    }

    private fun getAirPortFlyAPI() {
        // 固定：airFlyLine = 2, airFlyIO = 2；
        // 查詢的機場：airPortID
        viewModel.loadAllFlightInfo(2, 2)
    }

    // 刷新数据，重新获取最新的数据
    private fun refreshData() {
        // 如果有數據正在加載中，那就彈 toast 提示客戶
        if (viewModel.departureIsLoading.value == true || viewModel.arrivalIsLoading.value == true) {
            Toast.makeText(context, "正在加載數據，等數據加載完再進行搜索", Toast.LENGTH_SHORT).show()
            viewModel.setIsRefreshing(false)
        } else {
            // 顯示刷新的動畫
            viewModel.setIsRefreshing(true)
            // 清除舊數據
            viewModel.clearFlightInfo()
            // 開始加載中
            viewModel.setLoading(true)
            // 獲取 api 機場資訊
            getAirPortFlyAPI()
        }

    }

    private fun hideKeyboard() {
        // 取得輸入法服務
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val view = activity?.currentFocus ?: View(context)
        // 透過 windowToken 關閉鍵盤
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}