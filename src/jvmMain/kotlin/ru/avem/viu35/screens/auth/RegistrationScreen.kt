package ru.avem.viu35.screens.auth

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.viu35.composables.ConfirmDialog
import ru.avem.viu35.database.DBManager
import ru.avem.viu35.database.entities.User
import ru.avem.viu35.screens.MainScreen
import ru.avem.viu35.utils.keyEventNext
import ru.avem.viu35.utils.keyboardActionNext
import ru.avem.viu35.viewmodels.UserEditorViewModel

class RegistrationScreen(private var vm: UserEditorViewModel) : Screen {

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun Content() {
        val localNavigator = LocalNavigator.currentOrThrow
        val focusManager = LocalFocusManager.current

        val scope = CoroutineScope(Dispatchers.Default)
        var name by remember { mutableStateOf(TextFieldValue()) }
        var password by remember { mutableStateOf(TextFieldValue()) }
        var confirmPassword by remember { mutableStateOf(TextFieldValue()) }

        var nameErrorState by remember { mutableStateOf(false) }
        var loginErrorState by remember { mutableStateOf(false) }
        var passwordErrorState by remember { mutableStateOf(false) }
        var confirmPasswordErrorState by remember { mutableStateOf(false) }

        val dialogVisibleState = mutableStateOf(false)
        var titleDialog = mutableStateOf("")
        var textDialog = mutableStateOf("")

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Регистрация") }, navigationIcon = {
                    IconButton(
                        onClick = {
                            localNavigator.pop()
                        },
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                })
            },
            content = {
                if (dialogVisibleState.value) {
                    ConfirmDialog(
                        title = titleDialog.value,
                        text = textDialog.value,
                        yesCallback = { dialogVisibleState.value = false },
                        noCallback = { dialogVisibleState.value = false })
                }
                Column(
                    modifier = Modifier.padding(32.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Text(text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MaterialTheme.colors.primary)) {
                            append("Р")
                        }
                        append("егистрация")
                    }, fontSize = 30.sp)
                    Spacer(Modifier.size(16.dp))
                    OutlinedTextField(
                        textStyle = TextStyle.Default.copy(
                            fontSize = 20.sp
                        ),
                        value = name,
                        onValueChange = {
                            if (nameErrorState) {
                                nameErrorState = false
                            }
                            name = it
                        },

                        modifier = Modifier.focusTarget().onPreviewKeyEvent {
                            keyEventNext(it, focusManager)
                        },
                        isError = nameErrorState,
                        label = {
                            Text(text = "ФИО*")
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = keyboardActionNext(focusManager)
                    )
                    if (nameErrorState) {
                        Text(text = "Обязательно", color = MaterialTheme.colors.error)
                    }
                    Spacer(Modifier.size(16.dp))
                    var passwordVisibility by remember { mutableStateOf(true) }
                    var cPasswordVisibility by remember { mutableStateOf(true) }
                    OutlinedTextField(
                        textStyle = TextStyle.Default.copy(
                            fontSize = 20.sp
                        ),
                        value = password,
                        onValueChange = {
                            if (passwordErrorState) {
                                passwordErrorState = false
                            }
                            password = it
                        },
                        modifier = Modifier.focusTarget().onPreviewKeyEvent {
                            keyEventNext(it, focusManager)
                        },
                        label = {
                            Text(text = "Пароль*")
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                passwordVisibility = !passwordVisibility
                                cPasswordVisibility = !cPasswordVisibility
                            }) {
                                Icon(
                                    imageVector = if (passwordVisibility) Icons.Default.RemoveRedEye else Icons.Default.Clear,
                                    contentDescription = "visibility",
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = keyboardActionNext(focusManager),
                        isError = passwordErrorState,
                        visualTransformation = if (passwordVisibility) PasswordVisualTransformation() else VisualTransformation.None
                    )
                    if (passwordErrorState) {
                        Text(text = "Обязательно", color = MaterialTheme.colors.error)
                    }

                    Spacer(Modifier.size(16.dp))
                    OutlinedTextField(
                        textStyle = TextStyle.Default.copy(
                            fontSize = 20.sp
                        ),
                        value = confirmPassword,
                        onValueChange = {
                            if (confirmPasswordErrorState) {
                                confirmPasswordErrorState = false
                            }
                            confirmPassword = it
                        },
                        modifier = Modifier.focusTarget().onPreviewKeyEvent {
                            keyEventNext(it, focusManager)
                        },
                        isError = confirmPasswordErrorState,
                        label = {
                            Text(text = "Повторите пароль*")
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                cPasswordVisibility = !cPasswordVisibility
                                passwordVisibility = !passwordVisibility
                            }) {
                                Icon(
                                    imageVector = if (cPasswordVisibility) Icons.Default.RemoveRedEye else Icons.Default.Clear,
                                    contentDescription = "visibility",
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = keyboardActionNext(focusManager),
                        visualTransformation = if (cPasswordVisibility) PasswordVisualTransformation() else VisualTransformation.None
                    )
                    if (confirmPasswordErrorState) {
                        val msg = when {
                            confirmPassword.text.isEmpty() -> "Обязательно"
                            confirmPassword.text != password.text -> "Пароли не совпадают"
                            else -> ""
                        }
                        Text(text = msg, color = MaterialTheme.colors.error)
                    }
                    Spacer(Modifier.size(16.dp))
                    Button(
                        onClick = {
                            when {
                                name.text.isEmpty() -> {
                                    nameErrorState = true
                                }

                                password.text.isEmpty() -> {
                                    passwordErrorState = true
                                }

                                confirmPassword.text.isEmpty() -> {
                                    confirmPasswordErrorState = true
                                }

                                confirmPassword.text != password.text -> {
                                    confirmPasswordErrorState = true
                                }

                                else -> {
                                    if (DBManager.getAllUsers().any { it.name == name.text }) {
                                        titleDialog.value = "Ошибка"
                                        textDialog.value = "Такой пользователь уже существует"
                                        dialogVisibleState.value = true
                                    } else {
                                        scope.launch {
                                            transaction {
                                                User.new {
                                                    this.name = name.text
                                                    this.password = password.text
                                                }
                                            }
                                            vm.allUsers.value = DBManager.getAllUsers()
                                        }
                                        localNavigator.pop()
                                    }
                                }
                            }
                        },
                        content = {
                            Text(
                                text = "Зарегистрироваться",
                                color = MaterialTheme.colors.surface,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                    )
                    Spacer(Modifier.size(16.dp))
                    Row(horizontalArrangement = Arrangement.Center) {
                        TextButton(onClick = {
                            localNavigator.pop()
                        }) {
                            Text(
                                text = "Назад",
                                color = MaterialTheme.colors.primary,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            })
    }
}
