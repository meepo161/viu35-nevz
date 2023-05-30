package ru.avem.viu35.database

import androidx.compose.ui.res.useResource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.viu35.database.entities.*
import java.sql.Connection

object DBManager {
    init {
        Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        validateDB()
    }

    fun validateDB() {
        transaction {
            SchemaUtils.create(TestItems, TestItemFields, Users, Protocols)

            if (User.all().empty()) {
                User.new {
                    name = "admin"
                    password = "avem"
                }
            }
            if (Protocol.all().empty()) {
                var protocol1 = Protocol.new {
                    serial      = "serial"
                    operator    = "operator"
                    itemName    = "itemName"
                    pointsName  = "pointsName"
                    uViu        = "uViu"
                    iViu        = "iViu"
                    uMgr        = "uMgr"
                    rMgr        = "rMgr"
                    date        = "date"
                    result        = "result"
                    time        = "time"
                }
                var protocol2 = Protocol.new {
                    serial      = "serial"
                    operator    = "operator2"
                    itemName    = "itemName2"
                    pointsName  = "pointsName2"
                    uViu        = "uViu2"
                    iViu        = "iViu2"
                    uMgr        = "uMgr2"
                    rMgr        = "rMgr2"
                    date        = "date2"
                    result        = "result"
                    time        = "time"
                }
            }
            if (TestItem.all().empty()) {
                TestItem.new {
                    name = "Резистор токоограничивающий РТ-45"
                    type = "6TC.277.045"
                    useResource("image1.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов и стойкой поз.3"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = "100000"
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между стойкой поз.3 и поверхностью Д"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = "100000"
                        current = 50
                    }
                }

                TestItem.new {
                    name = "Блок резисторов высоковольтной цепи БРВЦ-46"
                    type = "6TC.277.046"
                    useResource("image2.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между выводами 1;9 и стойкой поз.6"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = "100000"
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между стойкой поз.6 и поверхностью К"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = "100000"
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 3
                        nameTest = "Между выводами 1 и 9"
                        uViu = 4500
                        time = 60
                        uMeger = 2500
                        rMeger = "100000"
                        current = 50
                    }
                }
            }
        }
    }

    fun getAllTestItems() = transaction { TestItem.all().toList() }
    fun getAllProtocols() = transaction { Protocol.all().toList() }
    fun getAllUsers() = transaction { User.all().toList() }
}

