package com.example.airport.request.Response

import java.io.Serializable

class BaseResponse<T> : Serializable {
    private var errorMsg: String? = null
    private var errorCode: Int? = null
    private var data: T? = null
    private var success: Boolean = false

    fun getErrMsg(): String? {
        return errorMsg
    }

    fun setErrMsg(message: String?) {
        this.errorMsg = message
    }

    fun getData(): T? {
        return data
    }

    fun setData(data: T) {
        this.data = data
    }

    fun getErrCode(): Int? {
        return errorCode
    }

    fun setErrCode(errCode: Int?) {
        this.errorCode = errCode
    }

    fun isSuccess(): Boolean {
        return success
    }

    fun setSuccess(success: Boolean) {
        this.success = success
    }
}