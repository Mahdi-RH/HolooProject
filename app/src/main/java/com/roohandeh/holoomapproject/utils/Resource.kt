package com.roohandeh.holoomapproject.utils

sealed class Resource<T>(val data: T? = null,val message: String? = null) {
    class Success<T>(data: T? = null) : Resource<T>(data = data)
    class Loading<T> : Resource<T>()
    class Error<T>(message: String? = null) : Resource<T>(message = message)
}