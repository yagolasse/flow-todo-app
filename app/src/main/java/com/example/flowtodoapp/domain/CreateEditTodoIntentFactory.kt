package com.example.flowtodoapp.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flowtodoapp.base.Intent
import com.example.flowtodoapp.base.IntentFactory
import com.example.flowtodoapp.base.ModelStore
import com.example.flowtodoapp.base.intent
import com.example.flowtodoapp.model.CreateEditTodoState
import com.example.flowtodoapp.model.CreateEditTodoViewEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class CreateEditTodoIntentFactory(
    private val modelStore: ModelStore<CreateEditTodoState>
) : IntentFactory<CreateEditTodoViewEvent>, KoinComponent {

    private val scope = (modelStore as ViewModel).viewModelScope
    private val useCase: ITodoUseCase by inject()

    override fun process(viewEvent: CreateEditTodoViewEvent) {
        modelStore.process(toIntent(viewEvent))
    }

    private fun toIntent(viewEvent: CreateEditTodoViewEvent): Intent<CreateEditTodoState> = when (viewEvent) {
        is CreateEditTodoViewEvent.SaveTodo -> intent {
            require(this is CreateEditTodoState.DataState)
            if (this !is CreateEditTodoState.DataState.LoadingSave) {
                scope.launch(Dispatchers.IO) {
                    useCase.save(todo).collect {
                        modelStore.process(intent(CreateEditTodoState.DoneSaving))
                    }
                }
            }
            CreateEditTodoState.DataState.LoadingSave(todo)
        }
        is CreateEditTodoViewEvent.EditTodo -> intent oldState@{
            require(this is CreateEditTodoState.DataState)
            if (this !is CreateEditTodoState.DataState.LoadingSave) {
                val newStateData = todo.copy(title = viewEvent.title?.toString() ?: "", content = viewEvent.content?.toString() ?: "")
                CreateEditTodoState.DataState.Editing(newStateData)
            } else {
                this@oldState
            }
        }
        is CreateEditTodoViewEvent.BackNavigation -> intent {
            CreateEditTodoState.BackNavigation
        }
    }
}