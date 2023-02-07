package ru.avem.viu35.viewmodels

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainScreenViewModel : ScreenModel {
    var selectedMeasurement = mutableStateOf<Boolean>(false)
    val scope = CoroutineScope(Dispatchers.Default)
}