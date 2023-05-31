package ru.avem.viu35.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import kotlinx.coroutines.launch
import operatorLogin
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.viu35.composables.ConfirmDialog
import ru.avem.viu35.composables.TableView
import ru.avem.viu35.database.DBManager
import ru.avem.viu35.database.entities.Protocol
import ru.avem.viu35.database.entities.Protocols
import ru.avem.viu35.openFile
import ru.avem.viu35.protocol.saveProtocolAsWorkbook
import ru.avem.viu35.viewmodels.MainScreenViewModel
import java.awt.Desktop
import java.io.File

class ProtocolsScreen(private var mainViewModel: MainScreenViewModel) : Screen {
    val dialogVisibleState = mutableStateOf(false)
    val titleDialog = mutableStateOf("")
    val textDialog = mutableStateOf("")
    val filterValue = mutableStateOf("")

    fun getProtocols(filter: String) {
        if (filter.isEmpty()) {
            mainViewModel.allProtocols.value = DBManager.getAllProtocols()
        } else {
            mainViewModel.allProtocols.value = DBManager.getAllProtocols().filter {
                it.serial.lowercase().contains(filter.lowercase()) ||
                        it.pointsName.lowercase().contains(filter.lowercase()) ||
                        it.itemName.lowercase().contains(filter.lowercase()) ||
                        it.operator.lowercase().contains(filter.lowercase()) ||
                        it.date.lowercase().contains(filter.lowercase())
            }
        }
    }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val isExpandedDropDownMenu = mutableStateOf(false)
        val showDirectoryPicker = mutableStateOf(false)


        MaterialTheme {
            Scaffold(topBar = {
                TopAppBar(title = { Text("База данных протоколов") }, navigationIcon = {
                    IconButton(onClick = {
                        navigator.pop()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                })
            }) {
                if (dialogVisibleState.value) {
                    ConfirmDialog(
                        titleDialog.value,
                        textDialog.value,
                        { dialogVisibleState.value = false },
                        { dialogVisibleState.value = false })
                }
                if (mainViewModel.selectedProtocol.value != null) {
                    DirectoryPicker(showDirectoryPicker.value) {
                        if (it != null) {
                            saveProtocolAsWorkbook(
                                listOf(mainViewModel.selectedProtocol.value!!), File(
                                    it, "${mainViewModel.selectedProtocol.value!!}.xlsx"
                                ).absolutePath
                            )
                        }
                        showDirectoryPicker.value = false
                    }
                }
                Column(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                ) {
                    Text(fontSize = 20.sp, text = "Выберите протокол для просмотра:")
                    Row(
                        modifier = Modifier.weight(0.1f).fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(fontSize = 20.sp, text = "Фильтр:")
                        OutlinedTextField(modifier = Modifier.fillMaxWidth(), textStyle = TextStyle.Default.copy(
                            fontSize = 20.sp, textAlign = TextAlign.Center
                        ), value = filterValue.value, onValueChange = {
                            filterValue.value = it
                            getProtocols(filterValue.value)
                        })
                    }
                    Box(modifier = Modifier.weight(0.9f)) {
                        TableView(
                            selectedItem = mainViewModel.selectedProtocol.value,
                            items = mainViewModel.allProtocols.value,
                            columns = listOf(
                                Protocol::itemName,
                                Protocol::operator,
                                Protocol::date,
                                Protocol::time,
                                Protocol::serial,
                                Protocol::result,
                            ),
                            columnNames = listOf(
                                "Тип аппарата",
                                "Оператор",
                                "Дата",
                                "Время",
                                "Серийный номер",
                                "Результат",
                            ),
                            onItemPrimaryPressed = {
                                mainViewModel.selectedProtocol.value = mainViewModel.allProtocols.value[it]
                            },
                            onItemSecondaryPressed = {
                                mainViewModel.selectedProtocol.value = mainViewModel.allProtocols.value[it]
                            },
                            contextMenuContent = {
                                DropdownMenuItem(onClick = {
                                    deleteProtocol()
                                    isExpandedDropDownMenu.value = false
                                }) {
                                    Text("Удалить")
                                }
                            },
                            isExpandedDropdownMenu = isExpandedDropDownMenu
                        )
                    }
                    Row(
                        modifier = Modifier.weight(0.1f).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                    ) {

                        Button(
                            modifier = Modifier.weight(1 / 4f).height(128.dp),
                            onClick = {
                                saveProtocolAsWorkbook(listOf(mainViewModel.selectedProtocol.value!!))
                                openFile(File("cfg/lastOpened.xlsx"))
                            },
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(text = "Открыть", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                Icon(
                                    imageVector = Icons.Filled.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                        Button(
                            modifier = Modifier.weight(1 / 4f).height(128.dp),
                            onClick = {
                                saveProtocolAsWorkbook(listOf(mainViewModel.selectedProtocol.value!!))
                                Desktop.getDesktop().print(File("cfg/lastOpened.xlsx"))
                            },
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(text = "Печать", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                Icon(
                                    imageVector = Icons.Filled.Print,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                        Button(
                            modifier = Modifier.weight(1 / 4f).height(128.dp),
                            onClick = {
                                showDirectoryPicker.value = true
                            },
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(text = "Сохранить как", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                Icon(
                                    imageVector = Icons.Filled.Save,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                        Button(
                            modifier = Modifier.weight(1 / 4f).height(128.dp),
                            onClick = {
                                deleteProtocol()
                            },
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 10.dp, pressedElevation = 15.dp, disabledElevation = 0.dp
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(text = "Удалить", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun deleteProtocol() {
        if (operatorLogin == "admin") {
            if (mainViewModel.selectedProtocol.value != null) {
                mainViewModel.scope.launch {
                    transaction {
                        Protocols.deleteWhere { id eq mainViewModel.selectedProtocol.value!!.id }
                    }
                    mainViewModel.allProtocols.value = DBManager.getAllProtocols()
                    mainViewModel.selectedProtocol.value = null
                }
            } else {
                dialogVisibleState.value = true
                titleDialog.value = "Ошибка"
                textDialog.value = "Не выбран протокол"
            }
        } else {
            dialogVisibleState.value = true
            titleDialog.value = "Ошибка"
            textDialog.value = "Удалять разрешено только администратору"
        }
    }
}
