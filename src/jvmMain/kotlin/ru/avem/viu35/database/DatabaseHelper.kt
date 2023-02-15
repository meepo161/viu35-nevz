package ru.avem.viu35.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.viu35.database.entities.TestItem
import ru.avem.viu35.database.entities.TestItems
import java.sql.Connection

fun validateDB() {
    Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    transaction {
        SchemaUtils.create(TestItems)
    }
}

fun getAllTestItems() = transaction { TestItem.all().toList() }
