package com.example.flowtodoapp.repository

import com.example.flowtodoapp.model.Todo
import kotlinx.coroutines.flow.Flow

interface ITodoRepository {

    fun getTodoListByQuery(query: String? = null): Flow<List<Todo>>
}