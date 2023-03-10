package ru.avem.viu35.viewmodels

import androidx.compose.runtime.MutableState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import ru.avem.viu35.utils.PASSWORD

class PasswordScreenViewModel(private val navigateTo: Screen) : ScreenModel {
    fun checkPassword(
        password: MutableState<String>,
        navigator: Navigator,
        passwordError: MutableState<Boolean>,
    ) {
        if (password.value == PASSWORD) {
            navigator.push(navigateTo)
        } else {
            passwordError.value = true
        }
    }
}