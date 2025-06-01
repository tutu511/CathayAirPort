package com.example.airport.ui.home

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.airport.R
import com.example.airport.BR
import com.example.airport.base.BaseActivity
import com.example.airport.databinding.ActivityHomeBinding


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
        val navController: NavController = navFragment.navController
        binding.homeBar.setupWithNavController(navController)

        // 取消 icon 自带的切换颜色
        binding.homeBar.itemIconTintList = null
    }

}