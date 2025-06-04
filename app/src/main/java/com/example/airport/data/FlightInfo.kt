package com.example.airport.data

import com.google.gson.annotations.SerializedName

data class FlightInfo(

    // 預定起飛或抵達時間 - "09:00"
    @SerializedName("expectTime")
    val expectTime: String,

    // 實際起飛或抵達時間 - 例如 "08:49"
    @SerializedName("realTime")
    val realTime: String,

    // 航空公司名稱 - "立榮航空"
    @SerializedName("airLineName")
    val airLineName: String,

    // 航空公司代碼 - "UIA"
    @SerializedName("airLineCode")
    val airLineCode: String,

    // 航空公司 Logo 圖片網址
    @SerializedName("airLineLogo")
    val airLineLogo: String,

    // 航空公司官方網址
    @SerializedName("airLineUrl")
    val airLineUrl: String,

    // 航班號碼 - "B78690"
    @SerializedName("airLineNum")
    val airLineNum: String,

    // 起飛機場代碼 - "MZG"
    @SerializedName("upAirportCode")
    val upAirportCode: String,

    // 起飛機場名稱 - "澎湖"
    @SerializedName("upAirportName")
    val upAirportName: String,

    // 飛機型號 - "AT76"
    @SerializedName("airPlaneType")
    val airPlaneType: String,

    // 登機門號 - "17"
    @SerializedName("airBoardingGate")
    val airBoardingGate: String,

    // 飛行狀態 - "抵達Arrived"【抵達班機】 --- "延遲Delayed"、"準時On Time"、"取消Cancelled"【起飛班機】
    @SerializedName("airFlyStatus")
    val airFlyStatus: String,

    // 延誤原因 - "颱風天"、"暴雨"
    @SerializedName("airFlyDelayCause")
    val airFlyDelayCause: String? = null
)