package com.example.flowtodoapp.view

import androidx.recyclerview.widget.RecyclerView
import com.example.flowtodoapp.base.ViewEventFlow
import com.example.flowtodoapp.databinding.ItemTodoBinding
import com.example.flowtodoapp.model.Todo
import com.example.flowtodoapp.model.TodoListViewEvent
import kotlinx.coroutines.flow.map
import reactivecircus.flowbinding.android.view.clicks

class TodoItemViewHolder(
    private val binding: ItemTodoBinding
) : RecyclerView.ViewHolder(binding.root), ViewEventFlow<TodoListViewEvent> {

    private lateinit var todo: Todo

    fun bind(item: Todo) {
        todo = item
        with(binding) {
            todoTitle.text = item.title
            todoContent.text = item.content
        }
    }

    override fun viewEvents() = binding.itemTodoContainer.clicks().map {
        TodoListViewEvent.CreateEditTodo(todo)
    }
}