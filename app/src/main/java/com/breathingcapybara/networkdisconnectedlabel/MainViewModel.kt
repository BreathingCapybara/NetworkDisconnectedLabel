package com.breathingcapybara.networkdisconnectedlabel

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    val networkStrengthState = mutableIntStateOf(0)
    fun setNetworkStrength(strength: Int) {
        viewModelScope.launch {
            delay(2.seconds)
            networkStrengthState.intValue = strength
        }
    }

}
