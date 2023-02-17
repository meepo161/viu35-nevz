package ru.avem.viu35.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.mouseClickable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import ru.avem.composables.HomeScreenDrawer
import ru.avem.viu35.composables.ComboBox
import ru.avem.viu35.composables.MainScreenActionBar
import ru.avem.viu35.viewmodels.MainScreenViewModel

object MainScreen : Screen {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()
        val viewModel = rememberScreenModel { MainScreenViewModel() }

        var list = listOf<String>(
            "Резистор токоограничивающий РТ-45 6TC.277.045",
            "Блок резисторов высоковольтной цепи БРВЦ-46 6TC.277.046"
        )
        var listState = remember { mutableStateOf(list[0]) }

        var imageSize = mutableStateOf(DpSize(128.dp, 128.dp))
        var imageVisibleState = mutableStateOf(false)

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
            if (imageVisibleState.value) {
                Image(
                    modifier = Modifier.fillMaxSize().mouseClickable {
                        if (buttons.isPrimaryPressed) {
                            imageVisibleState.value = false
                        }
                    },
                    painter = painterResource("unnamed.jpg"),
                    contentDescription = "Глад"
                )
            } else {
                Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Column(modifier = Modifier.weight(0.3f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            text = "Имя и тип аппарата"
                        )
                        ComboBox(
                            selectedItem = listState,
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            onDismissState = {},
                            items = list,
                            selectedValue = { list }
                        )
                        Column(modifier = Modifier) {
                            Column(
                                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Напряжение ВИУ, В")
                                OutlinedTextField(
                                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                    textStyle = TextStyle.Default.copy(fontSize = 28.sp, textAlign = TextAlign.Center),
                                    value = mutableStateOf("7000").value,
                                    onValueChange = {})
                            }
                            Column(
                                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Напряжение Мегер, В")
                                OutlinedTextField(
                                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                    textStyle = TextStyle.Default.copy(fontSize = 28.sp, textAlign = TextAlign.Center),
                                    value = mutableStateOf("1500").value,
                                    onValueChange = {})
                            }
                            Column(
                                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Ток утечки, мА")
                                OutlinedTextField(
                                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                    textStyle = TextStyle.Default.copy(fontSize = 28.sp, textAlign = TextAlign.Center),
                                    value = mutableStateOf("20").value,
                                    onValueChange = {})
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.weight(0.5f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Точка 1, №")
                                OutlinedTextField(
                                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                    textStyle = TextStyle.Default.copy(fontSize = 28.sp, textAlign = TextAlign.Center),
                                    value = mutableStateOf("3").value,
                                    onValueChange = {})
                            }
                            Column(
                                modifier = Modifier.weight(0.5f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Точка 2, №")
                                OutlinedTextField(
                                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                    textStyle = TextStyle.Default.copy(fontSize = 28.sp, textAlign = TextAlign.Center),
                                    value = mutableStateOf("Г").value,
                                    onValueChange = {})
                            }
                        }
                        Image(
                            modifier = Modifier.fillMaxWidth().clickable {
                                imageVisibleState.value = true
                            }.height(512.dp),
                            painter = painterResource("unnamed.jpg"),
                            contentDescription = "Глад"
                        )
                    }
                    Column(modifier = Modifier.weight(0.7f)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                            TextH5("№", modifier = Modifier.width(128.dp))

                            TextH5("Заводской номер")

                            Column {
                                Text(text = "ВИУ", fontSize = 28.sp)
                                Checkbox(
                                    checked = true,
                                    onCheckedChange = null,
                                    modifier = Modifier.scale(2f).size(64.dp)
                                )
                            }
                            TextH5(text = "Ток утечки, мА")

                            Column {
                                Text(text = "Мегер", fontSize = 28.sp)
                                Checkbox(
                                    checked = false,
                                    onCheckedChange = null,
                                    modifier = Modifier.scale(2f).size(64.dp)
                                )
                            }
                            TextH5("R изоляции, МОм")
                        }
                        repeat(10) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                                TextH5("${it + 1}", modifier = Modifier.width(128.dp))

                                OutlinedTextField(
                                    modifier = Modifier.padding(8.dp),
                                    textStyle = TextStyle.Default.copy(fontSize = 28.sp, textAlign = TextAlign.Center),
                                    value = mutableStateOf("${(0..111111111).random()}").value,
                                    onValueChange = {})

                                Checkbox(
                                    checked = true,
                                    onCheckedChange = null,
                                    modifier = Modifier.scale(2f).size(64.dp)
                                )

                                OutlinedTextField(
                                    modifier = Modifier.padding(8.dp),
                                    textStyle = TextStyle.Default.copy(fontSize = 28.sp, textAlign = TextAlign.Center),
                                    value = mutableStateOf("${(1..15).random()}").value,
                                    onValueChange = {})

                                Checkbox(
                                    checked = false,
                                    onCheckedChange = null,
                                    modifier = Modifier.scale(2f).size(64.dp)
                                )

                                OutlinedTextField(
                                    modifier = Modifier.padding(8.dp),
                                    textStyle = TextStyle.Default.copy(fontSize = 28.sp, textAlign = TextAlign.Center),
                                    value = mutableStateOf("").value,
                                    onValueChange = {})
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun TextH5(text: String, modifier: Modifier = Modifier) {
        OutlinedTextField(
            modifier = modifier.padding(8.dp),
            textStyle = TextStyle.Default.copy(fontSize = 28.sp, textAlign = TextAlign.Center),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            value = mutableStateOf(text).value,
            onValueChange = {})
    }
}
