package com.example.airport.data

import android.provider.ContactsContract
import com.google.gson.annotations.SerializedName
import java.sql.Date

data class FlightInfo(
    // FlyType：A-入境航班 / D-出境航班
    @SerializedName("FlyType")
    val FlyType: String,

    // Remark：航班狀態描述 - 已到ARRIVED
    @SerializedName("Remark")
    val Remark: String,

    // EstimatedTime：預估時間 - 00:02
    @SerializedName("EstimatedTime")
    val EstimatedTime: String,

    // ActualTime：實際時間 - 00:02
    @SerializedName("ActualTime")
    val ActualTime: String,

    // DepartureAirportID：起點機場IATA國際代碼 - MNL
    @SerializedName("DepartureAirportID")
    val DepartureAirportID: String,

    // DepartureAirport：起點機場 - 馬尼拉機場
    @SerializedName("DepartureAirport")
    val DepartureAirport: String,

    // ArrivalAirportID：目的地機場IATA國際代碼 - TPE
    @SerializedName("ArrivalAirportID")
    val ArrivalAirportID: String,

    // ArrivalAirport：目的地機場名稱 - 臺北桃園國際機場
    @SerializedName("ArrivalAirport")
    val ArrivalAirport: String,

    // AirlineID：航空公司IATA國際代碼 - JX
    @SerializedName("AirlineID")
    val AirlineID: String,

    // Airline：航空公司名稱 - 星宇航空
    @SerializedName("Airline")
    val Airline: String,

    // FlightNumber：航機班號 - 786
    @SerializedName("FlightNumber")
    val FlightNumber: String,

    // Terminal：航廈 - 1
    @SerializedName("Terminal")
    val Terminal: String,

    // Gate：登機門 - A9
    @SerializedName("Gate")
    val Gate: String,

    // ScheduleTime: 表訂出發/到達時間 - 00:10
    @SerializedName("ScheduleTime")
    val ScheduleTime: String,

    // UpdateTime: 資料更新時間 - 2023-05-23 11:00:28
    @SerializedName("UpdateTime")
    val UpdateTime: String

)