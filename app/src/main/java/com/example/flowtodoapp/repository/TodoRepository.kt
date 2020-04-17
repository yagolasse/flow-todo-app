package com.example.flowtodoapp.repository

import com.example.flowtodoapp.model.Todo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TodoRepository : ITodoRepository {

    // TODO refactor to use Room
    private val dataSet: MutableList<Todo> = mutableListOf()

    override fun getTodoListByQuery(query: String): Flow<List<Todo>> = flow {
        delay(1000)
        emit(dataSet.filter { it.content.contains(query, ignoreCase = true) })
    }

    override fun getTodoList(): Flow<List<Todo>> = flow {
        delay(1000)
        emit(dataSet)
    }

    override fun save(todo: Todo): Flow<Unit> = flow {
        // TODO use Room and stop using id management by hand
        delay(1000)
        dataSet.add(todo.copy(id = dataSet.size))
        emit(Unit)
    }

    override fun update(todo: Todo): Flow<Unit> = flow {
        require(todo.id != null)
        dataSet[todo.id] = todo
        emit(Unit)
    }
}