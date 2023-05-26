package ru.avem.viu35.viewmodels

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
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
import java.io.File

class MainScreenViewModel(val logScrollState: LazyListState) : ScreenModel {
    var selectedMeasurement = mutableStateOf(false)
    val scope = CoroutineScope(Dispatchers.Default)

    val imageVisibleState = mutableStateOf(false)
    val dialogVisibleState = mutableStateOf(false)
    val image = "${File("").absolutePath}\\images\\image.jpg"

    val listSerialNumbers = List(10) { mutableStateOf("") }
    val listCurrents = List(10) { mutableStateOf("") }
    val listColorsCurrentTF = List(10) { mutableStateOf(Color.White) }
    val listColorsRsTF = List(10) { mutableStateOf(Color.White) }
    val listRs = List(10) { mutableStateOf("") }
    val listProtections = List(10) { mutableStateOf("") }
    val listCheckBoxesViu = List(10) { mutableStateOf(false) }
    val mutableStateIsRunning = mutableStateOf(true)
    val listCheckBoxesMeger = List(10) { mutableStateOf(false) }
    val allCheckBoxesViu = mutableStateOf(ToggleableState.Off)
    val allCheckBoxesMeger = mutableStateOf(ToggleableState.Off)

    val specifiedUViu = mutableStateOf("")
    val specifiedUMeger = mutableStateOf("")
    val specifiedI = mutableStateOf("")
    val specifiedTime = mutableStateOf("")

    val measuredUViu = mutableStateOf("")
    val measuredTime = mutableStateOf("")
    val measuredI = mutableStateOf("")
    val measuredU = mutableStateOf("")

    val storedListCurrents = mutableListOf<Double>()
    val storedListRs = mutableListOf<Double>()
    val storedListResults = mutableListOf<Double>()

    val dot1 = mutableStateOf("")
    val dot2 = mutableStateOf("")

    val isTestRunningState = mutableStateOf(false)
    val logState = mutableStateOf(false)
    val logMessages = mutableStateListOf<String>()

    val listColorsProtection = List(10) { mutableStateOf(Color(0xFF0071bb)) }
    val colorZone = mutableStateOf(Color(0xFF0071bb))
    val colorSCO = mutableStateOf(Color(0xFF0071bb))
    val colorCurrent = mutableStateOf(Color(0xFF0071bb))

    val objects = mutableStateListOf(*getAllTestItems().sortedBy { it.name }.toTypedArray())
    val selectedObject = mutableStateOf<TestItem?>(null)
    val objectFields = mutableStateListOf<TestItemField>()
    val selectedField = mutableStateOf<TestItemField?>(null)

    val titleDialog = mutableStateOf("")
    val textDialog = mutableStateOf("")


    fun onTestObjectSelected(testItem: TestItem) {
        scope.launch {
            selectedObject.value = testItem
            objectFields.clear()
            transaction {
                objectFields.addAll(testItem.fieldsIterable)
                selectedField.value = objectFields.firstOrNull()
            }
            selectedField.value?.let { onTestObjectFieldSelected(it) }
        }
//        Files.write(Paths.get(image), testItem.image.bytes)
    }

    fun onTestObjectFieldSelected(testItemField: TestItemField) {
        scope.launch {
            transaction {
                selectedField.value = testItemField
                specifiedUViu.value = selectedField.value!!.uViu.toString()
                specifiedUMeger.value = selectedField.value!!.uMeger.toString()
                specifiedI.value = selectedField.value!!.current.toString()
                specifiedTime.value = selectedField.value!!.time.toString()
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