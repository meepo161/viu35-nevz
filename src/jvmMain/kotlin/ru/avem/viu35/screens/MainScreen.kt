package ru.avem.viu35.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.avem.composables.HomeScreenDrawer
import ru.avem.viu35.composables.ComboBox
import ru.avem.viu35.composables.MainScreenActionBar
import ru.avem.viu35.viewmodels.MainScreenViewModel

@Suppress("FunctionName")
object MainScreen : Screen {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()
        val vm = rememberScreenModel { MainScreenViewModel() }

        Scaffold(scaffoldState = scaffoldState, drawerContent = {
            HomeScreenDrawer()
        }, drawerShape = object : Shape {
            override fun createOutline(
                size: Size, layoutDirection: LayoutDirection, density: Density
            ): Outline {
                return Outline.Rectangle(Rect(offset = Offset.Zero, size = Size(480f, Float.MAX_VALUE)))
            }
        }, topBar = {
            TopAppBar(navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        if (scaffoldState.drawerState.isClosed) {
                            scaffoldState.drawerState.open()
                        } else {
                            scaffoldState.drawerState.close()
                        }
                    }
                }) { Icon(Icons.Filled.Menu, contentDescription = null) }
            }, title = {
                Text("ВИУ-35")
            }, actions = {
                MainScreenActionBar(navigator, vm) {}
            })
        }) {
            if (vm.imageVisibleState.value) {
                Image(
                    modifier = Modifier.fillMaxSize().onClick(matcher = PointerMatcher.mouse(PointerButton.Primary),
                        keyboardModifiers = { true },
                        onClick = {
                            vm.imageVisibleState.value = false
                        }), painter = painterResource("unnamed.jpg"), contentDescription = "Глад"
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LeftPanel(modifier = Modifier.weight(0.4f), vm)
                    RighPanel(modifier = Modifier.weight(0.6f), vm)
                }
            }
        }
    }

    @Composable
    private fun LeftPanel(modifier: Modifier, vm: MainScreenViewModel) {
        Card(modifier = modifier, elevation = 4.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(elevation = 4.dp) {
                    Column(
                        modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            text = "Имя и тип аппарата"
                        )
                        ComboBox(selectedItem = vm.selectedObject,
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            onDismissState = {},
                            items = vm.objects,
                            selectedValue = { vm.onTestObjectSelected(it!!) })

                        ComboBox(selectedItem = vm.selectedField,
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            onDismissState = {},
                            items = vm.objectFields,
                            selectedValue = { vm.onTestObjectFieldSelected(it!!) })
                    }
                }
                Card(elevation = 4.dp) {
                    Column(
                        modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            text = "Заданные параметры:"
                        )
                        Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                            Box(
                                modifier = Modifier.weight(0.7f).height(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Напряжение ВИУ, В",
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Box(modifier = Modifier.weight(0.3f)) {
                                OutlinedTextField(textStyle = TextStyle.Default.copy(
                                    fontSize = 20.sp, textAlign = TextAlign.Center
                                ), value = vm.specifiedUViu.value, onValueChange = {})
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                            Box(
                                modifier = Modifier.weight(0.7f).height(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Напряжение Мегер, В",
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Box(modifier = Modifier.weight(0.3f)) {
                                OutlinedTextField(textStyle = TextStyle.Default.copy(
                                    fontSize = 20.sp, textAlign = TextAlign.Center
                                ), value = vm.specifiedUMeger.value, onValueChange = {})
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                            Box(
                                modifier = Modifier.weight(0.7f).height(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Ток утечки, мА", fontSize = 20.sp, textAlign = TextAlign.Center
                                )
                            }
                            Box(modifier = Modifier.weight(0.3f)) {
                                OutlinedTextField(textStyle = TextStyle.Default.copy(
                                    fontSize = 20.sp, textAlign = TextAlign.Center
                                ), value = vm.specifiedI.value, onValueChange = {})
                            }
                        }
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            text = "Измеренные параметры:"
                        )
                        Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                            Box(
                                modifier = Modifier.weight(0.7f).height(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Напряжение ВИУ, В",
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Box(modifier = Modifier.weight(0.3f)) {
                                OutlinedTextField(textStyle = TextStyle.Default.copy(
                                    fontSize = 20.sp, textAlign = TextAlign.Center
                                ), value = vm.measuredUViu.value, onValueChange = {})
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                            Box(
                                modifier = Modifier.weight(0.7f).height(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Оставшееся время, сек",
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Box(modifier = Modifier.weight(0.3f)) {
                                OutlinedTextField(textStyle = TextStyle.Default.copy(
                                    fontSize = 20.sp, textAlign = TextAlign.Center
                                ), value = vm.measuredTime.value, onValueChange = {})
                            }
                        }
                    }
                }
                Card(elevation = 4.dp) {
                    Column(
                        modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.weight(0.5f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Точка 1, №",
                                )
                                OutlinedTextField(modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                    textStyle = TextStyle.Default.copy(
                                        fontSize = 20.sp, textAlign = TextAlign.Center
                                    ),
                                    value = vm.dot1.value,
                                    onValueChange = {})
                            }
                            Column(
                                modifier = Modifier.weight(0.5f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Точка 2, №",
                                )
                                OutlinedTextField(modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                    textStyle = TextStyle.Default.copy(
                                        fontSize = 20.sp, textAlign = TextAlign.Center
                                    ),
                                    value = vm.dot2.value,
                                    onValueChange = {})
                            }
                        }
                        Image(
                            modifier = Modifier.fillMaxWidth().clickable {
                                vm.imageVisibleState.value = true
                            }.height(512.dp),
                            painter = painterResource("unnamed.jpg"),
                            contentDescription = "Глад"
                        )
                    }
                }
            }
        }
    }

    @Composable
    @OptIn(DelicateCoroutinesApi::class)
    private fun RighPanel(modifier: Modifier, vm: MainScreenViewModel) {
        Card(modifier = modifier, elevation = 4.dp) {
            Column(
                modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(elevation = 4.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextH5("№", modifier = Modifier.width(72.dp))
                        TextH5("Заводской номер")
                        Column(
                            modifier = Modifier.width(96.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "ВИУ", fontSize = 20.sp)
                            TriStateCheckbox(
                                modifier = Modifier.scale(2f).size(48.dp),
                                state = vm.allCheckBoxesViu.value,
                                onClick = {
                                    vm.onClickTriStateCheckbox(
                                        vm.allCheckBoxesViu,
                                        vm.listCheckBoxesViu
                                    )
                                },
                            )
                        }
                        TextH5(text = "I, мА", modifier = Modifier.padding(8.dp).width(140.dp))
                        Column(
                            modifier = Modifier.width(96.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Мегер", fontSize = 20.sp)
                            TriStateCheckbox(
                                modifier = Modifier.scale(2f).size(48.dp),
                                state = vm.allCheckBoxesMeger.value,
                                onClick = {
                                    vm.onClickTriStateCheckbox(
                                        vm.allCheckBoxesMeger,
                                        vm.listCheckBoxesMeger
                                    )
                                },
                            )
                        }
                        TextH5("R, МОм", modifier = Modifier.padding(8.dp).width(140.dp))
                    }
                }
                repeat(10) { number ->
                    Card(elevation = 4.dp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            TextH5("${number + 1}", modifier = Modifier.width(72.dp))

                            OutlinedTextField(modifier = Modifier.padding(8.dp).width(280.dp),
                                singleLine = true,
                                textStyle = TextStyle.Default.copy(
                                    fontSize = 20.sp, textAlign = TextAlign.Center
                                ),
                                value = vm.listSerialNumbers[number].value,
                                onValueChange = { vm.listSerialNumbers[number].value = it })

                            Box(
                                modifier = Modifier.width(96.dp), contentAlignment = Alignment.Center
                            ) {
                                Checkbox(
                                    checked = vm.listCheckBoxesViu[number].value,
                                    onCheckedChange = { isChecked ->
                                        vm.listCheckBoxesViu[number].value = isChecked
                                        var selectedCheckBox = 0
                                        vm.listCheckBoxesViu.forEach {
                                            if (it.value) selectedCheckBox++
                                        }
                                        if (selectedCheckBox == vm.listCheckBoxesViu.size) {
                                            vm.allCheckBoxesViu.value = ToggleableState.On
                                        } else if (selectedCheckBox > 0) {
                                            vm.allCheckBoxesViu.value = ToggleableState.Indeterminate
                                        } else {
                                            vm.allCheckBoxesViu.value = ToggleableState.Off
                                        }
                                    },
                                    modifier = Modifier.scale(2f).size(48.dp)
                                )
                            }
                            OutlinedTextField(modifier = Modifier.padding(8.dp).width(140.dp),
                                textStyle = TextStyle.Default.copy(
                                    fontSize = 20.sp, textAlign = TextAlign.Center
                                ),
                                value = vm.listCurrents[number].value,
                                onValueChange = {})
                            Box(
                                modifier = Modifier.width(96.dp), contentAlignment = Alignment.Center
                            ) {
                                Checkbox(
                                    checked = vm.listCheckBoxesMeger[number].value,
                                    onCheckedChange = { isChecked ->
                                        vm.listCheckBoxesMeger[number].value = isChecked
                                        var selectedCheckBox = 0
                                        vm.listCheckBoxesMeger.forEach {
                                            if (it.value) selectedCheckBox++
                                        }
                                        if (selectedCheckBox == vm.listCheckBoxesMeger.size) {
                                            vm.allCheckBoxesMeger.value = ToggleableState.On
                                        } else if (selectedCheckBox > 0) {
                                            vm.allCheckBoxesMeger.value =
                                                ToggleableState.Indeterminate
                                        } else {
                                            vm.allCheckBoxesMeger.value = ToggleableState.Off
                                        }
                                    },
                                    modifier = Modifier.scale(2f).size(48.dp)
                                )
                            }
                            OutlinedTextField(modifier = Modifier.padding(8.dp).width(140.dp),
                                textStyle = TextStyle.Default.copy(
                                    fontSize = 20.sp, textAlign = TextAlign.Center
                                ),
                                value = vm.listRs[number].value,
                                onValueChange = {})
                            Circle(vm.listColors[number].value)
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(64.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1 / 3f).height(128.dp),
                        onClick = {},
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(text = "В меню", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Button(
                        modifier = Modifier.weight(1 / 3f).height(128.dp), onClick = {
                            GlobalScope.launch {
                                while (true) {
                                    vm.listCurrents.forEach {
                                        it.value = (1..100000).random().toString()
                                    }
                                    vm.listColors.forEach {
                                        it.value = Color(
                                        (1..256).random(),
                                        (1..256).random(),
                                        (1..256).random()
                                    )
                                    }
                                    delay(100)
                                }
                            }
                        }, elevation = ButtonDefaults.elevation(
                            defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.PriorityHigh,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(text = "Старт", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Icon(
                                imageVector = Icons.Filled.PriorityHigh,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    Button(
                        modifier = Modifier.weight(1 / 3f).height(128.dp),
                        onClick = {},
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Далее", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @Suppress("FunctionName")
    @Composable
    private fun TextH5(text: String, modifier: Modifier = Modifier) {
        OutlinedTextField(modifier = modifier.padding(8.dp),
            textStyle = TextStyle.Default.copy(fontSize = 20.sp, textAlign = TextAlign.Center),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent
            ),
            value = mutableStateOf(text).value,
            onValueChange = {})
    }

    @Composable
    fun Circle(color: Color) {
        Canvas(modifier = Modifier.size(48.dp), onDraw = {
            drawCircle(color = color)
            drawCircle(color = Color.Black, style = Stroke(width = 2.dp.toPx()))
        })
    }
}
