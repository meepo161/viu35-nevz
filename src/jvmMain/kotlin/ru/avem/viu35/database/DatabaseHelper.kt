package ru.avem.viu35.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.viu35.database.entities.TestItem
import ru.avem.viu35.database.entities.TestItemField
import ru.avem.viu35.database.entities.TestItemFields
import ru.avem.viu35.database.entities.TestItems
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection

fun validateDB() {
    Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    transaction {
        SchemaUtils.create(TestItems, TestItemFields)

        if (TestItem.all().empty()) {
            TestItem.new {
                name = "Резистор токоограничивающий РТ-45"
                type = "6TC.277.045"
                image =
                    ExposedBlob(Files.readAllBytes(Paths.get("C:/Users/Maga/IdeaProjects/viu35-nevz/src/jvmMain/resources/unnamed.jpg")))
            }.also { ti ->
                TestItemField.new {
                    testItem = ti
                    key = 1
                    nameTest = "Между любым из выводов и стойкой поз.3"
                    uViu = 7000
                    time = 60
                    uMeger = 2500
                    current = 150
                }
                TestItemField.new {
                    testItem = ti
                    key = 2
                    nameTest = "Между стойкой поз.3 и поверхностью Д"
                    uViu = 9500
                    time = 60
                    uMeger = 2500
                    current = 150
                }
            }

            TestItem.new {
                name = "Блок резисторов высоковольтной цепи БРВЦ-46"
                type = "6TC.277.046"
                image =
                    ExposedBlob(Files.readAllBytes(Paths.get("C:/Users/Maga/IdeaProjects/viu35-nevz/src/jvmMain/resources/unnamed2.jpg")))
            }.also { ti ->
                TestItemField.new {
                    testItem = ti
                    key = 1
                    nameTest = "Между выводами 1;9 и стойкой поз.6"
                    uViu = 7000
                    time = 60
                    uMeger = 2500
                    current = 150
                }
                TestItemField.new {
                    testItem = ti
                    key = 2
                    nameTest = "Между стойкой поз.6 и поверхностью К"
                    uViu = 9500
                    time = 60
                    uMeger = 2500
                    current = 150
                }
                TestItemField.new {
                    testItem = ti
                    key = 3
                    nameTest = "Между выводами 1 и 9"
                    uViu = 4500
                    time = 60
                    uMeger = 2500
                    current = 150
                }
            }
        }
    }
}

fun getAllTestItems() = transaction { TestItem.all().toList() }
