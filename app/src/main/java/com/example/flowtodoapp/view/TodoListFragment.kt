package com.example.flowtodoapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.flowtodoapp.R
import com.example.flowtodoapp.databinding.FragmentTodoListBinding
import com.example.flowtodoapp.model.*
import com.example.flowtodoapp.viewmodel.ITodoListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.ext.scope
import reactivecircus.flowbinding.android.widget.afterTextChanges

class TodoListFragment : Fragment(), ITodoListView {

    private lateinit var binding: FragmentTodoListBinding
    private val todoListViewModel: ITodoListViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todoListViewModel.bind(this)
    }

    override fun loadListIntent(): Flow<String?> = binding
        .searchFieldEditText
        .afterTextChanges(emitImmediately = true)
        .debounce(300)
        .map { it.editable?.toString() }
        .flowOn(Dispatchers.Main)

    override fun render(stateObservable: LiveData<State>) {
        stateObservable.observe(viewLifecycleOwner, Observer { newState ->
            when (newState) {
                is LoadingState -> renderLoadingState()
                is ErrorState -> renderErrorState(newState)
                is TodoListEmptyState -> renderEmptyState()
                is TodoListDataState -> renderDataState(newState)
            }
        })
    }

    private fun renderLoadingState() {
        with(binding) {
            progress.visibility = View.VISIBLE
            emptyStateLabel.visibility = View.GONE
        }
    }

    private fun renderErrorState(state: ErrorState) {

    }

    private fun renderDataState(state: TodoListDataState) {

    }

    private fun renderEmptyState() {
        with(binding) {
            progress.visibility = View.GONE
            emptyStateLabel.visibility = View.VISIBLE
        }
    }
}
