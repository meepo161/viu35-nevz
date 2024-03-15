package ru.avem.viu35.viewmodels

import androidx.compose.foundation.lazy.LazyListState
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
import ru.avem.viu35.database.DBManager
import ru.avem.viu35.database.DBManager.getAllTestItems
import ru.avem.viu35.database.entities.Protocol
import ru.avem.viu35.database.entities.TestItem
import ru.avem.viu35.database.entities.TestItemField
import java.io.File

class MainScreenViewModel(val logScrollState: LazyListState) : ScreenModel {
    var selectedMeasurement = mutableStateOf(false)
    val scope = CoroutineScope(Dispatchers.Default)

    val imageVisibleState = mutableStateOf(false)
    val dialogVisibleState = mutableStateOf(false)
    val exitDialogVisibleState = mutableStateOf(false)
    val image = "${File("").absolutePath}\\images\\image.jpg"

    val listSerialNumbers = List(10) { mutableStateOf("") }
    val listDateProduct = List(10) { mutableStateOf("") }
    val listCurrents = List(10) { mutableStateOf("") }
    val listColorsCurrentTF = List(10) { mutableStateOf(Color.Transparent) }
    val listColorsRsTF = List(10) { mutableStateOf(Color.Transparent) }
    val listRs = List(10) { mutableStateOf("") }
    val listProtections = List(10) { mutableStateOf("") }
    var listCheckBoxesViu = List(10) { mutableStateOf(false) }
    var listViu = MutableList(10) { false }
    val mutableStateIsRunning = mutableStateOf(true)
    var listCheckBoxesMeger = List(10) { mutableStateOf(false) }
    val allCheckBoxesViu = mutableStateOf(ToggleableState.Off)
    val allCheckBoxesMeger = mutableStateOf(ToggleableState.Off)

    val specifiedUViu = mutableStateOf("")
    val specifiedUMeger = mutableStateOf("")
    val specifiedRMeger = mutableStateOf("")
    val specifiedI = mutableStateOf("")
    val specifiedTime = mutableStateOf("")

    val measuredUViuAmp = mutableStateOf("")

    val measuredUViu = mutableStateOf("")
    val measuredTime = mutableStateOf("")
    val measuredI = mutableStateOf("")
    val measuredUMeger = mutableStateOf("")
    val indexMeger = mutableStateOf(0)

    var storedUViu = ""
    var storedUViuAmp = ""

    val storedListCurrents = mutableListOf<Double>()
    val storedListRs = mutableListOf<Double>()
    val storedListResults = mutableListOf<Double>()

    val isTestRunningState = mutableStateOf(false)
    val logState = mutableStateOf(false)
    val logMessages = mutableStateListOf<String>()

    val listColorsProtection = List(10) { mutableStateOf(Color(0xFF6200EE)) }

    val objects = mutableStateListOf(*getAllTestItems().sortedBy { it.name }.toTypedArray())
    val selectedObject = mutableStateOf<TestItem?>(null)
    val objectFields = mutableStateListOf<TestItemField>()
    val selectedField = mutableStateOf<TestItemField?>(null)

    var titleDialog = mutableStateOf("")
    var textDialog = mutableStateOf("")
    var nameGif = mutableStateOf("")

    var selectedProtocol = mutableStateOf<Protocol?>(null)
    var allProtocols = mutableStateOf(DBManager.getAllProtocols())


    fun onTestObjectSelected(testItem: TestItem) {
        scope.launch {
            selectedObject.value = testItem
            objectFields.clear()
            transaction {
                objectFields.addAll(testItem.fieldsIterable)
                selectedField.value = objectFields.firstOrNull()
            }
            selectedField.value?.let { onTestObjectFieldSelected(it) }
            clearTestFields()
        }
    }

    fun onTestObjectFieldSelected(testItemField: TestItemField) {
        scope.launch {
            transaction {
                selectedField.value = testItemField
                specifiedUViu.value = selectedField.value!!.uViu.toString()
                specifiedUMeger.value = selectedField.value!!.uMeger.toString()
                specifiedRMeger.value = selectedField.value!!.rMeger.toString()
                specifiedI.value = selectedField.value!!.current.toString()
                specifiedTime.value = selectedField.value!!.time.toString()
            }
            listCheckBoxesMeger.forEach {
                it.value = false
            }
            listCheckBoxesViu.forEach {
                it.value = false
            }
            allCheckBoxesViu.value = ToggleableState.Off
            allCheckBoxesMeger.value = ToggleableState.Off

            clearTestFields()
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

    fun clearTestFields() {
        measuredI.value = ""
        measuredUViu.value = ""
        measuredUMeger.value = ""
        measuredTime.value = ""
        listColorsCurrentTF.forEach {
            it.value = Color.Transparent
        }
        listColorsRsTF.forEach {
            it.value = Color.Transparent
        }
        listCurrents.forEach {
            it.value = ""
        }
        listRs.forEach {
            it.value = ""
        }
        listColorsProtection.forEach {
            it.value = Color(0xFF6200EE)
        }
    }

    fun showDialog(title: String, text: String, gif: String = "") {
        titleDialog.value = title
        textDialog.value = text
        nameGif.value = gif
        dialogVisibleState.value = true
    }

    fun hideDialog() {
        dialogVisibleState.value = false
        titleDialog.value = ""
        textDialog.value = ""
        nameGif.value = ""
    }
}