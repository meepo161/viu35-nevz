package ru.avem.viu35.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.avem.viu35.composables.ComboBox
import ru.avem.viu35.composables.EnabledTextButton
import ru.avem.viu35.database.DBManager

class ProtocolsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var selectedProtocol = mutableStateOf(DBManager.getAllProtocols().first())

        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text("Выберите протокол для просмотра:")
            ComboBox(
                modifier = Modifier.width(1800.dp),
                selectedItem = selectedProtocol,
                items = DBManager.getAllProtocols(),
                selectedValue = {
                    selectedProtocol.value = it
                }
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)) {
                EnabledTextButton("Назад") {
                    navigator.pop()
                }
                EnabledTextButton("Открыть") {
                    
                }
                EnabledTextButton("Сохранить") {

                }
            }
        }
    }
}
