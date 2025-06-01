package com.example.airport.ui.rate

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.airport.ui.theme.AirportTheme

// 保留 Fragment 架構 + Jetpack Compose 來畫 UI
class RateFragment: Fragment() {

    // 建立 ViewModel，範圍限定為這個 Fragment
    private val viewModel: RateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 用 ComposeView 建立 Composable 畫面
        return ComposeView(requireContext()).apply {
            setContent {
                AirportTheme {
                    // Jetpack Compose 的入口：顯示匯率畫面
                    ExchangeRateScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchAllRates()
    }
}