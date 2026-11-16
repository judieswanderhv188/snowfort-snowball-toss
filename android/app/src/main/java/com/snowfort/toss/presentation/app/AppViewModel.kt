package com.snowfort.toss.presentation.app

import androidx.lifecycle.ViewModel
import com.snowfort.toss.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val backStack = ArrayDeque<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    fun open(target: Screen) {
        val current = _screen.value
        if (current !is Screen.Splash) {
            backStack.addLast(current)
        }
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun replace(target: Screen) {
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun back() {
        if (backStack.isEmpty()) return
        _screen.value = backStack.removeLast()
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun home() {
        backStack.clear()
        _screen.value = Screen.Menu
        _canGoBack.value = false
    }
}
