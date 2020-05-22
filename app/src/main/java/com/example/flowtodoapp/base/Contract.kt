package com.example.flowtodoapp.base

import kotlinx.coroutines.flow.Flow

interface Reducer<S, A, E> {
    fun reduce(state: S, action: A): Pair<S, E?>
}

interface Middleware<A, S> {
    fun bind(actions: Flow<A>, stateFlow: Flow<S>): Flow<A>
}

interface Store<A, S, E> {
    fun process(action: A)
    fun storeState(): Flow<S>
    fun storeEvents(): Flow<E>
}

