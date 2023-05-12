package com.jaehl.spaceTraders.extensions

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T>MutableState<T>.post(value : T, dispatcher: CoroutineDispatcher = Dispatchers.Main){
    withContext(dispatcher){
        this@post.value = value
    }
}

suspend fun <T> SnapshotStateList<T>.postSwap(values : Collection<T>, dispatcher: CoroutineDispatcher = Dispatchers.Main){
    withContext(dispatcher){
        this@postSwap.clear()
        this@postSwap.addAll(values)
    }
}