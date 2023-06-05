package ru.avem.viu35.viewmodels

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.viu35.database.DBManager.getAllTestItems
import ru.avem.viu35.database.entities.TestItem
import ru.avem.viu35.database.entities.TestItemField
import ru.avem.viu35.database.entities.TestItemFields
import ru.avem.viu35.database.entities.TestItems
import java.nio.file.Files
import java.nio.file.Paths

class ObjectEditorViewModel(private var mainViewModel: MainScreenViewModel) : ScreenModel {
    private val scope = CoroutineScope(Dispatchers.Default)

    var showFilePicker = mutableStateOf(false)

    val createNewObjectVisibleState = mutableStateOf(false)
    val editObjectVisibleState = mutableStateOf(false)

    val nameObjectState = mutableStateOf("")
    val typeObjectState = mutableStateOf("")
    val imageObjectState = mutableStateOf("")

    val nameObjectStateError = mutableStateOf(false)
    val typeObjectStateError = mutableStateOf(false)
    val imageObjectStateError = mutableStateOf(false)

    val createNewFieldVisibleState = mutableStateOf(false)
    val editFieldVisibleState = mutableStateOf(false)

    val nameTestFieldState = mutableStateOf("")
    val uViuFieldState = mutableStateOf("")
    val timeFieldState = mutableStateOf("")
    val uMegerFieldState = mutableStateOf("")
    val rMegerFieldState = mutableStateOf("")
    var expandedUMeger = mutableStateOf(false)
    val currentFieldState = mutableStateOf("")

    val nameTestFieldErrorState = mutableStateOf(false)
    val uViuFieldErrorState = mutableStateOf(false)
    val timeFieldErrorState = mutableStateOf(false)
    val uMegerFieldErrorState = mutableStateOf(false)
    val rMegerFieldErrorState = mutableStateOf(false)
    val currentFieldErrorState = mutableStateOf(false)

    val imageVisibleState = mutableStateOf(false)

    fun onObjectSelected(testItem: TestItem) {
        scope.launch {
            mainViewModel.selectedObject.value = testItem
            mainViewModel.selectedField.value = null
            mainViewModel.objectFields.clear()
            transaction {
                mainViewModel.objectFields.addAll(testItem.fieldsIterable.sortedBy { it.key }.toTypedArray())
            }
        }
    }

    fun editTestObject() {
        if (mainViewModel.selectedObject.value != null) {
            nameObjectState.value = mainViewModel.selectedObject.value!!.name
            typeObjectState.value = mainViewModel.selectedObject.value!!.type
            editObjectVisibleState.value = true
        }
    }

    fun copyTestObject() {
        if (mainViewModel.selectedObject.value != null) {
            scope.launch {
                transaction {
                    TestItem.new {
                        name = mainViewModel.selectedObject.value!!.name
                        type = mainViewModel.selectedObject.value!!.type
                        image = mainViewModel.selectedObject.value!!.image
                    }.also { ti ->
                        mainViewModel.selectedObject.value!!.fieldsIterable.forEach {
                            TestItemField.new {
                                testItem = ti
                                key = it.key
                                nameTest = it.nameTest
                                uViu = it.uViu
                                time = it.time
                                uMeger = it.uMeger
                                rMeger = it.rMeger
                                current = it.current
                            }
                        }
                    }
                }
                mainViewModel.objects.clear()
                mainViewModel.objects.addAll(getAllTestItems().sortedBy { it.name }.toTypedArray())
            }
        }
    }

    fun deleteTestObject() {
        if (mainViewModel.selectedObject.value != null) {
            scope.launch {
                transaction {
                    TestItems.deleteWhere { TestItems.id eq mainViewModel.selectedObject.value!!.id }
                }
                mainViewModel.objects.remove(mainViewModel.selectedObject.value)
                mainViewModel.objects.firstOrNull()?.let { onObjectSelected(it) }
//                mainViewModel.selectedObject.value = mainViewModel.objects.firstOrNull()
            }
        }
    }

    fun createNewObject() {
        nameObjectStateError.value = nameObjectState.value.isEmpty()
        typeObjectStateError.value = typeObjectState.value.isEmpty()
        imageObjectStateError.value = imageObjectState.value.isEmpty()
        if (!nameObjectStateError.value && !typeObjectStateError.value && !imageObjectStateError.value) {
            scope.launch {
                transaction {
                    TestItem.new {
                        name = nameObjectState.value
                        type = typeObjectState.value
                        image = ExposedBlob(Files.readAllBytes(Paths.get(imageObjectState.value)))
                    }
                    mainViewModel.objects.clear()
                    mainViewModel.objects.addAll(getAllTestItems().sortedBy { it.name }.toTypedArray())
                }
                closeNewObjectDialog()
            }
        }
    }

