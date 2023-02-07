package ru.avem.viu35.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.exposed.sql.transactions.transaction

object ObjectEditorScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
//        val testObjects by lazy {
//            transaction {
//                TestObject.all().toMutableList()
//            }
//        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("База данных испытываемых аппаратов") },
                    actions = {
                        IconButton(onClick = {
//                            navigator.push(ObjectDetailsScreen(
//                                transaction {
//                                    TestObject.new {
//                                        name = "-"
//                                        distance = 1000
//                                        viuVoltage0 = 1000
//                                        viuTime0 = 60
//                                        viuVoltage1 = 1000
//                                        viuTime1 = 60
//                                        viuVoltage2 = 1000
//                                        viuTime2 = 60
//                                        viuVoltage3 = 1000
//                                        viuTime3 = 60
//                                        impulseVoltage = 0
//                                        impulseTime = 5000
//                                        meggerVoltage = 2500
//                                        slabCount = 360
//                                    }
//                                }
//                            ))
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navigator.pop()
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = null)
                        }
                    })
            }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                testObjects.forEach {
//                    item {
//                        TestObjectListItem(it)
//                    }
//                }
            }
        }
    }
}