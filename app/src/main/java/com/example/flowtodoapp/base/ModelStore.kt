package com.example.flowtodoapp.base

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

interface ModelStore<S> {

    fun process(intent: Intent<S>)

    fun modelState(): Flow<S>
}