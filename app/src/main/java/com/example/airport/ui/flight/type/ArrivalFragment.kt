package com.example.airport.ui.flight.type

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
                    loadMoreData()
                }

            }
        })

        // 觀察 FlightFragment 的 flightViewModel arrivalData 變化
        flightViewModel?.arrivalState?.observe(viewLifecycleOwner, Observer { data ->
            arrivalList.clear()
            if (data.list.isNotEmpty()) {
                arrivalList.addAll(data.list)
            }
            if (data.isEnd) {
                arrivalList.add(LoadMore(false, "已經滑到最底了 >_<"))
            }
            adapter.notifyDataSetChanged()
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
        arrivalList.add(LoadMore(true, "正在加载中，请稍等"))
        adapter.notifyItemInserted(arrivalList.size - 1)
        flightViewModel?.loadArrivalFlights(2, 2)
    }

    // 刪除 “加載中” 的提示
    private fun removeLoadingItem() {
        val index = arrivalList.indexOfFirst { it is LoadMore && it.isLoading}
        if (index != -1) {
            arrivalList.removeAt(index)
            adapter.notifyItemRemoved(index)
        }
    }

}