package ru.avem.viu35.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import ru.avem.composables.HomeScreenDrawer
import ru.avem.viu35.composables.MainScreenActionBar
import ru.avem.viu35.viewmodels.MainScreenViewModel

object MainScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()
        val viewModel = rememberScreenModel { MainScreenViewModel() }

        Scaffold(
            scaffoldState = scaffoldState,
            drawerContent = {
                HomeScreenDrawer()
            },
            drawerShape = object : Shape {
                override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
                    return Outline.Rectangle(Rect(offset = Offset.Zero, size = Size(400f, Float.MAX_VALUE)))
                }
            },
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (scaffoldState.drawerState.isClosed) {
                                        scaffoldState.drawerState.open()
                                    } else {
                                        scaffoldState.drawerState.close()
                                    }
                                }
                            }
                        ) { Icon(Icons.Filled.Menu, contentDescription = null) }
                    },
                    title = {
                        Text("ВИУ-35")
                    },
                    actions = {
                        MainScreenActionBar(navigator, viewModel) {
                        }
                    }
                )
            }
        ) {
            Row(modifier = Modifier.fillMaxSize().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(
                    modifier = Modifier.weight(0.7f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(modifier = Modifier.weight(0.5f).fillMaxWidth(), elevation = 4.dp) {
                        Column {
                            Text("ШАБЛОНЫ", modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.caption)
                        }
                    }
                    Card(modifier = Modifier.weight(0.5f).fillMaxWidth(), elevation = 4.dp) {
                        Column {
                            Text(
                                "ИЗМЕРЕНИЯ",
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.caption
                            )
                        }
                    }
                }
            }
        }
    }
}
