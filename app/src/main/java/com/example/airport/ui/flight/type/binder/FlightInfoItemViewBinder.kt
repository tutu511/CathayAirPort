package com.example.airport.ui.flight.type.binder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder
import com.example.airport.data.FlightInfo
import com.example.airport.databinding.ItemFlightInfoBinding

class FlightInfoItemViewBinder: ItemViewBinder<FlightInfo, FlightInfoItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val binding = ItemFlightInfoBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: FlightInfo) {
        // 綁定數據
        holder.binding.flightInfo = item
        holder.binding.executePendingBindings()
    }

    class ViewHolder(val binding: ItemFlightInfoBinding) : RecyclerView.ViewHolder(binding.root)

}