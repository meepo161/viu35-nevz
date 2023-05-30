package ru.avem.viu35.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.avem.viu35.composables.ConfirmDialog
import ru.avem.viu35.composables.TableView
import ru.avem.viu35.database.entities.User
import ru.avem.viu35.screens.auth.RegistrationScreen
import ru.avem.viu35.viewmodels.UserEditorViewModel

class UserEditorScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val isExpandedDropDownMenu = mutableStateOf(false)
        val vm = rememberScreenModel { UserEditorViewModel() }

        MaterialTheme {
            Scaffold(topBar = {
                TopAppBar(title = { Text("База данных пользователей") }, navigationIcon = {
                    IconButton(onClick = {
                        navigator.pop()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                })
            }) {
                if (vm.dialogVisibleState.value) {
                    ConfirmDialog(
                        vm.titleDialog.value,
                        vm.textDialog.value,
                        { vm.dialogVisibleState.value = false },
                        { vm.dialogVisibleState.value = false })
                }

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(modifier = Modifier.width(800.dp).padding(16.dp), verticalArrangement = Arrangement.Center) {
                        Box(modifier = Modifier.weight(0.9f)) {
                            TableView(
                                selectedItem = vm.selectedUser.value,
                                items = vm.allUsers.value,
                                columns = listOf(
                                    User::name
                                ),
                                columnNames = listOf(
                                    "Фамилия Имя Отчество"
                                ),
                                onItemPrimaryPressed = {
                                    vm.selectedUser.value = vm.allUsers.value[it]
                                },
                                onItemSecondaryPressed = {
                                    vm.selectedUser.value = vm.allUsers.value[it]
                                },
                                contextMenuContent = {
                                    DropdownMenuItem(onClick = {
                                        vm.deleteUser()
                                        isExpandedDropDownMenu.value = false
                                    }) {
                                        Text("Удалить")
                                    }
                                },
                                isExpandedDropdownMenu = isExpandedDropDownMenu
                            )
                        }
                        Row(modifier = Modifier.weight(0.1f), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                            Button(
                                modifier = Modifier.weight(1 / 2f).height(128.dp),
                                onClick = {
                                    navigator.push(RegistrationScreen(vm))
                                },
                                elevation = ButtonDefaults.elevation(
                                    defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(text = "Добавить", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                    Icon(
                                        imageVector = Icons.Filled.PersonAdd,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                            Button(
                                modifier = Modifier.weight(1 / 2f).height(128.dp),
                                onClick = {
                                    vm.deleteUser()
                                },
                                elevation = ButtonDefaults.elevation(
                                    defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(text = "Удалить", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                    Icon(
                                        imageVector = Icons.Filled.PersonRemove,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}