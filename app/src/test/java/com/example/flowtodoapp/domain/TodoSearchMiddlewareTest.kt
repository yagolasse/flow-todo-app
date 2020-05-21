package com.example.flowtodoapp.domain

import android.text.SpannableStringBuilder
import com.example.flowtodoapp.base.test
import com.example.flowtodoapp.model.Todo
import com.example.flowtodoapp.model.TodoListAction
import com.example.flowtodoapp.model.TodoListState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.test.KoinTest

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
        val useCase = mockk<ITodoUseCase> {
            coEvery { getTodoListByQuery(any()) } returns flow { emit(result) }
        }
        val middleware = TodoSearchMiddleware(useCase)
        runBlockingTest {
            // When
            middleware.bind(actionFlow, stateFlow).test(this)
                // Then
                .assertValues(TodoListAction.DisplayList(result)).finish()
        }
    }
}