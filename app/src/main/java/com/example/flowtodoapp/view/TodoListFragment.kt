package com.example.flowtodoapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flowtodoapp.base.ViewEventFlow
import com.example.flowtodoapp.databinding.FragmentTodoListBinding
import com.example.flowtodoapp.factory.TodoListIntentFactory
import com.example.flowtodoapp.model.TodoListModel
import com.example.flowtodoapp.model.TodoListModelStore
import com.example.flowtodoapp.model.TodoListViewEvent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import reactivecircus.flowbinding.android.widget.afterTextChanges

class TodoListFragment : Fragment(), ViewEventFlow<TodoListViewEvent> {

    private val scope = MainScope()
    private val modelStore: TodoListModelStore by inject()
    private val intentFactory: TodoListIntentFactory by inject()
    private lateinit var binding: FragmentTodoListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewEvents().process().launchIn(scope)
        modelStore.modelState().forTodoList().launchIn(scope)
    }

    override fun onDestroy() {
        super.onDestroy()
        modelStore.close()
        scope.cancel()
    }

    override fun viewEvents(): Flow<TodoListViewEvent> {
        // Using a list to have more than one input at the same time if it is needed
        val flowList = listOf(
            binding
                .searchFieldEditText
                .afterTextChanges(emitImmediately = true)
                .debounce(500)
                .map { get<TodoListViewEvent> { parametersOf(it.editable) } }
        )

        return flowList.asFlow().flattenMerge(flowList.size)
    }

    private fun Flow<TodoListViewEvent>.process(): Flow<TodoListViewEvent> = onEach {
        intentFactory.process(it)
    }

    private fun Flow<TodoListModel>.forTodoList(): Flow<TodoListModel> = onEach { (data, loading, error) ->
        with(binding) {
            // Using when only because those states are mutually exclusive
            when {
                error != null -> TODO("Missing show error")
                loading -> {
                    emptyStateLabel.visibility = View.GONE
                    progress.visibility = View.VISIBLE
                }
                data != null -> {
                    emptyStateLabel.visibility = View.VISIBLE
                    progress.visibility = View.GONE
                }
            }
        }
    }
}