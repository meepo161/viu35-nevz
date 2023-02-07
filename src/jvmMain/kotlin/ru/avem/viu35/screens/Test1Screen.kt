package ru.avem.viu35.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import java.lang.Thread.sleep
import kotlin.concurrent.thread

object Test1Screen : Screen {
    @Composable
    override fun Content() {
        var counter by remember { mutableStateOf("0") }

        val testController = remember {
            object {
                var isExperimentRunning = false

                init {
                    println("NIGGS")
                }

                fun start() {
                    isExperimentRunning = true
                    thread {
                        repeat(100) {
                            counter = it.toString()
                            sleep(1000)
                        }
                    }
                }
            }
        }

        Column {
            Text(counter)
            if (!testController.isExperimentRunning) {
                Button(onClick = {
                    testController.start()
                }) {
                    Text("START")
                }
            }
        }
    }
}