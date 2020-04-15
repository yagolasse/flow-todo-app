package com.example.flowtodoapp.repository

import com.example.flowtodoapp.model.Todo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TodoRepository : ITodoRepository {

    // TODO refactor to use Room
    private val dataSet: List<Todo> = mutableListOf(Todo("Title", "abcde"), Todo("Title", "edcba"))

    override fun getTodoListByQuery(query: String): Flow<List<Todo>> = flow {
        delay(1000)
        emit(dataSet.filter { it.content.contains(query, ignoreCase = true) })
    }

    override fun getTodoList(): Flow<List<Todo>> = flow {
        delay(1000)
        emit(dataSet)
    }
}