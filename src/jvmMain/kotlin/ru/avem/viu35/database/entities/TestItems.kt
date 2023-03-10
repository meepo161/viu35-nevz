package ru.avem.viu35.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction

object TestItems : IntIdTable() {
    val name = varchar("name", 256)
    val type = varchar("type", 256)
    val image = blob("image")
}

class TestItem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TestItem>(TestItems)

    var name by TestItems.name
    var type by TestItems.type
    var image by TestItems.image

    val fieldsIterable by TestItemField referrersOn (TestItemFields.testItem)

    val fields: Map<Int, TestItemField>
        get() {
            return transaction { fieldsIterable.map { it.key to it }.toMap() }
        }

    override fun toString() = "$name $type"
}