package com.example.flowtodoapp.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flowtodoapp.base.IntentFactory
import com.example.flowtodoapp.base.ModelStore
import com.example.flowtodoapp.base.intent
import com.example.flowtodoapp.model.Todo
import com.example.flowtodoapp.model.TodoListState
import com.example.flowtodoapp.model.TodoListViewEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

@ExperimentalCoroutinesApi
class TodoListIntentFactory(
    private val modelStore: ModelStore<TodoListState>
) : IntentFactory<TodoListViewEvent>, KoinComponent {

    private val scope = (modelStore as ViewModel).viewModelScope
    private val useCase: ITodoUseCase by inject()

    override fun process(viewEvent: TodoListViewEvent) {
        modelStore.process(toIntent(viewEvent))
    }

    private fun toIntent(viewEvent: TodoListViewEvent) = when (viewEvent) {
        is TodoListViewEvent.CreateEditTodo -> intent<TodoListState> {
            TodoListState.NavigateToTodoCreateEdit(viewEvent.editItem, viewEvent.alreadyExecuted)
        }
        is TodoListViewEvent.Query -> intent {
            useCase
                .getTodoListByQuery(viewEvent.editable)
                .processExceptionIntent()
                .processDataIntent()

            TodoListState.Loading
        }
    }

    private fun Flow<List<Todo>>.processExceptionIntent() = catch {
        modelStore.process(intent(TodoListState.ErrorWithoutMessage))
    }

    private fun Flow<List<Todo>>.processDataIntent() {
        scope.launch(Dispatchers.IO) {
            collect { dataSet ->
                val newState = if (dataSet.isNotEmpty()) TodoListState.Data(dataSet)
                else TodoListState.Empty
                modelStore.process(intent(newState))
            }
        }
    }
}