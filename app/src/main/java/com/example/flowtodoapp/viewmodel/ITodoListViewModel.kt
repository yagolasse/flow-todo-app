package com.example.flowtodoapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.flowtodoapp.view.ITodoListView

/**
 * Represents the contract of the TodoListViewModel.
 * Using an abstract class because Koin does not have a way to inject a view model as an interface.
 */
abstract class ITodoListViewModel : ViewModel() {

    abstract fun bind(view: ITodoListView)
}