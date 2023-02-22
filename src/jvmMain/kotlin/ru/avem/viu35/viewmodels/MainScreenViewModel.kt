package ru.avem.viu35.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.viu35.database.entities.TestItem
import ru.avem.viu35.database.entities.TestItemField
import ru.avem.viu35.database.getAllTestItems

class MainScreenViewModel : ScreenModel {
    var selectedMeasurement = mutableStateOf<Boolean>(false)
    val scope = CoroutineScope(Dispatchers.Default)

    val list = listOf(
        "Резистор токоограничивающий РТ-45 6TC.277.045", "Блок резисторов высоковольтной цепи БРВЦ-46 6TC.277.046"
    )
    val listState = mutableStateOf(list[0])

    val imageVisibleState = mutableStateOf(false)

    val listSerialNumbers = List(10) { mutableStateOf("") }
    val listCurrents = List(10) { mutableStateOf("") }
    val listRs = List(10) { mutableStateOf("") }
    val listProtections = List(10) { mutableStateOf("") }
    val listCheckBoxesViu = List(10) { mutableStateOf(false) }
    val listCheckBoxesMeger = List(10) { mutableStateOf(false) }
    val allCheckBoxesViu = mutableStateOf(ToggleableState.Off)
    val allCheckBoxesMeger = mutableStateOf(ToggleableState.Off)

    val specifiedUViu = mutableStateOf("")
    val specifiedUMeger = mutableStateOf("")
    val specifiedI = mutableStateOf("")

    val measuredUViu = mutableStateOf("")
    val measuredTime = mutableStateOf("")

    val dot1 = mutableStateOf("")
    val dot2 = mutableStateOf("")

    val listColors = List(10) { mutableStateOf(Color.Cyan) }

    val objects = mutableStateListOf(*getAllTestItems().sortedBy { it.name }.toTypedArray())
    val selectedObject = mutableStateOf<TestItem?>(null)
    val objectFields = mutableStateListOf<TestItemField>()
    val selectedField = mutableStateOf<TestItemField?>(null)

    fun onTestObjectSelected(testItem: TestItem) {
        scope.launch {
            selectedObject.value = testItem
            objectFields.clear()
            transaction {
                objectFields.addAll(testItem.fieldsIterable)
                selectedField.value = objectFields.first()
            }
        }
    }

    fun onTestObjectFieldSelected(testItemField: TestItemField) {
        scope.launch {
            transaction {
                selectedField.value = testItemField
                specifiedUViu.value = selectedField.value!!.uViu.toString()
                specifiedUMeger.value = selectedField.value!!.uMeger.toString()
                specifiedI.value = selectedField.value!!.time.toString()
            }
        }
    }

    fun onClickTriStateCheckbox(checkBox: MutableState<ToggleableState>, list: List<MutableState<Boolean>>) {
        if (checkBox.value == ToggleableState.Off) {
            checkBox.value = ToggleableState.On
        } else {
            checkBox.value = ToggleableState.Off
        }
        list.forEach {
            it.value = checkBox.value == ToggleableState.On
        }
    }
}