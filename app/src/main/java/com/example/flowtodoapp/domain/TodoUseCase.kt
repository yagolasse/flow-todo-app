package com.example.flowtodoapp.domain

import android.text.Editable
import com.example.flowtodoapp.model.Todo
import com.example.flowtodoapp.repository.ITodoRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.KoinComponent
import org.koin.core.inject

class TodoUseCase(private val emptyTodoTitleErrorMessage: String) : ITodoUseCase, KoinComponent {

    private val repository: ITodoRepository by inject()

    override fun getTodoListByQuery(query: Editable?): Flow<List<Todo>> {
        return if (query == null) repository.getTodoList()
        else repository.getTodoListByQuery(query.toString().trim())
    }

    override fun save(todo: Todo): Flow<Unit> {
        require(todo.title.isNotBlank())
        return if (todo.id == null) repository.save(todo)
        else repository.update(todo)
    }
}