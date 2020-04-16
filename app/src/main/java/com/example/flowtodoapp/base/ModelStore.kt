package com.example.flowtodoapp.base

import androidx.lifecycle.LiveData

interface ModelStore<S> {

    fun process(intent: Intent<S>)

    fun modelState(): LiveData<S>
}