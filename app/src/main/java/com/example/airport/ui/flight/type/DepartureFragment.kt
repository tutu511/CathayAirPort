package com.example.airport.ui.flight.type

import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.example.airport.BR
import com.example.airport.R
import com.example.airport.base.BaseFragment
import com.example.airport.data.LoadMore
import com.example.airport.databinding.FragmentDepartureBinding
import com.example.airport.ui.flight.FlightFragment
import com.example.airport.ui.flight.type.binder.FlightInfoItemViewBinder
import com.example.airport.ui.flight.type.binder.LoadMoreItemViewBinder

class DepartureFragment : BaseFragment<FragmentDepartureBinding, DepartureViewModel>() {

    // 列表多類型適配器
    private val adapter = MultiTypeAdapter()
    private val arrivalList = ArrayList<Any>()
    private val type = "D"
    private var airPortID = "TPE"

    override fun getLayoutId() = R.layout.fragment_departure

    override fun getViewModelId() = BR.departureVm

    override fun initView() {
        // 觀察 FlightFragment 的 airPortID 變化
        val parentFragment = parentFragment
        if (parentFragment is FlightFragment) {
            parentFragment.airPortID.observe(viewLifecycleOwner, Observer { newAirPortID ->
                airPortID = newAirPortID
                arrivalList.clear()
                viewModel.clearFlightInfo()
                getAirPortFlyAPI()
            })
        }

        // 註冊不同的類型
        adapter.register(FlightInfoItemViewBinder())
        adapter.register(LoadMoreItemViewBinder())
        adapter.items = arrivalList
        binding.rvDeparture.layoutManager = LinearLayoutManager(context)
        binding.rvDeparture.adapter = adapter
        binding.rvDeparture.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (viewModel.isLoading.value == false && totalItemCount <= (lastVisibleItem + 1)) {
                    viewModel.setLoading(true)
                    loadMoreData()
                }

            }
        })

        // 设置下拉刷新监听
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (viewModel.isLoading.value == true) {
                Toast.makeText(context, "正在加載數據，等數據加載完再進行刷新", Toast.LENGTH_SHORT).show()
                binding.swipeRefreshLayout.isRefreshing = false
            } else {
                refreshData()
            }
        }

        // 觀察 flightInfoList 的變化，需要更新列表
        viewModel.flightInfoList.observe(viewLifecycleOwner, Observer { newItems ->
            if (newItems.isNotEmpty()) {
                val loadMoreIndex = arrivalList.indexOfFirst { it is LoadMore }
                if (loadMoreIndex != -1) {
                    arrivalList.removeAll { it is LoadMore }
                    adapter.notifyItemRemoved(loadMoreIndex)
                }
                binding.swipeRefreshLayout.isRefreshing = false
                arrivalList.addAll(newItems.subList(arrivalList.size, newItems.size))
                adapter.notifyDataSetChanged()
            }

        })

        // 觀察 isLoading 的值 -- 數據是否還在加載
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            // 如果已經滑到最底了，就不会更新数据了，这时候就要自己观察 isLoading 数据，进行删除
            if (!isLoading) {
                val loadMoreIndex = arrivalList.indexOfFirst { it is LoadMore }
                if (loadMoreIndex != -1) {
                    arrivalList.removeAt(loadMoreIndex)
                    adapter.notifyItemRemoved(loadMoreIndex)
                }
            }
        })

        // 觀察 isEnd 的值 -- 數據是否已經到底了
        viewModel.isEnd.observe(viewLifecycleOwner, Observer { isEnd ->
            if (isEnd) {
                addEndOfListMessage()
            }
        })

        // 第一次获取数据  - 测试代码
        getAirPortFlyAPI()
    }

    private fun getAirPortFlyAPI() {
        viewModel.loadFlightInfo(type, airPortID)
    }

    // 加載更多數據
    private fun loadMoreData() {
        arrivalList.add(LoadMore(true, "正在加载中，请稍等"))
        adapter.notifyItemInserted(arrivalList.size - 1)
        getAirPortFlyAPI()
    }

    // 停止加載，假設已經滑到底了
    private fun addEndOfListMessage() {
        arrivalList.add(LoadMore(false, "已經滑到最底了 >_<"))
        adapter.notifyItemInserted(arrivalList.size - 1)
    }

    private fun refreshData() {
        // 刷新数据，重新获取最新的数据
        binding.swipeRefreshLayout.isRefreshing = true
        arrivalList.clear()
        viewModel.clearFlightInfo()
        getAirPortFlyAPI()
    }

}