package com.example.flowtodoapp.base

import kotlinx.coroutines.flow.Flow

interface ViewEventFlow<E> {
    fun viewEvents(): Flow<E>
}