package com.example.rss_mvi.internal

sealed class HttpResponse<out T> {
    data class Success<T>(val data: T) : HttpResponse<T>()
    data class Error<T>(val message: String) : HttpResponse<T>() {
        constructor(t: Throwable) : this(t.message ?: "")
    }
}