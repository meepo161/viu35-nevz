package ru.avem.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.avem.viu35.screens.ObjectEditorScreen
import ru.avem.viu35.screens.PasswordScreen
import kotlin.system.exitProcess

@Composable
fun HomeScreenDrawer() {
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
                    "Версия 0.0.1", fontSize = 12.sp, style = TextStyle(
                        color = Color.Gray
                    )
                )
            }
        }
    }
    Divider()
    DrawerMenuItem(Icons.Filled.Settings, "База данных испытываемых аппаратов") {
//        navigator.push(PasswordScreen(ObjectEditorScreen))
        navigator.push(ObjectEditorScreen)
    }
    DrawerMenuItem(painterResource("icons/baseline_storage_24.xml"), "База данных протоколов") {
//        navigator.push(ProtocolsScreen)
    }
    DrawerMenuItem(Icons.Filled.SettingsRemote, "Состояние защит") {
//        navigator.push(SettingsScreen)
    }
    Divider()
    DrawerMenuItem(Icons.Filled.ExitToApp, "Выход") {
        exitProcess(0)
    }
}