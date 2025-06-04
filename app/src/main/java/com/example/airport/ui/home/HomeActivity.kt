package com.example.airport.ui.home

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.airport.R
import com.example.airport.BR
import com.example.airport.base.BaseActivity
import com.example.airport.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView


class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_home
    }

    override fun getViewModelId(): Int {
        return BR.homeVm
    }

    override fun initView() {
        val navFragment =
            supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navFragment.navController

        when (val navView = binding.homeBar) {
            is BottomNavigationView -> {
                // 在底部
                navView.setupWithNavController(navController)
                navView.itemIconTintList = null
            }
            is NavigationRailView -> {
                // 在左側
                NavigationUI.setupWithNavController(
                    navView,
                    navController
                )
                navView.itemIconTintList = null
            }
        }
    }

}