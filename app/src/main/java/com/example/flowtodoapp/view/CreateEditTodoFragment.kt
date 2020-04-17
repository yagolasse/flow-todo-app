package com.example.flowtodoapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

import com.example.flowtodoapp.R
import com.example.flowtodoapp.base.ViewEventFlow
import com.example.flowtodoapp.databinding.FragmentCreateEditTodoBinding
import com.example.flowtodoapp.model.CreateEditTodoModel
import com.example.flowtodoapp.model.CreateEditTodoModelStore
import com.example.flowtodoapp.model.CreateEditTodoViewEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.koin.android.viewmodel.ext.android.viewModel
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.afterTextChanges

class CreateEditTodoFragment : Fragment(), ViewEventFlow<CreateEditTodoViewEvent> {

    private val modelStore: CreateEditTodoModelStore by viewModel()
    private lateinit var binding: FragmentCreateEditTodoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCreateEditTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        modelStore.modelState().forTodoDetail()
    }

    override fun viewEvents(): Flow<CreateEditTodoViewEvent> {
        val titleTextFlow = binding.todoTitleEditText.afterTextChanges(emitImmediately = true)
        val contentTextFlow = binding.todoContentEditText.afterTextChanges(emitImmediately = true)
        val buttonClick = binding.finishEditingButton.clicks()
        val flowList = listOf(buttonClick.combine(titleTextFlow) { (_, textEvent) -> textEvent})
    }

    private fun LiveData<CreateEditTodoModel>.forTodoDetail() {
        observe(viewLifecycleOwner, Observer {

        })
    }

}