    fun editObject() {
        nameObjectStateError.value = nameObjectState.value.isEmpty()
        typeObjectStateError.value = typeObjectState.value.isEmpty()
        imageObjectStateError.value = imageObjectState.value.isEmpty()
        if (!nameObjectStateError.value && !typeObjectStateError.value && !imageObjectStateError.value) {
            scope.launch {
                transaction {
                    mainViewModel.selectedObject.value!!.name = nameObjectState.value
                    mainViewModel.selectedObject.value!!.type = typeObjectState.value
                    mainViewModel.selectedObject.value!!.image =
                        ExposedBlob(Files.readAllBytes(Paths.get(imageObjectState.value)))
                    mainViewModel.objects.clear()
                    mainViewModel.objects.addAll(getAllTestItems().sortedBy { it.name }.toTypedArray())
                }
                closeEditObjectDialog()
            }
        }
    }

    fun closeNewObjectDialog() {
        createNewObjectVisibleState.value = false
        clearObjectStates()
    }

    fun closeEditObjectDialog() {
        editObjectVisibleState.value = false
        clearObjectStates()
    }

    fun createNewField() {
        nameTestFieldErrorState.value = nameTestFieldState.value.isEmpty()
        uViuFieldErrorState.value =
            uViuFieldState.value.isEmpty()
                    || uViuFieldState.value.toIntOrNull() == null
                    || uViuFieldState.value.toIntOrNull()!! > 15000
                    || uViuFieldState.value.toIntOrNull()!! < 500
        timeFieldErrorState.value =
            timeFieldState.value.isEmpty()
                    || timeFieldState.value.toIntOrNull() == null
                    || timeFieldState.value.toIntOrNull()!! > 600
                    || timeFieldState.value.toIntOrNull()!! < 1
        uMegerFieldErrorState.value = uMegerFieldState.value.isEmpty()
                || uMegerFieldState.value.toIntOrNull() == null
                || !(uMegerFieldState.value.toIntOrNull()!! == 2500
                || uMegerFieldState.value.toIntOrNull()!! == 1000
                || uMegerFieldState.value.toIntOrNull()!! == 500)
        rMegerFieldErrorState.value = rMegerFieldState.value.isEmpty()
                || rMegerFieldState.value.replace(",", ".").toDoubleOrNull() == null
                || rMegerFieldState.value.replace(",", ".").toDoubleOrNull()!! > 100000
                || rMegerFieldState.value.replace(",", ".").toDoubleOrNull()!! < 0.1
        currentFieldErrorState.value =
            currentFieldState.value.isEmpty()
                    || currentFieldState.value.toIntOrNull() == null
                    || currentFieldState.value.toIntOrNull()!! > 1000
                    || currentFieldState.value.toIntOrNull()!! < 1

        if (!nameTestFieldErrorState.value
            && !uViuFieldErrorState.value
            && !timeFieldErrorState.value
            && !uMegerFieldErrorState.value
            && !rMegerFieldErrorState.value
            && !currentFieldErrorState.value
        ) {
            scope.launch {
                transaction {
                    var lastKey = 0
                    mainViewModel.selectedObject.value!!.fieldsIterable.forEach {
                        if (lastKey < it.key) {
                            lastKey = it.key
                        }
                    }
                    val newField = TestItemField.new {
                        testItem = mainViewModel.selectedObject.value!!
                        key = lastKey + 1
                        nameTest = nameTestFieldState.value
                        uViu = uViuFieldState.value.toInt()
                        time = timeFieldState.value.toInt()
                        uMeger = uMegerFieldState.value.toInt()
                        rMeger = rMegerFieldState.value.replace(",", ".").toDouble().toString()
                        current = currentFieldState.value.toInt()
                    }
                    rMegerFieldState.value = rMegerFieldState.value.replace(",", ".")
                    mainViewModel.objectFields.add(newField)
                    mainViewModel.selectedField.value = mainViewModel.objectFields.lastOrNull()
                    if (mainViewModel.selectedField.value != null) {
                        mainViewModel.onTestObjectFieldSelected(mainViewModel.objectFields.last())
                    }
                }
                closeNewFieldDialog()
            }
        }
    }

    fun closeEditFieldDialog() {
        editFieldVisibleState.value = false
        clearStates()
    }

    fun closeNewFieldDialog() {
        createNewFieldVisibleState.value = false
        clearStates()
    }

    private fun clearStates() {
        nameTestFieldState.value = ""
        uViuFieldState.value = ""
        timeFieldState.value = ""
        uMegerFieldState.value = ""
        rMegerFieldState.value = ""
        currentFieldState.value = ""
    }

    private fun clearObjectStates() {
        nameObjectState.value = ""
        typeObjectState.value = ""
        imageObjectState.value = ""
    }

