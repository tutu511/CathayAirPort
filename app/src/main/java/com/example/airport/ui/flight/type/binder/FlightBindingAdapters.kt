package com.example.airport.ui.flight.type.binder

import android.view.View
import androidx.databinding.BindingAdapter
import com.example.airport.R

@BindingAdapter("statusBackground")
fun setStatusBackground(view: View, status: String?) {
    val backgroundRes = when (status) {
        "抵達Arrived" -> R.drawable.shape_rc_adc5d8_corner10_bottom
        "準時On Time" -> R.drawable.shape_rc_c6f6a7_corner10_bottom
        "取消Cancelled" -> R.drawable.shape_rc_f4cfb4_corner10_bottom
        "延遲Delayed" -> R.drawable.shape_rc_f8c0d2_corner10_bottom
        else -> R.drawable.shape_rc_adc5d8_corner10_bottom // 預設背景
    }
    view.setBackgroundResource(backgroundRes)
}