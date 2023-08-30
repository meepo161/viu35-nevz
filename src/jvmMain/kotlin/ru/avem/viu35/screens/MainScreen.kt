package ru.avem.viu35.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerButton
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
import isTestRunning
import kotlinx.coroutines.launch
import onExit
import org.jetbrains.skia.Image
import ru.avem.viu35.composables.*
import ru.avem.viu35.viewmodels.MainScreenViewModel
import ru.avem.viu35.viewmodels.TestViewModel
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

@Suppress("FunctionName")
object MainScreen : Screen {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()
        val logScrollState = rememberLazyListState()
        val vm = rememberScreenModel { MainScreenViewModel(logScrollState) }
        val testViewModel = rememberScreenModel { TestViewModel(vm, scope) }

        val size = Dimension(800, 600)
        val img = BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until size.width) {
            for (y in 0 until size.height) {
                img.setRGB(x, y, 0xff0000)
            }
        }
        ImageIO.write(img, "BMP", File("test.bmp"))

        Scaffold(scaffoldState = scaffoldState,
            drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
            drawerContent = { HomeScreenDrawer(mainViewModel = vm, isClickable = vm.mutableStateIsRunning) },
            drawerShape = object : Shape {
                override fun createOutline(
                    size: Size, layoutDirection: LayoutDirection, density: Density
                ): Outline {
                    return Outline.Rectangle(
                        Rect(
                            offset = Offset.Zero, size = Size(480f, Float.MAX_VALUE)
                        )
                    )
                }
            },
            topBar = {
                TopAppBar(navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            if (scaffoldState.drawerState.isClosed && !isTestRunning) {
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
            Column(modifier = Modifier.fillMaxSize()) {

                AnimatedVisibility(vm.imageVisibleState.value) {
                    Image(
                        modifier = Modifier.fillMaxSize().onClick(matcher = PointerMatcher.mouse(PointerButton.Primary),
                            keyboardModifiers = { true },
                            onClick = {
                                vm.imageVisibleState.value = false
                            }),
                        contentDescription = "image",
                        bitmap = if (vm.selectedObject.value != null) {
                            Image.Companion.makeFromEncoded(vm.selectedObject.value!!.image.bytes)
                                .toComposeImageBitmap()
                        } else {
                            ImageBitmap(800, 800)
                        }
                    )
                }
                AnimatedVisibility(!vm.imageVisibleState.value) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LeftPanel(modifier = Modifier.weight(0.4f), vm)
                        RightPanel(modifier = Modifier.weight(0.6f), vm, testViewModel)
                    }
                }
            }
            if (vm.dialogVisibleState.value) {
                ConfirmDialog(
                    title = vm.titleDialog.value,
                    text = vm.textDialog.value,
                    nameGif = vm.nameGif.value,
                    yesCallback = { vm.hideDialog() },
                    noCallback = { vm.hideDialog() }
                )
            }
            if (vm.exitDialogVisibleState.value) {
                ConfirmDialog(
                    title = "Выход",
                    text = "Вы собираетесь выйти из приложения",
                    nameGif = "",
                    yesCallback = { onExit() },
                    noCallback = { vm.exitDialogVisibleState.value = false },
                    secondButton = true
                )
            }
        }
    }

    @Composable
    private fun LeftPanel(modifier: Modifier, vm: MainScreenViewModel) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Card(elevation = 4.dp) {
                Column(
                    modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        text = "Тип аппарата и номер чертежа"
                    )
                    ComboBox(selectedItem = vm.selectedObject,
                        modifier = Modifier.width(1000.dp).padding(8.dp),
                        textAlign = TextAlign.Start,
                        isEditable = vm.mutableStateIsRunning.value,
                        onDismissState = {},
                        items = vm.objects,
                        selectedValue = {
                            vm.onTestObjectSelected(it!!)
                        })

                    ComboBox(selectedItem = vm.selectedField,
                        modifier = Modifier.width(1000.dp).padding(8.dp),
                        textAlign = TextAlign.Start,
                        isEditable = vm.selectedObject.value != null && vm.mutableStateIsRunning.value,
                        onDismissState = {},
                        items = vm.objectFields,
                        selectedValue = {
                            vm.onTestObjectFieldSelected(it!!)
                        })
                }
            }
            Card(elevation = 4.dp) {
                Column(
                    modifier = Modifier.weight(0.5f).padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier.weight(0.5f).padding(end = 64.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            text = "Заданные параметры:"
                        )

                        Text(
                            modifier = Modifier.weight(0.5f).padding(start = 64.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            text = "Измеренные параметры:"
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                        Box(modifier = Modifier.weight(0.3f)) {
                            OutlinedTextField(textStyle = TextStyle.Default.copy(
                                fontSize = 20.sp, textAlign = TextAlign.Center
                            ), value = vm.specifiedUViu.value, onValueChange = {})
                        }
                        Box(
                            modifier = Modifier.weight(0.4f).height(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Напряжение TRMS|AMP, В",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Box(modifier = Modifier.weight(0.15f)) {
                            OutlinedTextField(textStyle = TextStyle.Default.copy(
                                fontSize = 20.sp, textAlign = TextAlign.Center
                            ), value = vm.measuredUViu.value, onValueChange = {})
                        }
                        Box(modifier = Modifier.weight(0.15f)) {
                            OutlinedTextField(textStyle = TextStyle.Default.copy(
                                fontSize = 20.sp, textAlign = TextAlign.Center
                            ), value = vm.measuredUViuAmp.value, onValueChange = {})
                        }
                    } //U ВИУ
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                        Box(modifier = Modifier.weight(0.3f)) {
                            OutlinedTextField(textStyle = TextStyle.Default.copy(
                                fontSize = 20.sp, textAlign = TextAlign.Center
                            ), value = vm.specifiedI.value, onValueChange = {})
                        }
                        Box(
                            modifier = Modifier.weight(0.4f).height(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Ток утечки, мА", fontSize = 20.sp, textAlign = TextAlign.Center
                            )
                        }
                        Box(modifier = Modifier.weight(0.3f)) {
                            OutlinedTextField(textStyle = TextStyle.Default.copy(
                                fontSize = 20.sp, textAlign = TextAlign.Center
                            ), value = vm.measuredI.value, onValueChange = {})
                        }
                    }//I утечки
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                        Box(modifier = Modifier.weight(0.3f)) {
                            OutlinedTextField(textStyle = TextStyle.Default.copy(
                                fontSize = 20.sp, textAlign = TextAlign.Center
                            ), value = vm.specifiedUMeger.value, onValueChange = {})
                        }
                        Box(
                            modifier = Modifier.weight(0.4f).height(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Напряжение МГР, В",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Box(modifier = Modifier.weight(0.3f)) {
                            OutlinedTextField(textStyle = TextStyle.Default.copy(
                                fontSize = 20.sp, textAlign = TextAlign.Center
                            ), value = vm.measuredUMeger.value, onValueChange = {})
                        }
                    }  //U МГР
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                        Box(modifier = Modifier.weight(0.3f)) {
                            OutlinedTextField(textStyle = TextStyle.Default.copy(
                                fontSize = 20.sp, textAlign = TextAlign.Center
                            ), value = vm.specifiedRMeger.value, onValueChange = {})
                        }
                        Box(
                            modifier = Modifier.weight(0.4f).height(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "R изоляции, МОм",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Box(modifier = Modifier.weight(0.3f)) {
                            OutlinedTextField(textStyle = TextStyle.Default.copy(
                                fontSize = 20.sp, textAlign = TextAlign.Center
                            ), value = vm.listRs[vm.indexMeger.value].value, onValueChange = {})
                        }
                    }  //R МГР
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                        Box(modifier = Modifier.weight(0.3f)) {
                            OutlinedTextField(textStyle = TextStyle.Default.copy(
                                fontSize = 20.sp, textAlign = TextAlign.Center
                            ), value = vm.specifiedTime.value, onValueChange = {})
                        }
                        Box(
                            modifier = Modifier.weight(0.4f).height(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Время, сек", fontSize = 20.sp, textAlign = TextAlign.Center
                            )
                        }
                        Box(modifier = Modifier.weight(0.3f)) {
                            OutlinedTextField(textStyle = TextStyle.Default.copy(
                                fontSize = 20.sp, textAlign = TextAlign.Center
                            ), value = vm.measuredTime.value, onValueChange = {})
                        }
                    }//время
                }
            }
            Card(modifier = Modifier.fillMaxHeight(), elevation = 4.dp) {
                AnimatedVisibility(vm.logState.value) {
                    LogText(
                        modifier = Modifier.fillMaxSize(),
                        vm.logMessages,
                        vm.logScrollState
                    )
                }
                AnimatedVisibility(!vm.logState.value && vm.selectedObject.value != null) {
                    val targetState = vm.selectedObject.value!!.image.bytes
                    print(targetState) //TODO без этого не работает
                    Image(
                        modifier = Modifier.fillMaxWidth().clickable {
                            vm.imageVisibleState.value = true
                        }.height(512.dp),
                        contentDescription = "image",
                        bitmap =
                        Image.makeFromEncoded(vm.selectedObject.value!!.image.bytes)
                            .toComposeImageBitmap()
                    )
                }
            }
        }
    }

    @Composable
    private fun RightPanel(modifier: Modifier, vm: MainScreenViewModel, testViewModel: TestViewModel) {
        Column(
            modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(elevation = 32.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextH5("№", modifier = Modifier.width(72.dp))
                    Column(Modifier.padding(start = 20.dp)) {
                        Text("Заводской", fontSize = 20.sp, modifier = Modifier.width(136.dp))
                        Text("номер", fontSize = 20.sp, modifier = Modifier.width(136.dp))
                    }
                    Column(Modifier.padding(start = 10.dp)) {
                        Text("Дата", fontSize = 20.sp, modifier = Modifier.width(146.dp))
                        Text("изготовления", fontSize = 20.sp, modifier = Modifier.width(146.dp))
                    }
                    Column(
                        modifier = Modifier.width(96.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "МГР", fontSize = 20.sp)
                        TriStateCheckbox(
                            modifier = Modifier.scale(2f).height(48.dp).width(96.dp),
                            colors = CheckboxDefaults.colors(MaterialTheme.colors.primary),
                            state = vm.allCheckBoxesMeger.value,
                            enabled = vm.mutableStateIsRunning.value && (vm.specifiedI.value.toIntOrNull()
                                ?: 0) < 100,
                            onClick = {
                                vm.onClickTriStateCheckbox(
                                    vm.allCheckBoxesMeger,
                                    vm.listCheckBoxesMeger
                                )
                            },
                        )
                    }
                    TextH5("R, МОм", modifier = Modifier.padding(8.dp).width(140.dp))
                    Column(
                        modifier = Modifier.width(96.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "ВИУ", fontSize = 20.sp)
                        TriStateCheckbox(
                            modifier = Modifier.scale(2f).height(48.dp).width(96.dp),
                            colors = CheckboxDefaults.colors(MaterialTheme.colors.primary),
                            state = vm.allCheckBoxesViu.value,
                            enabled = vm.mutableStateIsRunning.value && (vm.specifiedI.value.toIntOrNull()
                                ?: 0) < 100,
                            onClick = {
                                vm.onClickTriStateCheckbox(
                                    vm.allCheckBoxesViu,
                                    vm.listCheckBoxesViu
                                )
                            },
                        )
                    }
                    TextH5(text = "I, мА", modifier = Modifier.padding(8.dp).width(140.dp))
                    TextH5("Статус", modifier = Modifier.padding(8.dp).fillMaxWidth())
                }
            }
            Column(modifier = Modifier.weight(0.99f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(10) { number ->
                    if ((vm.specifiedI.value.toIntOrNull() ?: 0) < 100 || (((vm.specifiedI.value.toIntOrNull()
                            ?: 0) > 100) && number == 0)
                    ) {
                        Card(elevation = 4.dp) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TextH5("${number + 1}", modifier = Modifier.width(72.dp))

                                OutlinedTextField(modifier = Modifier.padding(8.dp).width(140.dp),
                                    singleLine = true,
                                    textStyle = TextStyle.Default.copy(
                                        fontSize = 20.sp, textAlign = TextAlign.Center
                                    ),
                                    value = vm.listSerialNumbers[number].value,
                                    onValueChange = { vm.listSerialNumbers[number].value = it })

                                OutlinedTextField(modifier = Modifier.padding(8.dp).width(140.dp),
                                    singleLine = true,
                                    textStyle = TextStyle.Default.copy(
                                        fontSize = 20.sp, textAlign = TextAlign.Center
                                    ),
                                    value = vm.listDateProduct[number].value,
                                    onValueChange = { vm.listDateProduct[number].value = it })
                                Box(
                                    modifier = Modifier.width(96.dp), contentAlignment = Alignment.Center
                                ) {
                                    Checkbox(
                                        checked = vm.listCheckBoxesMeger[number].value,
                                        enabled = vm.mutableStateIsRunning.value,
                                        colors = CheckboxDefaults.colors(MaterialTheme.colors.primary),
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
                                        modifier = Modifier.scale(2f).height(48.dp).width(96.dp)
                                    )
                                }
                                OutlinedTextField(modifier = Modifier.padding(8.dp).width(140.dp)
                                    .background(vm.listColorsRsTF[number].value),
                                    textStyle = TextStyle.Default.copy(
                                        fontSize = 20.sp, textAlign = TextAlign.Center, color = Color.Black
                                    ),
                                    value = vm.listRs[number].value,
                                    onValueChange = {})
                                Box(
                                    modifier = Modifier.width(96.dp), contentAlignment = Alignment.Center
                                ) {
                                    Checkbox(
                                        checked = vm.listCheckBoxesViu[number].value,
                                        enabled = vm.mutableStateIsRunning.value,
                                        colors = CheckboxDefaults.colors(MaterialTheme.colors.primary),
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
                                        modifier = Modifier.scale(2f).height(48.dp).width(96.dp)
                                    )
                                }
                                OutlinedTextField(modifier = Modifier.padding(8.dp).width(140.dp)
                                    .background(vm.listColorsCurrentTF[number].value),
                                    textStyle = TextStyle.Default.copy(
                                        fontSize = 20.sp, textAlign = TextAlign.Center, color = Color.Black
                                    ),
                                    value = vm.listCurrents[number].value,
                                    onValueChange = {})
                                Circle(
                                    color = vm.listColorsProtection[number].value,
                                    modifier = modifier.width(140.dp)
                                )
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(64.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1 / 3f).height(64.dp), onClick = {
                        vm.logState.value = !vm.logState.value
                    }, elevation = ButtonDefaults.elevation(
                        defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                    ), enabled = !isTestRunning
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = if (vm.logState.value) "Показать чертеж" else "Показать лог",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Button(
                    modifier = Modifier.weight(1 / 3f).height(64.dp), onClick = {
                        testViewModel.start()
                    }, elevation = ButtonDefaults.elevation(
                        defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                    ), enabled = !vm.isTestRunningState.value
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = "Старт", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                Button(
                    modifier = Modifier.weight(1 / 3f).height(64.dp),
                    onClick = {
                        testViewModel.stop()
                    },
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                    ), enabled = vm.isTestRunningState.value
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = "Стоп", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Icon(
                            imageVector = Icons.Filled.Stop,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
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
    fun Circle(color: Color, modifier: Modifier = Modifier) {
        Canvas(modifier = modifier.size(48.dp), onDraw = {
            drawCircle(color = color)
            drawCircle(color = Color.Black, style = Stroke(width = 2.dp.toPx()))
        })
    }
}
