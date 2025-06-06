package com.example.airport.data

data class FlightState(
    val list: List<FlightInfo> = emptyList(),
    val isEnd: Boolean = false,
    val isSuccess: Boolean = true,
    val isRefresh: Boolean = false,
)