    fun copyField() {
        scope.launch {
            transaction {
                val newField = TestItemField.new {
                    testItem = mainViewModel.selectedObject.value!!
                    key = mainViewModel.selectedObject.value!!.fieldsIterable.last().key + 1
                    nameTest = mainViewModel.selectedField.value!!.nameTest
                    uViu = mainViewModel.selectedField.value!!.uViu
                    time = mainViewModel.selectedField.value!!.time
                    uMeger = mainViewModel.selectedField.value!!.uMeger
                    rMeger = mainViewModel.selectedField.value!!.rMeger
                    current = mainViewModel.selectedField.value!!.current
                }
                mainViewModel.objectFields.add(newField)
                mainViewModel.selectedField.value = mainViewModel.objectFields.lastOrNull()
            }
        }
    }


    fun deleteField() {
        scope.launch {
            transaction {
                TestItemFields.deleteWhere { TestItemFields.id eq mainViewModel.selectedField.value!!.id }
            }
            mainViewModel.objectFields.remove(mainViewModel.selectedField.value)
            mainViewModel.selectedField.value = mainViewModel.objectFields.firstOrNull()
        }
    }

    fun editField() {
        if (mainViewModel.selectedField.value != null) {
            nameTestFieldErrorState.value = nameTestFieldState.value.isEmpty()
            uViuFieldErrorState.value =
                uViuFieldState.value.isEmpty()
                        || uViuFieldState.value.toIntOrNull() == null
                        || uViuFieldState.value.toIntOrNull()!! > 15000
                        || uViuFieldState.value.toIntOrNull()!! < 500
            timeFieldErrorState.value =
                timeFieldState.value.isEmpty()
                        || timeFieldState.value.toIntOrNull() == null
                        || timeFieldState.value.toIntOrNull()!! > 600
                        || timeFieldState.value.toIntOrNull()!! < 1
            uMegerFieldErrorState.value = uMegerFieldState.value.isEmpty()
                    || uMegerFieldState.value.toIntOrNull() == null
                    || !(uMegerFieldState.value.toIntOrNull()!! == 2500
                    || uMegerFieldState.value.toIntOrNull()!! == 1000
                    || uMegerFieldState.value.toIntOrNull()!! == 500)
            rMegerFieldErrorState.value = rMegerFieldState.value.isEmpty()
                    || rMegerFieldState.value.replace(",", ".").toDoubleOrNull() == null
                    || rMegerFieldState.value.replace(",", ".").toDoubleOrNull()!! > 100000
                    || rMegerFieldState.value.replace(",", ".").toDoubleOrNull()!! < 0.1
            currentFieldErrorState.value =
                currentFieldState.value.isEmpty()
                        || currentFieldState.value.toIntOrNull() == null
                        || currentFieldState.value.toIntOrNull()!! > 1000
                        || currentFieldState.value.toIntOrNull()!! < 1

            if (!nameTestFieldErrorState.value
                && !uViuFieldErrorState.value
                && !timeFieldErrorState.value
                && !uMegerFieldErrorState.value
                && !rMegerFieldErrorState.value
                && !currentFieldErrorState.value
            ) {
                scope.launch {
                    transaction {
                        mainViewModel.selectedField.value?.nameTest = nameTestFieldState.value
                        mainViewModel.selectedField.value?.uViu = uViuFieldState.value.toInt()
                        mainViewModel.selectedField.value?.time = timeFieldState.value.toInt()
                        mainViewModel.selectedField.value?.uMeger = uMegerFieldState.value.toInt()
                        mainViewModel.selectedField.value?.rMeger =
                            rMegerFieldState.value.replace(",", ".").toDouble().toString()
                        mainViewModel.selectedField.value?.current = currentFieldState.value.toInt()
                        mainViewModel.objectFields.clear()
                        mainViewModel.objectFields.addAll(mainViewModel.selectedObject.value!!.fieldsIterable.sortedBy { it.key }
                            .toTypedArray())
                        mainViewModel.selectedField.value = mainViewModel.objectFields.lastOrNull()
                        if (mainViewModel.selectedField.value != null) {
                            mainViewModel.onTestObjectFieldSelected(mainViewModel.objectFields.last())
                        }
                    }
                    closeEditFieldDialog()
                }
            }
        }
    }

    fun editFieldWindow() {
        if (mainViewModel.selectedField.value != null) {
            nameTestFieldState.value =
                mainViewModel.selectedField.value!!.nameTest
            uViuFieldState.value =
                mainViewModel.selectedField.value!!.uViu.toString()
            timeFieldState.value =
                mainViewModel.selectedField.value!!.time.toString()
            uMegerFieldState.value =
                mainViewModel.selectedField.value!!.uMeger.toString()
            rMegerFieldState.value =
                mainViewModel.selectedField.value!!.rMeger.toString()
            currentFieldState.value =
                mainViewModel.selectedField.value!!.current.toString()
            editFieldVisibleState.value = true
        }
    }
}