package com.example.airport.ui.flight.type

import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.example.airport.BR
import com.example.airport.R
import com.example.airport.base.BaseFragment
import com.example.airport.data.LoadMore
import com.example.airport.databinding.FragmentDepartureBinding
import com.example.airport.ui.flight.FlightViewModel
import com.example.airport.ui.flight.type.binder.FlightInfoItemViewBinder
import com.example.airport.ui.flight.type.binder.LoadMoreItemViewBinder

class DepartureFragment : BaseFragment<FragmentDepartureBinding, DepartureViewModel>() {

    // 列表多類型適配器
    private val adapter = MultiTypeAdapter()
    private val departureList = ArrayList<Any>()

    private var flightViewModel: FlightViewModel? = null

    override fun getLayoutId() = R.layout.fragment_departure

    override fun getViewModelId() = BR.departureVm

    override fun initView() {
        // 獲取 FlightFragment 的 flightViewModel
        flightViewModel = parentFragment?.let {
            ViewModelProvider(it)[FlightViewModel::class.java]
        }

        // 註冊不同的類型
        adapter.register(FlightInfoItemViewBinder())
        adapter.register(LoadMoreItemViewBinder())
        adapter.items = departureList
        binding.rvDeparture.layoutManager = LinearLayoutManager(context)
        binding.rvDeparture.adapter = adapter
        binding.rvDeparture.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (flightViewModel?.departureIsLoading?.value == false && totalItemCount <= (lastVisibleItem + 1)) {
                    flightViewModel?.setDepartureLoading(true)
                    loadMoreData()
                }
            }
        })


        // 觀察 FlightFragment 的 flightViewModel departureData 變化
        flightViewModel?.departureState?.observe(viewLifecycleOwner, Observer { data ->
            Log.d("tutu","departureData observe = " + data)
            departureList.clear()
            if (data.list.isNotEmpty()) {
                departureList.addAll(data.list)
            }
            if (data.isEnd) {
                departureList.add(LoadMore(false, "已經滑到最底了 >_<"))
            }
            Log.d("tutu", "departureList= $departureList")
            Log.d("tutu", "departureList= ${departureList.size}")

            adapter.notifyDataSetChanged()
        })

        // 加載完畢後要刪掉剛剛添加的 “正在加载中，请稍等” 數據
        flightViewModel?.departureIsLoading?.observe(viewLifecycleOwner) { isLoading ->
            // 控制是否顯示 loading item
            if (!isLoading) {
                removeLoadingItem()
            }
        }

    }

    // 加載更多數據
    private fun loadMoreData() {
        departureList.add(LoadMore(true, "正在加载中，请稍等"))
        adapter.notifyItemInserted(departureList.size - 1)
        flightViewModel?.loadDepartureFlights(2, 2)
    }

    // 刪除 “加載中” 的提示
    private fun removeLoadingItem() {
        val index = departureList.indexOfFirst { it is LoadMore && it.isLoading }
        if (index != -1) {
            departureList.removeAt(index)
            adapter.notifyItemRemoved(index)
        }
    }

}