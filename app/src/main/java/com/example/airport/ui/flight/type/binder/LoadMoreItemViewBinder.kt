package com.example.airport.ui.flight.type.binder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder
import com.example.airport.data.LoadMore
import com.example.airport.databinding.ItemLoadMoreBinding

class LoadMoreItemViewBinder: ItemViewBinder<LoadMore, LoadMoreItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val binding = ItemLoadMoreBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: LoadMore) {
        holder.bind(item)
    }

    class ViewHolder(private val binding: ItemLoadMoreBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LoadMore) {
            binding.loadMore = item
            binding.executePendingBindings()
        }
    }
}