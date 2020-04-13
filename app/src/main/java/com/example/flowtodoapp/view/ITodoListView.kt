package com.example.flowtodoapp.view

import androidx.lifecycle.LiveData
import com.example.flowtodoapp.model.State
import kotlinx.coroutines.flow.Flow

interface ITodoListView {
    fun loadListIntent(): Flow<String?>
    fun render(stateObservable: LiveData<State>)
}