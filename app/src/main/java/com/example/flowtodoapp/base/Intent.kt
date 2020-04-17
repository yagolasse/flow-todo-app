package com.example.flowtodoapp.base

/**
 * Represents an contract for transforming an old state into a new one.
 * We always have the old state in case of needing it to fill the new one.
 */
interface Intent<T> {
    fun reduce(oldState: T): T
}

/**
 * Some DSL methods to create intents with ease.
 */

/**
 * Intent builder function for those cases where the old state is not needed.
 */
fun <T> intent(newState: T) = object : Intent<T> {
    override fun reduce(oldState: T) = newState
}

/**
 * Intent builder function for those cases where the old state is needed.
 */
inline fun <T> intent(crossinline block: T.() -> T) = object : Intent<T> {
    override fun reduce(oldState: T): T = oldState.block()
}

/**
 * Intent builder function for those cases where the old state is needed but you will not emit a new state.
 */
inline fun <T> sideEffect(crossinline block: T.() -> Unit) = object : Intent<T> {
    override fun reduce(oldState: T): T = oldState.apply(block)
}