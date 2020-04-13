package com.example.flowtodoapp.viewmodel

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.flowtodoapp.model.*
import com.example.flowtodoapp.repository.ITodoRepository
import com.example.flowtodoapp.usecase.ITodoUseCase
import com.example.flowtodoapp.view.ITodoListView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class TodoListViewModel : ITodoListViewModel(), KoinComponent {

    private val useCase: ITodoUseCase by inject()

    override fun bind(view: ITodoListView) {
        val intentResult = view
            .loadListIntent()
            .flatMapLatest { useCase.getTodoListByQuery(it) }
            .flowOn(Dispatchers.IO)
            .map {
                if (it.isNotEmpty()) {
                    TodoListDataState(it)
                } else {
                    TodoListEmptyState
                }
            }
            .onStart { emit(LoadingState) }
            .catch { emit(ErrorState(it)) }
            .asLiveData(viewModelScope.coroutineContext)

        view.render(intentResult)
    }
}