package ru.avem.viu35.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import isDarkTheme
import operatorLogin
import ru.avem.composables.DrawerMenuItem
import ru.avem.viu35.database.DBManager
import ru.avem.viu35.file
import ru.avem.viu35.screens.ObjectEditorScreen
import ru.avem.viu35.screens.ProtocolsScreen
import ru.avem.viu35.screens.UserEditorScreen
import ru.avem.viu35.viewmodels.MainScreenViewModel

@Composable
fun HomeScreenDrawer(mainViewModel: MainScreenViewModel, isClickable: MutableState<Boolean> = mutableStateOf(true)) {
    val navigator = LocalNavigator.currentOrThrow

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource("icons/ic_avem_logo.xml"),
                modifier = Modifier.width(48.dp).height(48.dp).padding(end = 4.dp),
                tint = Color.Blue,
                contentDescription = null
            )
            Column {
                Text("ВИУ-35", fontSize = 16.sp)
                Text(
                    "Версия 1.0.2", fontSize = 12.sp, style = TextStyle(
                        color = Color.Gray
                    )
                )
            }
        }
        Divider()
        DrawerMenuItem(Icons.Filled.Settings, "База данных испытываемых аппаратов") {
            if (operatorLogin == "admin") {
                if (isClickable.value) {
                    navigator.push(ObjectEditorScreen(mainViewModel))
                }
            } else {
                mainViewModel.showDialog("Внимание", "Редактирование доступно только для администратора")
            }
        }
        DrawerMenuItem(painterResource("icons/baseline_storage_24.xml"), "База данных протоколов") {
            mainViewModel.allProtocols.value = DBManager.getAllProtocols()
            navigator.push(ProtocolsScreen(mainViewModel))
        }
        DrawerMenuItem(Icons.Filled.PersonAdd, "База данных пользователей") {
            if (operatorLogin == "admin") {
                if (isClickable.value) {
                    navigator.push(UserEditorScreen())
                }
            } else {
                mainViewModel.showDialog("Внимание", "Редактирование доступно только для администратора")
            }
        }
        Divider()
        DrawerMenuItem(
            if (isDarkTheme.value) {
                Icons.Filled.DarkMode
            } else {
                Icons.Filled.LightMode
            }, "Сменить тему"
        ) {
            if (isClickable.value) {
                isDarkTheme.value = !isDarkTheme.value
                file.writeText(
                    if (isDarkTheme.value) {
                        "1"
                    } else {
                        "0"
                    }
                )
            }
        }
        Divider()
        DrawerMenuItem(Icons.Filled.People, "Сменить пользователя") {
            if (isClickable.value) {
                navigator.popUntilRoot()
            }
        }
        DrawerMenuItem(Icons.Filled.ExitToApp, "Выход") {
            if (isClickable.value) {
                mainViewModel.exitDialogVisibleState.value = true
            }
        }
    }
}