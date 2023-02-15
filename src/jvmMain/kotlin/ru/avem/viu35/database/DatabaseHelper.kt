package ru.avem.viu35.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
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

        if (TestItem.all().count() == 0L) {
            for (i in 100 downTo 0) {
                TestItem.new {
                    name = "${i}"
                    type = "аппарат"
                }
            }
        }
    }
}

fun getAllTestItems() = transaction { TestItem.all().toList() }
