package com.example.flowtodoapp.usecase

import com.example.flowtodoapp.model.Todo
import kotlinx.coroutines.flow.Flow

interface ITodoUseCase {

    fun getTodoListByQuery(query: String? = null): Flow<List<Todo>>
}