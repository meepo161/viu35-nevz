package ru.avem.viu35.viewmodels

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import ru.avem.viu35.database.entities.TestItemFieldScheme
import ru.avem.viu35.database.entities.TestItemScheme

class ObjectEditorViewModel : ScreenModel {
    var tvm = mutableStateOf(TestItemScheme("", "", listOf()))
    val currentTVM = mutableStateOf(TestItemFieldScheme(0, "", "", ""))
}