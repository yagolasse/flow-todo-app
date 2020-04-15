package com.example.flowtodoapp.base

interface IntentFactory<E> {
    fun process(viewEvent: E)
}