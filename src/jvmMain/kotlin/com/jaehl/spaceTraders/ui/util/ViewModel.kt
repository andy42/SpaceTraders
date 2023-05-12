package com.jaehl.spaceTraders.ui.util

import kotlinx.coroutines.CoroutineScope

open class ViewModel {
    protected lateinit var viewModelScope: CoroutineScope

    open fun init(viewModelScope: CoroutineScope) {
        this.viewModelScope = viewModelScope
    }

}