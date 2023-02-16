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
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.viu35.composables.ScrollableLazyColumn
import ru.avem.viu35.composables.TableView
import ru.avem.viu35.composables.TestObjectListItem
import ru.avem.viu35.database.entities.*
import ru.avem.viu35.database.getAllTestItems
import ru.avem.viu35.viewmodels.ObjectEditorViewModel

object ObjectEditorScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val isExpandedDropDownMenu = mutableStateOf(false)
        val vm = rememberScreenModel { ObjectEditorViewModel() }

        LaunchedEffect(vm.objects) {
            launch {
                vm.objects.addAll(getAllTestItems())
                transaction {
                    vm.objects.forEach {
                        val listTests = mutableListOf<TestItemFieldScheme>()
                        it.fields.values.forEach {
                            listTests.add(TestItemFieldScheme(it.key, it.dot1, it.dot2, it.description))
                        }
                        vm.objectsState.add(TestItemScheme(name = it.name, type = it.type, tests = listTests))
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
            AnimatedVisibility(vm.objects.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.3f).padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ScrollableLazyColumn(
                            modifier = Modifier.padding(4.dp).weight(0.6f),
                        ) {
                            vm.objectsState.sortedBy { it.name }.forEach {
                                item {
                                    TestObjectListItem(
                                        text = "${it.name} ${it.type}",
                                        click = { vm.tvm.value = it }
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
                            selectedItem = vm.currentTVM.value,
                            items = vm.tvm.value.tests,
                            columns = listOf(
                                TestItemFieldScheme::key,
                                TestItemFieldScheme::dot1,
                                TestItemFieldScheme::dot2,
                                TestItemFieldScheme::description,
                            ),
                            columnNames = listOf("№ Проверки", "Первая точка", "Вторая точка", "Описание"),
                            onItemPrimaryPressed = { vm.currentTVM.value = vm.tvm.value.tests[it] },
                            onItemSecondaryPressed = { vm.currentTVM.value = vm.tvm.value.tests[it] },
                            contextMenuContent = {
                                DropdownMenuItem(onClick = {
                                    isExpandedDropDownMenu.value = false
                                    // navigator.push(TestsEditorScreen) TODO
                                }) {
                                    Text("Редактировать")
                                }
                                DropdownMenuItem(onClick = {
//                                    transaction {
//                                        TestItemFields.deleteWhere {TestItemFields.key eq vm.currentTVM.value.key }
//                                    } TODO
                                    isExpandedDropDownMenu.value = false
                                    vm.currentTVM.value = vm.tvm.value.tests.first()
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