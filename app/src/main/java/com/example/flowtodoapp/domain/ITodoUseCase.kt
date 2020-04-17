package com.example.flowtodoapp.domain

import android.text.Editable
import com.example.flowtodoapp.model.Todo
import kotlinx.coroutines.flow.Flow

interface ITodoUseCase {

    fun getTodoListByQuery(query: Editable?): Flow<List<Todo>>
    fun save(todo: Todo): Flow<Unit>
}