package ru.avem.viu35.viewmodels

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.viu35.database.DBManager
import ru.avem.viu35.database.entities.User
import ru.avem.viu35.database.entities.Users

class UserEditorViewModel : ScreenModel {
    val dialogVisibleState = mutableStateOf(false)
    val titleDialog = mutableStateOf("")
    val textDialog = mutableStateOf("")
    var selectedUser = mutableStateOf<User?>(null)
    val allUsers = mutableStateOf(DBManager.getAllUsers())

    fun deleteUser() {
        if (selectedUser.value == null) {
            titleDialog.value = "Ошибка"
            textDialog.value = "Не выбран пользователь"
            dialogVisibleState.value = true
        } else if (selectedUser.value!!.name == "admin") {
            titleDialog.value = "Ошибка"
            textDialog.value = "Невозможно удалить администратора"
            dialogVisibleState.value = true
        } else {
            transaction {
                Users.deleteWhere {
                    name eq selectedUser.value!!.name
                }
            }
            allUsers.value = DBManager.getAllUsers()
            selectedUser = mutableStateOf<User?>(null)
        }
    }
}