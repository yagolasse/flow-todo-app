package com.example.flowtodoapp.repository

import com.example.flowtodoapp.model.Todo
import kotlinx.coroutines.flow.Flow

interface ITodoRepository {

    fun getTodoListByQuery(query: String): Flow<List<Todo>>
    fun getTodoList(): Flow<List<Todo>>
}