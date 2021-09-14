package com.example.rss_mvi.internal

internal val Any.TAG: String
    get() = if (!javaClass.isAnonymousClass) {
        val name = javaClass.simpleName
        if (name.length <= 23) name else name.substring(0, 23)
    } else {
        val name = javaClass.name
        if (name.length <= 23) name else name.substring(name.length - 23, name.length)
    }

internal interface ViewModelContract<EVENT> {
    fun process(viewEvent: EVENT)
}

class NoObserverAttachedException(message: String) : Exception(message)