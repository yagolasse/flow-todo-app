package com.example.flowtodoapp.domain

import android.text.Editable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import com.example.flowtodoapp.base.test
import com.example.flowtodoapp.model.Todo
import com.example.flowtodoapp.model.TodoListAction
import com.example.flowtodoapp.model.TodoListState
import com.example.flowtodoapp.repository.ITodoRepository
import com.example.flowtodoapp.repository.TodoRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.test.KoinTest

import org.junit.Assert.*

class TodoSearchMiddlewareTest : KoinTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `bind() should return list when query is an empty string`() {
        // Given
        val result = listOf<Todo>()
        val query = SpannableStringBuilder("")
        val actionFlow = flow { emit(TodoListAction.GetByQuery(query)) }
        val stateFlow = flow { emit(TodoListState()) }
        val repository = mockk<ITodoRepository> {
            coEvery { getTodoListByQuery(any()) } returns flow { emit(result) }
        }
        val middleware = TodoSearchMiddleware(repository)
        runBlockingTest {
            middleware
                // When
                .bind(actionFlow, stateFlow)
                .test(this)
                // Then
                .assertValues(TodoListAction.DisplayList(result))
                .finish()
        }
    }
}