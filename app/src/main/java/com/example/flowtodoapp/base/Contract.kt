package com.example.flowtodoapp.base

import kotlinx.coroutines.flow.Flow

interface Reducer<S, A> {
    fun reduce(state: S, action: A): S
}

interface Middleware<A, S> {
    fun bind(actions: Flow<A>, stateFlow: Flow<S>): Flow<A>
}

interface Store<A, S> {
    fun process(action: A)
    fun storeState(): Flow<S>
}

