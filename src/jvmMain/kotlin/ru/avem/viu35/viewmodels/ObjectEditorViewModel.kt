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
import ru.avem.viu35.database.entities.TestItem
import ru.avem.viu35.database.entities.TestItemField
import ru.avem.viu35.database.entities.TestItemFields
import ru.avem.viu35.database.entities.TestItems
import ru.avem.viu35.database.getAllTestItems
import java.nio.file.Files
import java.nio.file.Paths

class ObjectEditorViewModel(private var mainViewModel: MainScreenViewModel) : ScreenModel {
    private val scope = CoroutineScope(Dispatchers.Default)

    var showFilePicker = mutableStateOf(false)

    val createNewObjectVisibleState = mutableStateOf(false)

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
    val currentFieldState = mutableStateOf("")

    val nameTestFieldErrorState = mutableStateOf(false)
    val uViuFieldErrorState = mutableStateOf(false)
    val timeFieldErrorState = mutableStateOf(false)
    val uMegerFieldErrorState = mutableStateOf(false)
    val currentFieldErrorState = mutableStateOf(false)

    val imageVisibleState = mutableStateOf(false)

    fun onObjectSelected(testItem: TestItem) {
        scope.launch {
            mainViewModel.selectedObject.value = testItem
            mainViewModel.objectFields.clear()
            transaction {
                mainViewModel.objectFields.addAll(testItem.fieldsIterable.sortedBy { it.key }.toTypedArray())
            }
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
                mainViewModel.selectedObject.value = mainViewModel.objects.firstOrNull()
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

    fun closeNewObjectDialog() {
        createNewObjectVisibleState.value = false
        nameObjectState.value = ""
        typeObjectState.value = ""
        imageObjectState.value = ""
    }

    fun createNewField() {
        nameTestFieldErrorState.value = nameTestFieldState.value.isEmpty()
        uViuFieldErrorState.value = uViuFieldState.value.isEmpty() || uViuFieldState.value.toIntOrNull() == null
        timeFieldErrorState.value = timeFieldState.value.isEmpty() || timeFieldState.value.toIntOrNull() == null
        uMegerFieldErrorState.value = uMegerFieldState.value.isEmpty() || uMegerFieldState.value.toIntOrNull() == null
        currentFieldErrorState.value =
            currentFieldState.value.isEmpty() || currentFieldState.value.toIntOrNull() == null

        if (!nameTestFieldErrorState.value
            && !uViuFieldErrorState.value
            && !timeFieldErrorState.value
            && !uMegerFieldErrorState.value
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
                        current = currentFieldState.value.toInt()
                    }
                    mainViewModel.objectFields.add(newField)
                    mainViewModel.selectedField.value = mainViewModel.objectFields.lastOrNull()
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
        currentFieldState.value = ""
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
        nameTestFieldErrorState.value = nameTestFieldState.value.isEmpty()
        uViuFieldErrorState.value = uViuFieldState.value.isEmpty() || uViuFieldState.value.toIntOrNull() == null
        timeFieldErrorState.value = timeFieldState.value.isEmpty() || timeFieldState.value.toIntOrNull() == null
        uMegerFieldErrorState.value = uMegerFieldState.value.isEmpty() || uMegerFieldState.value.toIntOrNull() == null
        currentFieldErrorState.value =
            currentFieldState.value.isEmpty() || currentFieldState.value.toIntOrNull() == null

        if (!nameTestFieldErrorState.value
            && !uViuFieldErrorState.value
            && !timeFieldErrorState.value
            && !uMegerFieldErrorState.value
            && !currentFieldErrorState.value
        ) {
            scope.launch {
                transaction {
                    val newField = TestItemField.new {
                        testItem = mainViewModel.selectedObject.value!!
                        key = mainViewModel.selectedField.value!!.key
                        nameTest = nameTestFieldState.value
                        uViu = uViuFieldState.value.toInt()
                        time = timeFieldState.value.toInt()
                        uMeger = uMegerFieldState.value.toInt()
                        current = currentFieldState.value.toInt()
                    }
                    TestItemFields.deleteWhere { TestItemFields.id eq mainViewModel.selectedField.value!!.id }
                    mainViewModel.objectFields.clear()
                    mainViewModel.objectFields.addAll(mainViewModel.selectedObject.value!!.fieldsIterable.sortedBy { it.key }
                        .toTypedArray())
                    mainViewModel.selectedField.value = mainViewModel.objectFields.lastOrNull()
                }
                closeEditFieldDialog()
            }
        }
    }
}