package com.example.flowtodoapp.usecase

import com.example.flowtodoapp.model.Todo
import com.example.flowtodoapp.repository.ITodoRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.KoinComponent
import org.koin.core.inject

class TodoUseCase : ITodoUseCase, KoinComponent {

    private val repository: ITodoRepository by inject()

    override fun getTodoListByQuery(query: String?): Flow<List<Todo>> {
        // TODO apply rules
        return repository.getTodoListByQuery(query?.trim())
    }
}