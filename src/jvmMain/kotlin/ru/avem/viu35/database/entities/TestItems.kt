package ru.avem.viu35.database.entities

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction

object TestItems : IntIdTable() {
    val name = varchar("name", 256)
    val type = integer("type")
}

class TestItem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TestItem>(TestItems)

    var name by TestItems.name
    var type by TestItems.type

    val fieldsIterable by TestItemField referrersOn (TestItemFields.testItem)

    val fields: Map<String, TestItemField>
        get() {
            return transaction { fieldsIterable.map { it.key1 to it }.toMap() }
        }

    override fun toString() = name
}