package ru.avem.viu35.database

import androidx.compose.ui.res.useResource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.viu35.database.entities.*
import java.sql.Connection
import java.text.SimpleDateFormat

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
                    serial = "serial"
                    operator = "operator"
                    itemName = "itemName"
                    pointsName = "pointsName"
                    uViu = "uViu"
                    iViu = "iViu"
                    uMgr = "uMgr"
                    rMgr = "rMgr"
                    date = "date"
                    result = "result"
                    time = "time"
                }
                var protocol2 = Protocol.new {
                    serial = "serial"
                    operator = "operator2"
                    itemName = "itemName2"
                    pointsName = "pointsName2"
                    uViu = "uViu2"
                    iViu = "iViu2"
                    uMgr = "uMgr2"
                    rMgr = "rMgr2"
                    date = "date2"
                    result = "result"
                    time = "time"
                }
                Protocol.new {
                    serial = "serial11111111111"
                    operator = "Тестовый оператор"
                    itemName = "бЛОК12у"
                    pointsName = "ауккук"
                    uViu = "1"
                    iViu = "2"
                    uMgr = "3"
                    rMgr = "4"
                    date = SimpleDateFormat("dd.MM.y").format(System.currentTimeMillis()).toString()
                    time = SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()).toString()
                    result = "Неизвестно"
                }
            }
            if (TestItem.all().empty()) {
                TestItem.new {
                    name = "Резистор токоограничивающий РТ-45"
                    type = "6ТС.273.045"
                    useResource("6ТС.273.045.png") {
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
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между стойкой поз.3 и поверхностью Д"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Блок реэисторов высоковольтной цепи БРВЦ-4б"
                    type = "6ТС.277.046"
                    useResource("6ТС.277.046.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между выводами 1; 9 и стойкой поз. 6"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между дойкой поз. 6 и поверхностью К"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между выводами 1 и 9"
                        uViu = 4500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Пускотормозной резистор ПТР-97"
                    type = "6ТС.273.097"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов и скобами поз. 2,10"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобами поз. 2,10 и поверхностями Г"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Пускотормозной резистор ПТР-98"
                    type = "6ТС.273.098"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов и скобами поз. 9"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобами поз. 9 н поверхностями Г"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Пускотормозной резистор ПТР-99"
                    type = "6ТС.273.099"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов и скобами поз. 8"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобами поз. 8 и поверхностями Г"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Пускотормозной резистор ПТР-100"
                    type = "6ТС.273.100"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов и скобами поз. 13"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобами поз. 13 и поверхностями Г"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Пускотормозной резистор ПТР-101"
                    type = "6ТС.273.101"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов и скобами поз. 14"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобами поз. 14 и поверхностями Г"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Пускотормозной резистор ПТР-102"
                    type = "6ТС.273.102"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов и скобами поз. 10"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобами поз. 10 и поверхностями Г"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Пускотормозной резистор ПТР-103"
                    type = "6ТС.273.103"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов и скобами поз. 12"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобами поз. 12 и поверхностями Г"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Пускотормозной резистор ПТР-104"
                    type = "6ТС.273.104"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов и скобами поз. 13"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобами поз. 13 и поверхностями Г"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Пускотормозной резистор ПТР-105"
                    type = "6ТС.273.105"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов и скобами поз. 13"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобами поз. 13 и поверхностями Г"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Пускотормозной резистор ПТР-106"
                    type = "6ТС.273.106"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов и скобами поз. 12"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобами поз. 12 и поверхностями Г"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Пускотормозной резистор ПТР-107"
                    type = "6ТС.273.107"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между выводами 1, 2, 3 и скобами поз. 12"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобами поз. 12 и поверхностями Г"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Пускотормозной резистор ПТР-108"
                    type = "6ТС.273.108"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между выводами 1, 2 и скобами поз. 10"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобами поз. 10 и поверхностями Г"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Пускотормозной резистор ПТР-109"
                    type = "6ТС.273.109"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов и скобами поз. 4, 16"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобами поз. 4,16 и поверхностями Г"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между выводом 3 и скобой поз. 14"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобой поз. 14 и поверхностью Ж"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Резистор пуско-тормозной РПТ-2"
                    type = "СТНР.434346.002"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов 1,2 и скобой поз.12"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобой поз. 12 и поверхностью Г"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Резистор пуско-тормозной РПТ-3"
                    type = "СТНР.434346.003"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов 1,2,3,4 и скобой поз.10"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобой поз. 10 и поверхностью Г"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Резистор пуско-тормозной РПТ-4"
                    type = "СТНР.434346.004"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов 1,2,3,4,5 и скобой поз.12"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобой поз. 12 и поверхностью Ж"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Резистор пуско-тормозной РПТ-5"
                    type = "СТНР.434346.005"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов 1,2,3,5 и скобой поз.12"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов 4,6 и скобой поз.12"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобой поз. 12 и поверхностью Ж"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Резистор пуско-тормозной РПТ-1"
                    type = "СТНР.434356.001"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между любым из выводов 1(или 2) и скобой поз.12"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобой поз. 12 и поверхностью Г"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Резистор пуско-тормозной РПТ-6"
                    type = "СТНР.434356.002"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между выводом 1 и скобой поз.15"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между выводом 3 и скобой поз.15"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобой поз. 15 и поверхностью Ж"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
                TestItem.new {
                    name = "Блок сопротивлений БС-003"
                    type = "СТНР.434356.003"
                    useResource("$type.png") {
                        image =
                            ExposedBlob(it.readAllBytes())
                    }
                }.also { ti ->
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между выводами 1 или 2,3 и стойкой поз.1"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 1
                        nameTest = "Между выводами 4 или 5 и стойкой поз.1"
                        uViu = 7000
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                    TestItemField.new {
                        testItem = ti
                        key = 2
                        nameTest = "Между скобой поз. 15 и поверхностью Ж"
                        uViu = 9500
                        time = 60
                        uMeger = 2500
                        rMeger = 150.0.toString()
                        current = 50
                    }
                }
            }
        }
    }

    fun getAllTestItems() = transaction { TestItem.all().toList().reversed() }
    fun getAllProtocols() = transaction { Protocol.all().toList().reversed() }
    fun getAllUsers() = transaction { User.all().toList().reversed() }
}

