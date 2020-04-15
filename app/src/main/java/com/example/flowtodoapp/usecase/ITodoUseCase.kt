package com.example.flowtodoapp.usecase

import android.text.Editable
import com.example.flowtodoapp.model.Todo
import kotlinx.coroutines.flow.Flow

interface ITodoUseCase {

    fun getTodoListByQuery(query: Editable?): Flow<List<Todo>>
}