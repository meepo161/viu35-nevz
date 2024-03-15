package ru.avem.viu35.screens.auth

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import onExit
import operatorLogin
import operatorPostString
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.viu35.composables.ComboBox
import ru.avem.viu35.database.DBManager
import ru.avem.viu35.database.entities.User
import ru.avem.viu35.database.entities.Users
import ru.avem.viu35.screens.MainScreen

class LoginScreen : Screen {
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    @Preview
    override fun Content() {
        val localNavigator = LocalNavigator.currentOrThrow
        val focusManager = LocalFocusManager.current

        val login = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }
        var loginErrorState by remember { mutableStateOf(false) }
        var passwordErrorState by remember { mutableStateOf(false) }
        var users = remember { mutableStateListOf<User>() }
        val selectedLogin = remember { mutableStateOf(DBManager.getAllUsers().first()) }
        var passwordVisibility by remember { mutableStateOf(true) }

        val gradient =
            Brush.verticalGradient(listOf(Color(0xFF7a32ff), Color(0xFF5e00c2)))
        val gradient2 =
            Brush.verticalGradient(listOf(Color(0x66F7a32ff), Color(0x665e00c2)))

        LifecycleEffect(onStarted = {
            users.addAll(DBManager.getAllUsers())
        })

        fun authorize() {
            operatorLogin = login.value
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
                    var user = transaction {
                        User.find {
                            (Users.name eq login.value) and (Users.password eq password.value)
                        }.toList()
                    }
                    if (user.isEmpty()) {
                        loginErrorState = true
                        passwordErrorState = true
                    } else {
                        operatorPostString = user[0].post
                        authorize()
                    }
                }
            }
        }

        Scaffold(
            content = {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Вход",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.size(64.dp))

                    Column(
                        modifier = Modifier.width(460.dp)
                    ) {
                        Text(
                            text = "Имя пользователя",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        ComboBox(
                            modifier = Modifier.width(460.dp),
                            selectedItem = selectedLogin,
                            items = users,
                            selectedValue = {
                                selectedLogin.value = it
                            },
                            textAlign = TextAlign.Start,
                            fontSize = 32
                        )
                    }
                    Spacer(Modifier.size(16.dp))
                    Column(
                        modifier = Modifier.width(460.dp)
                    ) {
                        Text(
                            text = "Пароль",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        TextField(
                            textStyle = TextStyle.Default.copy(
                                fontSize = 32.sp
                            ),
                            value = password.value,
                            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                            singleLine = true,
                            onValueChange = { password.value = it },
                            isError = passwordErrorState,
                            modifier = Modifier.width(560.dp).focusTarget().onKeyEvent {
                                if (it.key == Key.Enter) {
                                    tryLogin()
                                    true
                                } else {
                                    false
                                }
                            },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Lock,
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    Text(text = "Введите пароль")
                                }
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
                        )
                    }
                    if (passwordErrorState) {
                        Text(text = "Ошибка", color = MaterialTheme.colors.primary)
                    } else {
                        Text(text = "")
                    }
                    Spacer(Modifier.size(32.dp))
                    Button(
                        modifier = Modifier.width(480.dp).height(64.dp).padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(color = Color.Transparent),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        shape = RoundedCornerShape(32.dp),
                        contentPadding = PaddingValues(),
                        onClick = {
                            tryLogin()
                        },
                    ) {
                        Box(
                            modifier = Modifier
                                .background(brush = gradient, shape = RoundedCornerShape(32.dp))
                                .then(
                                    Modifier.width(480.dp).height(64.dp).padding(horizontal = 16.dp, vertical = 8.dp)
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "ВОЙТИ",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(Modifier.size(16.dp))
                    Button(
                        modifier = Modifier.width(240.dp).height(64.dp).padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(color = Color.Transparent),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        shape = RoundedCornerShape(32.dp),
                        contentPadding = PaddingValues(),
                        onClick = {
                            onExit()
                        },
                    ) {
                        Box(
                            modifier = Modifier
                                .background(brush = gradient2, shape = RoundedCornerShape(32.dp))
                                .then(
                                    Modifier.width(480.dp).height(64.dp).padding(horizontal = 16.dp, vertical = 8.dp)
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "ВЫЙТИ",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            })
    }
}
