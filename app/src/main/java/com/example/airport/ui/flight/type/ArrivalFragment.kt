package com.example.airport.ui.flight.type

import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.example.airport.R
import com.example.airport.BR
import com.example.airport.base.BaseFragment
import com.example.airport.data.LoadMore
import com.example.airport.databinding.FragmentArrivalBinding
import com.example.airport.ui.flight.FlightViewModel
import com.example.airport.ui.flight.type.binder.FlightInfoItemViewBinder
import com.example.airport.ui.flight.type.binder.LoadMoreItemViewBinder

class ArrivalFragment : BaseFragment<FragmentArrivalBinding, ArrivalViewModel>() {

    // 列表多類型適配器
    private val adapter = MultiTypeAdapter()
    private val arrivalList = ArrayList<Any>()

    private var flightViewModel: FlightViewModel? = null

    override fun getLayoutId() = R.layout.fragment_arrival

    override fun getViewModelId() = BR.arrivalVm

    override fun initView() {
        // 獲取 FlightFragment 的 flightViewModel
        flightViewModel = parentFragment?.let {
            ViewModelProvider(it)[FlightViewModel::class.java]
        }

        // 註冊不同的類型
        adapter.register(FlightInfoItemViewBinder())
        adapter.register(LoadMoreItemViewBinder())
        adapter.items = arrivalList
        binding.rvArrival.layoutManager = LinearLayoutManager(context)
        binding.rvArrival.adapter = adapter
        binding.rvArrival.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (flightViewModel?.arrivalIsLoading?.value == false && totalItemCount <= (lastVisibleItem + 1)) {
                    flightViewModel?.setArrivalLoading(true)
                    removeEndItem()
                    loadMoreData()
                }

            }
        })

        // 觀察 FlightFragment 的 flightViewModel arrivalData 變化
        flightViewModel?.arrivalState?.observe(viewLifecycleOwner, Observer { data ->
            // 是否是全部刷新
            if (data.isRefresh) {
                arrivalList.clear()
                if (data.isSuccess) {
                    if (data.list.isNotEmpty()) {
                        arrivalList.addAll(data.list)
                    }
                    if (data.isEnd) {
                        arrivalList.add(LoadMore(false, getString(R.string.tv_flight_end)))
                    }
                } else {
                    arrivalList.add(LoadMore(false, getString(R.string.tv_flight_error)))
                }
                adapter.notifyDataSetChanged()
                binding.rvArrival.scrollToPosition(0)
            } else {
                // 加載更多
                removeEndItem()

                if (data.isSuccess) {
                    val startIndex = arrivalList.size
                    if (data.list.isNotEmpty()) {
                        arrivalList.addAll(data.list)
                    }
                    if (data.isEnd) {
                        arrivalList.add(LoadMore(false, getString(R.string.tv_flight_end)))
                    }
                    adapter.notifyItemRangeChanged(startIndex, arrivalList.size - startIndex)
                } else {
                    arrivalList.add(LoadMore(false, getString(R.string.tv_flight_error)))
                    adapter.notifyItemInserted(arrivalList.size - 1)
                }
            }
        })

        // 加載完畢後要刪掉剛剛添加的 “正在加载中，请稍等” 數據
        flightViewModel?.arrivalIsLoading?.observe(viewLifecycleOwner) { isLoading ->
            // 控制是否顯示 loading item
            if (!isLoading) {
                removeLoadingItem()
            }
        }
    }

    // 加載更多數據
    private fun loadMoreData() {
        arrivalList.add(LoadMore(true, getString(R.string.tv_flight_loading)))
        adapter.notifyItemInserted(arrivalList.size - 1)
        flightViewModel?.loadArrivalFlights(2, 2, false)
    }

    // 刪除 “加載中” 的提示
    private fun removeLoadingItem() {
        val index = arrivalList.indexOfFirst { it is LoadMore && it.isLoading}
        if (index != -1) {
            arrivalList.removeAt(index)
            adapter.notifyItemRemoved(index)
        }
    }

    // 刪除 “已經到底” 的提示
    private fun removeEndItem() {
        val index = arrivalList.indexOfFirst { it is LoadMore && !it.isLoading }
        if (index != -1) {
            arrivalList.removeAt(index)
            adapter.notifyItemRemoved(index)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.rvArrival.requestFocus()
    }

}