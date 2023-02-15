package ru.avem.viu35.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.viu35.composables.ScrollableLazyColumn
import ru.avem.viu35.composables.TableView
import ru.avem.viu35.composables.TestObjectListItem
import ru.avem.viu35.database.entities.TestItem
import ru.avem.viu35.database.entities.TestItemFieldScheme
import ru.avem.viu35.database.entities.TestItemScheme
import ru.avem.viu35.database.getAllTestItems
import ru.avem.viu35.futureTVM

object ObjectEditorScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val objects = mutableStateListOf<TestItem>()
        val objectsState = mutableStateListOf<TestItemScheme>()
        val objectsTableVIewState = mutableStateOf(listOf<TestItemFieldScheme>())
        val name = remember { mutableStateOf("") }
        val type = remember { mutableStateOf("") }
        var isExpandedDropDownMenu = mutableStateOf(false)
        futureTVM.value = listOf(TestItemFieldScheme(0, "", "", ""))

        LaunchedEffect(objects) {
            launch {
                objects.addAll(getAllTestItems())
                transaction {
                    objects.forEach {
                        val listTests = mutableListOf<TestItemFieldScheme>()
                        var key = 0
                        it.fields.values.forEach {
                            listTests.add(TestItemFieldScheme(key++, it.dot1, it.dot2, it.description))
                        }
                        objectsState.add(TestItemScheme(name = it.name, type = it.type, tests = listTests))
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("База данных испытываемых аппаратов") },
                    navigationIcon = {
                        IconButton(onClick = {
                            navigator.pop()
                            navigator.pop()
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = null)
                        }
                    })
            }
        ) {
            AnimatedVisibility(objects.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.3f).padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ScrollableLazyColumn(
                            modifier = Modifier.padding(4.dp).weight(0.6f),
                        ) {
                            objectsState.sortedBy { it.name }.forEach {
                                item {
                                    TestObjectListItem(
                                        text = "${it.name} ${it.type}",
                                        testObject = it
                                    )
                                }
                            }
                        }
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 10.dp,
                                pressedElevation = 15.dp,
                                disabledElevation = 0.dp
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
                                defaultElevation = 10.dp,
                                pressedElevation = 15.dp,
                                disabledElevation = 0.dp
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
                                defaultElevation = 10.dp,
                                pressedElevation = 15.dp,
                                disabledElevation = 0.dp
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
                                defaultElevation = 10.dp,
                                pressedElevation = 15.dp,
                                disabledElevation = 0.dp
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
                        TableView(
                            selectedItem = futureTVM.value.first(),
                            items = futureTVM.value,
                            columns = listOf(
                                TestItemFieldScheme::key,
                                TestItemFieldScheme::dot1,
                                TestItemFieldScheme::dot2,
                                TestItemFieldScheme::description,
                            ),
                            columnNames = listOf("№ Проверки","Первая точка", "Вторая точка", "Описание"),
                            onItemPrimaryPressed = { /*currentTests = tests[it]*/ },
                            onItemSecondaryPressed = { /*currentTests = tests[it]*/ },
                            contextMenuContent = {
                                DropdownMenuItem(onClick = {
                                    isExpandedDropDownMenu.value = false
                                    // navigator.push(TestsEditorScreen) TODO
                                }) {
                                    Text("Редактировать")
                                }
                                DropdownMenuItem(onClick = {
//                                    tests.remove(currentTests)
//                                    currentTests = tests.first()
                                }) {
                                    Text("Удалить")
                                }
                            },
                            isExpandedDropdownMenu = isExpandedDropDownMenu
                        )
                    }
                }
            }
        }
    }
}