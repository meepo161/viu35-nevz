package ru.avem.viu35.screens.auth

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import operator
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.viu35.composables.ComboBox
import ru.avem.viu35.database.DBManager
import ru.avem.viu35.database.entities.User
import ru.avem.viu35.database.entities.Users
import ru.avem.viu35.screens.MainScreen
import ru.avem.viu35.utils.keyEventNext
import ru.avem.viu35.utils.keyboardActionNext

class LoginScreen : Screen {
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    @Preview
    override fun Content() {
        val localNavigator = LocalNavigator.currentOrThrow
        val focusManager = LocalFocusManager.current

        var login = mutableStateOf("")
        var password = mutableStateOf("")
        var loginErrorState by remember { mutableStateOf(false) }
        var passwordErrorState by remember { mutableStateOf(false) }
        lateinit var users: List<User>
        var selectedLogin = mutableStateOf(DBManager.getAllUsers().first())


        fun authorize() {
            operator = login.value
            localNavigator.push(MainScreen)
        }

        fun tryLogin() {
            login.value = selectedLogin.value.toString()
            when {
                login.value.isEmpty() -> {
                    loginErrorState = true
                }

                password.value.isEmpty() -> {
                    passwordErrorState = true
                }

                else -> {
                    transaction {
                        users = User.find {
                            (Users.name eq login.value) and (Users.password eq password.value)
                        }.toList()
                    }
                    if (users.isEmpty()) {
                        loginErrorState = true
                        passwordErrorState = true
                    } else {
                        authorize()
                    }
                }
            }
        }

        Scaffold(
            content = {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MaterialTheme.colors.primary)) {
                            append("А")
                        }
                        append("вторизация")
                    }, fontSize = 30.sp)
                    Spacer(Modifier.size(16.dp))
                    ComboBox(
                        modifier = Modifier.width(280.dp),
                        selectedItem = selectedLogin,
                        items = DBManager.getAllUsers(),
                        selectedValue = {
                            selectedLogin.value = it
                        },
                        textAlign = TextAlign.Start
                    )
                    Spacer(Modifier.size(16.dp))
                    var passwordVisibility by remember { mutableStateOf(true) }
                    OutlinedTextField(
                        textStyle = TextStyle.Default.copy(
                            fontSize = 20.sp
                        ),
                        value = password.value,
                        singleLine = true,
                        onValueChange = { password.value = it },
                        isError = passwordErrorState,
                        modifier = Modifier.focusTarget().onPreviewKeyEvent {
                            keyEventNext(it, focusManager)
                        }.onKeyEvent {
                            if (it.key == Key.Enter) {
                                tryLogin()
                                true
                            } else {
                                false
                            }
                        },
                        label = {
                            Text(text = "Введите пароль*")
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                passwordVisibility = !passwordVisibility
                            }) {
                                Icon(
                                    imageVector = if (passwordVisibility) Icons.Default.RemoveRedEye else Icons.Default.Clear,
                                    contentDescription = "visibility",
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisibility) PasswordVisualTransformation() else VisualTransformation.None,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = keyboardActionNext(focusManager)
                    )
                    if (passwordErrorState) {
                        Text(text = "Ошибка", color = MaterialTheme.colors.primary)
                    } else {
                        Text(text = "")
                    }
                    Spacer(Modifier.size(16.dp))
                    Button(
                        onClick = {
                            tryLogin()
                        },
                        content = {
                            Text(
                                text = "Вход",
                                color = MaterialTheme.colors.surface,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                    )
                }
            })
    }
}
