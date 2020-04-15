package com.example.flowtodoapp.base

interface Intent<T> {
    fun reduce(oldState: T): T
}

inline fun <T> intent(crossinline block: T.() -> T) = object : Intent<T> {
    override fun reduce(oldState: T): T = oldState.block()
}

inline fun <T> sideEffect(crossinline block: T.() -> Unit) = object : Intent<T> {
    override fun reduce(oldState: T): T = oldState.apply(block)
}