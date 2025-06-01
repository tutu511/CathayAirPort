package com.example.airport.ui.flight

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.airport.ui.flight.type.ArrivalFragment
import com.example.airport.ui.flight.type.DepartureFragment

class FlightsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    val fragmentList = listOf(
        DepartureFragment(),
        ArrivalFragment()
    )

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = fragmentList[position]
}