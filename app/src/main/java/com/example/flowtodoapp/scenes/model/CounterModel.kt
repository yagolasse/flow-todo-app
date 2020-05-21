package com.example.flowtodoapp.scenes.model

sealed class CounterAction {
    data class Increment(val incrementAmount: Int = 1) : CounterAction()
    data class ShowValue(val value: Int) : CounterAction()
}

data class CounterState(val value: Int)