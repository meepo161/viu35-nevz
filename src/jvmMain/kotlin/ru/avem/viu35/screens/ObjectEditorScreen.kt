package ru.avem.viu35.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import org.bouncycastle.math.raw.Mod
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.skia.Image
import ru.avem.viu35.composables.*
import ru.avem.viu35.database.entities.TestItemField
import ru.avem.viu35.viewmodels.MainScreenViewModel
import ru.avem.viu35.viewmodels.ObjectEditorViewModel
import java.nio.file.Files
import java.nio.file.Paths

class ObjectEditorScreen(private var mainViewModel: MainScreenViewModel) : Screen {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val isExpandedDropDownMenu = mutableStateOf(false)
        val vm = rememberScreenModel { ObjectEditorViewModel(mainViewModel) }

        val fileType = "jpg"
        MaterialTheme {
            Scaffold(topBar = {
                TopAppBar(title = { Text("База данных испытываемых аппаратов") }, navigationIcon = {
                    IconButton(onClick = {
                        navigator.popUntilRoot()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                })
            }) {
                AnimatedVisibility(mainViewModel.objects.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                    FilePicker(vm.showFilePicker.value, fileExtension = fileType) { path ->
                        vm.showFilePicker.value = false
                        if (path != null) {
                            vm.imageObjectState.value = path
                        }
                    }
                    if (vm.createNewObjectVisibleState.value) {
                        CustomDialog(title = "Создание нового аппарата",
                            text = "Введите данные аппарата",
                            yesButton = "Создать",
                            noButton = "Отмена",
                            yesCallback = {
                                vm.createNewObject()
                            },
                            noCallback = {
                                vm.closeNewObjectDialog()
                            }) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                                    Box(
                                        modifier = Modifier.weight(0.3f).height(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Имя", fontSize = 20.sp, textAlign = TextAlign.Center
                                        )
                                    }
                                    Box(modifier = Modifier.weight(0.7f)) {
                                        OutlinedTextField(textStyle = TextStyle.Default.copy(
                                            fontSize = 20.sp, textAlign = TextAlign.Center
                                        ),
                                            isError = vm.nameObjectStateError.value,
                                            value = vm.nameObjectState.value,
                                            onValueChange = { vm.nameObjectState.value = it })
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                                    Box(
                                        modifier = Modifier.weight(0.3f).height(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Тип", fontSize = 20.sp, textAlign = TextAlign.Center
                                        )
                                    }
                                    Box(modifier = Modifier.weight(0.7f)) {
                                        OutlinedTextField(textStyle = TextStyle.Default.copy(
                                            fontSize = 20.sp, textAlign = TextAlign.Center
                                        ),
                                            isError = vm.typeObjectStateError.value,
                                            value = vm.typeObjectState.value,
                                            onValueChange = { vm.typeObjectState.value = it })
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                                    Button(
                                        onClick = {
                                            vm.showFilePicker.value = true
                                        },
                                        modifier = Modifier.fillMaxWidth().background(
                                            if (vm.imageObjectStateError.value) {
                                                Color.Red
                                            } else {
                                                MaterialTheme.colors.primary
                                            }
                                        ),
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
                                            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
                                            Text(text = "Выбрать файл чертежа")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (vm.editObjectVisibleState.value) {
                        CustomDialog(title = "Редактирование аппарата",
                            text = "Введите данные аппарата",
                            yesButton = "Сохранить",
                            noButton = "Отмена",
                            yesCallback = {
                                vm.editObject()
                            },
                            noCallback = {
                                vm.closeEditObjectDialog()
                            }) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                                    Box(
                                        modifier = Modifier.weight(0.3f).height(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Имя", fontSize = 20.sp, textAlign = TextAlign.Center
                                        )
                                    }
                                    Box(modifier = Modifier.weight(0.7f)) {
                                        OutlinedTextField(textStyle = TextStyle.Default.copy(
                                            fontSize = 20.sp, textAlign = TextAlign.Center
                                        ),
                                            isError = vm.nameObjectStateError.value,
                                            value = vm.nameObjectState.value,
                                            onValueChange = { vm.nameObjectState.value = it })
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                                    Box(
                                        modifier = Modifier.weight(0.3f).height(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Тип", fontSize = 20.sp, textAlign = TextAlign.Center
                                        )
                                    }
                                    Box(modifier = Modifier.weight(0.7f)) {
                                        OutlinedTextField(textStyle = TextStyle.Default.copy(
                                            fontSize = 20.sp, textAlign = TextAlign.Center
                                        ),
                                            isError = vm.typeObjectStateError.value,
                                            value = vm.typeObjectState.value,
                                            onValueChange = { vm.typeObjectState.value = it })
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                                    Button(
                                        onClick = {
                                            vm.showFilePicker.value = true
                                        },
                                        modifier = Modifier.fillMaxWidth().background(
                                            if (vm.imageObjectStateError.value) {
                                                Color.Red
                                            } else {
                                                MaterialTheme.colors.primary
                                            }
                                        ),
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
                                            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
                                            Text(text = "Выбрать файл чертежа")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (vm.editFieldVisibleState.value) {
                        CustomDialog(modifier = Modifier.width(1024.dp),
                            title = "Редактирование проверки",
                            text = "Введите данные проверки",
                            yesButton = "Сохранить",
                            noButton = "Отмена",
                            yesCallback = {
                                vm.editField()
                            },
                            noCallback = {
                                vm.closeEditFieldDialog()
                            }) {
                            DialogField(vm)
                        }
                    }
                    if (vm.createNewFieldVisibleState.value) {
                        CustomDialog(modifier = Modifier.width(1024.dp),
                            title = "Создание новой проверки",
                            text = "Введите данные проверки",
                            yesButton = "Создать",
                            noButton = "Отмена",
                            yesCallback = {
                                vm.createNewField()
                            },
                            noCallback = {
                                vm.closeNewFieldDialog()
                            }) {
                            DialogField(vm)
                        }
                    }
                    if (vm.imageVisibleState.value) {
                        println(mainViewModel.selectedObject.value!!.image.bytes) //TODO без этого не работает
                        Image(
                            modifier = Modifier.fillMaxSize()
                                .onClick(matcher = PointerMatcher.mouse(PointerButton.Primary),
                                    keyboardModifiers = { true },
                                    onClick = {
                                        vm.imageVisibleState.value = false
                                    }),
                            contentDescription = "image",
                            bitmap = if (mainViewModel.selectedObject.value != null) {
                                Image.Companion.makeFromEncoded(mainViewModel.selectedObject.value!!.image.bytes)
                                    .toComposeImageBitmap()
                            } else {
                                ImageBitmap(800, 800)
                            }
                        )
                    } else {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(0.3f).padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ScrollableLazyColumn(
                                    modifier = Modifier.padding(4.dp).weight(0.6f),
                                ) {
                                    items(mainViewModel.objects.size) {
                                        TestObjectListItem(modifier = Modifier.background(
                                            if (mainViewModel.selectedObject.value?.id == mainViewModel.objects[it].id) {
                                                MaterialTheme.colors.primary
                                            } else {
                                                MaterialTheme.colors.background
                                            }
                                        ),
                                            text = "${mainViewModel.objects[it].name} ${mainViewModel.objects[it].type}",
                                            onClick = {
                                                vm.onObjectSelected(mainViewModel.objects[it])
                                            })
                                    }
                                }
                                Button(
                                    onClick = {
                                        if (mainViewModel.selectedObject.value != null) {
                                            vm.imageVisibleState.value = true
                                        }
                                    },
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
                                    onClick = { vm.createNewObjectVisibleState.value = true },
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
                                    onClick = { vm.copyTestObject() },
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
                                    onClick = {
                                        vm.nameObjectState.value = mainViewModel.selectedObject.value!!.name
                                        vm.typeObjectState.value = mainViewModel.selectedObject.value!!.type
                                        vm.editObjectVisibleState.value = true
                                    },
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
                                    onClick = { vm.deleteTestObject() },
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
                                        selectedItem = mainViewModel.selectedField.value,
                                        items = mainViewModel.objectFields,
                                        columns = listOf(
                                            TestItemField::key,
                                            TestItemField::nameTest,
                                            TestItemField::uViu,
                                            TestItemField::time,
                                            TestItemField::uMeger,
                                            TestItemField::current,
                                        ),
                                        columnNames = listOf(
                                            "№",
                                            "Наименование",
                                            "U ВИУ, В",
                                            "Время, с",
                                            "U мегер, В",
                                            "Ток утечки, мА"
                                        ),
                                        onItemPrimaryPressed = {
                                            mainViewModel.selectedField.value = mainViewModel.objectFields[it]
                                        },
                                        onItemSecondaryPressed = {
                                            mainViewModel.selectedField.value = mainViewModel.objectFields[it]
                                        },
                                        contextMenuContent = {
                                            DropdownMenuItem(onClick = {
                                                isExpandedDropDownMenu.value = false
                                                vm.nameTestFieldState.value =
                                                    mainViewModel.selectedField.value!!.nameTest
                                                vm.uViuFieldState.value =
                                                    mainViewModel.selectedField.value!!.uViu.toString()
                                                vm.timeFieldState.value =
                                                    mainViewModel.selectedField.value!!.time.toString()
                                                vm.uMegerFieldState.value =
                                                    mainViewModel.selectedField.value!!.uMeger.toString()
                                                vm.currentFieldState.value =
                                                    mainViewModel.selectedField.value!!.current.toString()
                                                vm.editFieldVisibleState.value = true
                                            }) {
                                                Text("Редактировать")
                                            }
                                            DropdownMenuItem(onClick = {
                                                vm.deleteField()
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
                                        onClick = {
                                            vm.createNewFieldVisibleState.value = true
                                        },
                                        modifier = Modifier.weight(0.25f).height(72.dp),
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
                                            Text(text = "Добавить")
                                        }
                                    }
                                    Button(
                                        onClick = {
                                            vm.nameTestFieldState.value =
                                                mainViewModel.selectedField.value!!.nameTest
                                            vm.uViuFieldState.value =
                                                mainViewModel.selectedField.value!!.uViu.toString()
                                            vm.timeFieldState.value =
                                                mainViewModel.selectedField.value!!.time.toString()
                                            vm.uMegerFieldState.value =
                                                mainViewModel.selectedField.value!!.uMeger.toString()
                                            vm.currentFieldState.value =
                                                mainViewModel.selectedField.value!!.current.toString()
                                            vm.editFieldVisibleState.value = true
                                        },
                                        modifier = Modifier.weight(0.25f).height(72.dp),
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
                                        onClick = { vm.copyField() },
                                        modifier = Modifier.weight(0.25f).height(72.dp),
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
                                        onClick = {
                                            vm.deleteField()
                                        },
                                        modifier = Modifier.weight(0.25f).height(72.dp),
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
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun DialogField(vm: ObjectEditorViewModel) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                Box(
                    modifier = Modifier.weight(0.3f).height(48.dp), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Описание", fontSize = 20.sp, textAlign = TextAlign.Center
                    )
                }
                Box(modifier = Modifier.weight(0.7f)) {
                    OutlinedTextField(textStyle = TextStyle.Default.copy(
                        fontSize = 20.sp, textAlign = TextAlign.Center
                    ),
                        isError = vm.nameTestFieldErrorState.value,
                        value = vm.nameTestFieldState.value,
                        onValueChange = { vm.nameTestFieldState.value = it })
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                Box(
                    modifier = Modifier.weight(0.3f).height(48.dp), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "U ВИУ, В", fontSize = 20.sp, textAlign = TextAlign.Center
                    )
                }
                Box(modifier = Modifier.weight(0.7f)) {
                    OutlinedTextField(textStyle = TextStyle.Default.copy(
                        fontSize = 20.sp, textAlign = TextAlign.Center
                    ),
                        isError = vm.uViuFieldErrorState.value,
                        value = vm.uViuFieldState.value,
                        onValueChange = { vm.uViuFieldState.value = it })
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                Box(
                    modifier = Modifier.weight(0.3f).height(48.dp), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ток утечки, мА", fontSize = 20.sp, textAlign = TextAlign.Center
                    )
                }
                Box(modifier = Modifier.weight(0.7f)) {
                    OutlinedTextField(textStyle = TextStyle.Default.copy(
                        fontSize = 20.sp, textAlign = TextAlign.Center
                    ),
                        isError = vm.currentFieldErrorState.value,
                        value = vm.currentFieldState.value,
                        onValueChange = { vm.currentFieldState.value = it })
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                Box(
                    modifier = Modifier.weight(0.3f).height(48.dp), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Время, сек", fontSize = 20.sp, textAlign = TextAlign.Center
                    )
                }
                Box(modifier = Modifier.weight(0.7f)) {
                    OutlinedTextField(textStyle = TextStyle.Default.copy(
                        fontSize = 20.sp, textAlign = TextAlign.Center
                    ),
                        isError = vm.timeFieldErrorState.value,
                        value = vm.timeFieldState.value,
                        onValueChange = { vm.timeFieldState.value = it })
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                Box(
                    modifier = Modifier.weight(0.3f).height(48.dp), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "U Мегер, В", fontSize = 20.sp, textAlign = TextAlign.Center
                    )
                }
                Box(modifier = Modifier.weight(0.7f)) {
                    OutlinedTextField(textStyle = TextStyle.Default.copy(
                        fontSize = 20.sp, textAlign = TextAlign.Center
                    ),
                        isError = vm.uMegerFieldErrorState.value,
                        value = vm.uMegerFieldState.value,
                        onValueChange = { vm.uMegerFieldState.value = it })
                }
            }
        }

    }
}