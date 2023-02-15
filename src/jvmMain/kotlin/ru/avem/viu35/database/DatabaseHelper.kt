package ru.avem.viu35.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.viu35.database.entities.*
import java.sql.Connection

fun validateDB() {
    Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    transaction {
        SchemaUtils.create(TestItems, TestItemFields)

        if (TestItem.all().empty()) {
            for (i in 1 until 99) {
                var k = 0
                var list = mutableListOf<TestItemFieldScheme>()
                for (j in 0 until i) {
                    list.add(TestItemFieldScheme(k++, "dot${k}", "dot${k}", "description$i"))
                }
                TestItem.new {
                    name = "$i"
                    type = "аппарат"
                }.also { ti ->
                    list.forEach {
                        TestItemField.new {
                            testItem = ti
                            key = it.key
                            dot1 = it.dot1
                            dot2 = it.dot2
                            description = it.description
                        }
                    }
                }
            }
        }
    }
}

fun getAllTestItems() = transaction { TestItem.all().toList() }
