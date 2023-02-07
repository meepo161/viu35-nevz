package ru.avem.viu35.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.avem.viu35.PASSWORD

class PasswordScreen(private val navigateTo: Screen) : Screen {
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val password = remember { mutableStateOf("") }
        val passwordVisible = remember { mutableStateOf(false) }
        val passwordError = remember { mutableStateOf(false) }

        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
            TopAppBar(
                title = { Text("Введите пароль для продолжения") },
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.pop()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                })
        }) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    isError = passwordError.value,
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Пароль") },
                    singleLine = true,
                    placeholder = { Text("Пароль") },
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.onKeyEvent {
                        if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                            checkPassword(password, navigator, passwordError)
                        }
                        false
                    },
                    trailingIcon = {
                        val image = if (passwordVisible.value)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible.value) "Скрыть пароль" else "Показать пароль"
                        IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                            Icon(imageVector = image, description)
                        }
                    }
                )
                Button(
                    onClick = {
                        if (password.value == "123") {
                            navigator.push(navigateTo)
                        } else {
                            passwordError.value = true
                        }
                    },
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 10.dp,
                        pressedElevation = 15.dp,
                        disabledElevation = 0.dp
                    ),

                    ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Далее")
                        Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }

        }
    }

    private fun checkPassword(
        password: MutableState<String>,
        navigator: Navigator,
        passwordError: MutableState<Boolean>
    ) {
        if (password.value == PASSWORD) {
            navigator.push(navigateTo)
        } else {
            passwordError.value = true
        }
    }

}