package ru.avem.viu35.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.viu35.database.entities.TestItem
import ru.avem.viu35.database.entities.TestItemField
import ru.avem.viu35.database.entities.TestItemFields
import ru.avem.viu35.database.getAllTestItems

class ObjectEditorViewModel : ScreenModel {
    private val scope = CoroutineScope(Dispatchers.Default)
    val objects = mutableStateListOf(*getAllTestItems().sortedBy { it.name}.toTypedArray())
    val selectedObject = mutableStateOf<TestItem?>(null)
    val objectFields = mutableStateListOf<TestItemField>()
    val selectedField = mutableStateOf<TestItemField?>(null)

    fun onTestObjectSelected(testItem: TestItem) {
        scope.launch {
            selectedObject.value = testItem
            objectFields.clear()
            transaction {
                objectFields.addAll(testItem.fieldsIterable)
            }
        }
    }

    fun onObjectFieldDelete() {
        scope.launch {
            transaction {
                TestItemFields.deleteWhere { TestItemFields.id eq selectedField.value!!.id }
            }
            objectFields.remove(selectedField.value)
            selectedField.value = objectFields.firstOrNull()
        }
    }
}