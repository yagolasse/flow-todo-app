package com.example.flowtodoapp.base

/**
 * Contract for an intent factory.
 * Is here that is defined your api calls or side effects for an intent reducer, for instance.
 */
interface IntentFactory<E> {
    fun process(viewEvent: E)
}