package com.jaehl.spaceTraders.ui.dialogs.registration

import androidx.compose.runtime.mutableStateOf
import com.jaehl.spaceTraders.data.repo.AgentRepo
import com.jaehl.spaceTraders.ui.util.ViewModel
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegistrationDialogViewModel @Inject constructor(
    private val logger : Logger,
    private val agentRepo: AgentRepo
) : ViewModel() {

    private lateinit var config : RegistrationDialogConfig

    var title = mutableStateOf("Registration")
        private set

    var token = mutableStateOf("")
        private set

    var createButtonEnabled = mutableStateOf(false)
        private set

    fun init(viewModelScope: CoroutineScope, config : RegistrationDialogConfig) {
        super.init(viewModelScope)

        this.config = config

        viewModelScope.launch {

        }
    }

    fun onTokenChange(value : String) {
        token.value = value
        createButtonEnabled.value = value.isNotEmpty()
    }

    fun onCreateClick() = viewModelScope.launch {
        agentRepo.createNew(
            token = token.value
        )
        config.onDismissed()
    }

    fun onCloseClick() = viewModelScope.launch {
        //config.onDismissed()
    }
}