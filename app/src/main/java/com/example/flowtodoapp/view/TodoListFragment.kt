package com.example.flowtodoapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.flowtodoapp.base.ModelStore
import com.example.flowtodoapp.databinding.FragmentTodoListBinding
import com.example.flowtodoapp.model.TodoListAction
import com.example.flowtodoapp.model.TodoListEvent
import com.example.flowtodoapp.model.TodoListState
import com.example.flowtodoapp.util.isVisibleIf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.android.viewmodel.ext.android.viewModel
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.afterTextChanges

@FlowPreview
@ExperimentalCoroutinesApi
class TodoListFragment : Fragment() {

    private lateinit var binding: FragmentTodoListBinding
    private val modelStore: ModelStore<TodoListAction, TodoListState, TodoListEvent> by viewModel()
    private val adapter: TodoListRecyclerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        TodoListRecyclerAdapter(viewLifecycleOwner.lifecycleScope, modelStore)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTodoListBinding.inflate(inflater, container, false).apply {
            todoListRecycler.adapter = adapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewEvents().process().launchIn(viewLifecycleOwner.lifecycleScope)
        modelStore.storeState().renderNewState().launchIn(viewLifecycleOwner.lifecycleScope)
        modelStore.storeEvents().handleEvents().launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun viewEvents(): Flow<TodoListAction> {
        val flowList = listOf(addButtonFlow(), searchFieldEditTextFlow())
        return flowList.asFlow().flattenMerge(flowList.size)
    }

    private fun addButtonFlow(): Flow<TodoListAction> = binding
        .addButton
        .clicks()
        .map { TodoListAction.CreateEditTodo() }

    private fun searchFieldEditTextFlow(): Flow<TodoListAction> = binding
        .searchFieldEditText
        .afterTextChanges(emitImmediately = true)
        .debounce(timeoutMillis = 500)
        .map { TodoListAction.GetByQuery(it.editable) }

    private fun Flow<TodoListAction>.process() = onEach {
        modelStore.process(it)
    }

    private fun Flow<TodoListState>.renderNewState() = onEach { newState ->
        with(binding) {
            progress isVisibleIf newState.isLoading
            emptyStateLabel isVisibleIf newState.isEmpty
            todoListRecycler isVisibleIf (newState.data != null)

            if (newState.data != null) {
                adapter.dataSet = newState.data
            }
        }
    }

    private fun Flow<TodoListEvent>.handleEvents() = onEach {
        when (it) {
            is TodoListEvent.NavigateToCreateEditTodo -> context?.run { Toast.makeText(this, "Worked", Toast.LENGTH_SHORT).show() }
        }
    }
}