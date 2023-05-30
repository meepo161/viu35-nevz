package ru.avem.viu35.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.navigator.Navigator
import ru.avem.viu35.viewmodels.MainScreenViewModel

@Composable
fun MainScreenActionBar(navigator: Navigator, viewModel: MainScreenViewModel, onDeleteCallback: () -> Unit) {
//    IconButton(onClick = {
//        navigator.push(SavedMeasurementsScreen)
//    }) {
//        Icon(
//            painterResource("/icons/clipboard_list.xml"),
//            contentDescription = null,
//            tint = Color.White
//        )
//    }
//    IconButton(onClick = {
//        navigator.push(AddMacroScreen)
//    }) {
//        Icon(
//            painterResource("/icons/ic_add_white_32dp.xml"),
//            contentDescription = null,
//            tint = Color.White
//        )
//    }
//    AnimatedVisibility(viewModel.selectedMeasurement.value) {
//        IconButton(onClick = {
//            onDeleteCallback()
//        }) {
//            Icon(
//                painterResource("/icons/ic_baseline_delete_white_32dp.xml"),
//                contentDescription = null,
//                tint = Color.White
//            )
//        }
//    }
//    IconButton(onClick = {
//        navigator.push(SettingsScreen)
//    }) {
//        Icon(
//            painterResource("/icons/ic_wrench_white_32dp.xml"),
//            contentDescription = null,
//            tint = Color.White
//        )
//    }
}