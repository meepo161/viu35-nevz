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
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class ObjectEditorViewModel(var mainViewModel: MainScreenViewModel) : ScreenModel {
    private val scope = CoroutineScope(Dispatchers.Default)
//    val objects = mutableStateListOf(*getAllTestItems().sortedBy { it.name }.toTypedArray())
//    val selectedObject = mutableStateOf<TestItem?>(null)
//    val objectFields = mutableStateListOf<TestItemField>()
//    val selectedField = mutableStateOf<TestItemField?>(null)

    var showFilePicker = mutableStateOf(false)

    val nameState = mutableStateOf("")
    val typeState = mutableStateOf("")
    val imagePathState = mutableStateOf("")

    val nameStateError = mutableStateOf(false)
    val typeStateError = mutableStateOf(false)
    val imagePathStateError = mutableStateOf(false)

    val imageVisibleState = mutableStateOf(false)
    val createNewObjectVisibleState = mutableStateOf(false)
    val image = "${File("").absolutePath}\\images\\image.jpg"

    fun onTestObjectSelected(testItem: TestItem) {
        scope.launch {
            mainViewModel.selectedObject.value = testItem
            mainViewModel.objectFields.clear()
            transaction {
                mainViewModel.objectFields.addAll(testItem.fieldsIterable)
            }
        }
    }

    fun onObjectFieldDelete() {
        scope.launch {
            transaction {
                TestItemFields.deleteWhere { TestItemFields.id eq mainViewModel.selectedField.value!!.id }
            }
            mainViewModel.objectFields.remove(mainViewModel.selectedField.value)
            mainViewModel.selectedField.value = mainViewModel.objectFields.firstOrNull()
        }
    }

    fun copyObject(copyTestItem: TestItem) {
        if (mainViewModel.selectedObject.value != null) {
            scope.launch {
                transaction {
                    TestItem.new {
                        name = copyTestItem.name
                        type = copyTestItem.type
                        image = copyTestItem.image
                    }.also { ti ->
                        copyTestItem.fieldsIterable.forEach {
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

    fun deleteObject() {
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
        nameStateError.value = nameState.value.isEmpty()
        typeStateError.value = typeState.value.isEmpty()
        imagePathStateError.value = imagePathState.value.isEmpty()
        if (!nameStateError.value && !typeStateError.value && !imagePathStateError.value) {
            transaction {
                TestItem.new {
                    name = nameState.value
                    type = typeState.value
                    image = ExposedBlob(Files.readAllBytes(Paths.get(imagePathState.value)))
                }
                mainViewModel.objects.clear()
                mainViewModel.objects.addAll(getAllTestItems().sortedBy { it.name }.toTypedArray())
            }
            closeNewObjectWindow()
        }
    }

    fun closeNewObjectWindow() {
        createNewObjectVisibleState.value = false
        nameState.value = ""
        typeState.value = ""
        imagePathState.value = ""
    }
}