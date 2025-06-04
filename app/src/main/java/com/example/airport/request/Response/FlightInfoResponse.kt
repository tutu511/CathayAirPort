package com.example.airport.request.Response

import com.example.airport.data.FlightInfo
import com.google.gson.annotations.SerializedName

data class FlightInfoResponse (
    @SerializedName("InstantSchedule")
    val instantSchedule: List<FlightInfo>
)