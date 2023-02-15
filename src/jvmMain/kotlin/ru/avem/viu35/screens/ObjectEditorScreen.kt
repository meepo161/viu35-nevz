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
import ru.avem.viu35.composables.ScrollableLazyColumn
import ru.avem.viu35.composables.TestObjectListItem
import ru.avem.viu35.database.entities.TestItem
import ru.avem.viu35.database.getAllTestItems

object ObjectEditorScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val objects = mutableStateListOf<TestItem>()
        val name = remember { mutableStateOf("") }
        val type = remember { mutableStateOf("") }

        LaunchedEffect(objects) {
            launch {
                objects.addAll(getAllTestItems())
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
                            objects.sortedBy { it.name }.forEach {
                                item {
                                    TestObjectListItem(text = "${it.name} ${it.type}")
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
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                                Text(text = "Удалить")
                            }
                        }
                    }
                    Column(modifier = Modifier.fillMaxWidth(0.6f).padding(8.dp)) {
                        ScrollableLazyColumn(
                            modifier = Modifier.padding(4.dp).weight(0.6f),
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    Text(text = "Имя аппарата:")
                                    TextField(value = name.value, onValueChange = { name.value = it })
                                }
                            }
                            item {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    Text(text = "Тип аппарата:")
                                    TextField(value = type.value, onValueChange = { type.value = it })
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}