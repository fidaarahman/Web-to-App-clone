package com.threedev.appconvertor.ui.utils

data class Resource<out T>(val status: Status, val data: T?, val code:Int, val message: String?) {

    companion object {

        fun <T> success(code: Int, data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, code, "Data Fetch successfully")
        }

        fun <T> error(code: Int, msg: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, code, msg)
        }

        fun <T> loading(code: Int, data: T?): Resource<T> {
            return Resource(Status.LOADING, data, code,null)
        }

    }

}
