package ru.avem.viu35.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.avem.viu35.composables.ScrollableLazyColumn
import ru.avem.viu35.composables.TableView
import ru.avem.viu35.composables.TestObjectListItem
import ru.avem.viu35.database.entities.TestItemField
import ru.avem.viu35.viewmodels.ObjectEditorViewModel

object ObjectEditorScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val isExpandedDropDownMenu = mutableStateOf(false)
        val vm = rememberScreenModel { ObjectEditorViewModel() }

        Scaffold(topBar = {
            TopAppBar(title = { Text("База данных испытываемых аппаратов") }, navigationIcon = {
                IconButton(onClick = {
                    navigator.popUntilRoot()
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
            })
        }) {
            AnimatedVisibility(vm.objects.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.3f).padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ScrollableLazyColumn(
                            modifier = Modifier.padding(4.dp).weight(0.6f),
                        ) {
                            items(vm.objects.size) {
                                TestObjectListItem(
                                    modifier = Modifier.background(
                                        if (vm.selectedObject.value?.id == vm.objects[it].id) {
                                            MaterialTheme.colors.primary
                                        } else {
                                            MaterialTheme.colors.background
                                        }
                                    ),
                                    text = "${vm.objects[it].name} ${vm.objects[it].type}",
                                    onClick = {
                                        vm.onTestObjectSelected(vm.objects[it])
                                    })
                            }
                        }
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                            ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.Image, contentDescription = null)
                                Text(text = "Просмотреть чертеж")
                            }
                        }
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                            ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                                Text(text = "Создать новый")
                            }
                        }
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                            ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = null)
                                Text(text = "Копировать")
                            }
                        }
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                            ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
                                Text(text = "Редактировать")
                            }
                        }
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                            ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                                Text(text = "Удалить")
                            }
                        }
                    }
                    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Box(modifier = Modifier.weight(0.8f)) {
                            TableView(
                                selectedItem = vm.selectedField.value,
                                items = vm.objectFields,
                                columns = listOf(
                                    TestItemField::key,
                                    TestItemField::nameTest,
                                    TestItemField::uViu,
                                    TestItemField::time,
                                    TestItemField::uMeger,
                                    TestItemField::current,
                                ),
                                columnNames = listOf(
                                    "№", "Наименование", "U ВИУ, В", "Время, с", "U мегер, В", "Ток утечки, мА"
                                ),
                                onItemPrimaryPressed = { vm.selectedField.value = vm.objectFields[it] },
                                onItemSecondaryPressed = { vm.selectedField.value = vm.objectFields[it] },
                                contextMenuContent = {
                                    DropdownMenuItem(onClick = {
                                        isExpandedDropDownMenu.value = false
                                        // navigator.push(TestsEditorScreen) TODO
                                    }) {
                                        Text("Редактировать")
                                    }
                                    DropdownMenuItem(onClick = {
                                        vm.onObjectFieldDelete()
                                        isExpandedDropDownMenu.value = false
                                    }) {
                                        Text("Удалить")
                                    }
                                },
                                isExpandedDropdownMenu = isExpandedDropDownMenu
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {},
                                modifier = Modifier.weight(0.5f).height(96.dp),
                                elevation = ButtonDefaults.elevation(
                                    defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                                ),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.North, contentDescription = null)
                                    Text(text = "Переместить выше")
                                }
                            }
                            Button(
                                onClick = {},
                                modifier = Modifier.weight(0.5f).height(96.dp),
                                elevation = ButtonDefaults.elevation(
                                    defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                                ),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.South, contentDescription = null)
                                    Text(text = "Переместить ниже")
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {},
                                modifier = Modifier.weight(0.25f).height(72.dp),
                                elevation = ButtonDefaults.elevation(
                                    defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                                ),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                                    Text(text = "Добавить")
                                }
                            }
                            Button(
                                onClick = {},
                                modifier = Modifier.weight(0.25f).height(72.dp),
                                elevation = ButtonDefaults.elevation(
                                    defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                                ),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
                                    Text(text = "Редактировать")
                                }
                            }
                            Button(
                                onClick = {},
                                modifier = Modifier.weight(0.25f).height(72.dp),
                                elevation = ButtonDefaults.elevation(
                                    defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                                ),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = null)
                                    Text(text = "Копировать")
                                }
                            }
                            Button(
                                onClick = {},
                                modifier = Modifier.weight(0.25f).height(72.dp),
                                elevation = ButtonDefaults.elevation(
                                    defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                                ),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                                    Text(text = "Удалить")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}