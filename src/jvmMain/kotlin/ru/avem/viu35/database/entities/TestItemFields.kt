package ru.avem.viu35.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object TestItemFields : IntIdTable() {
    val testItem = reference("testItem", TestItems)
    val key = integer("key").autoIncrement()
    val dot1 = varchar("dot1", 256)
    val dot2 = varchar("dot2", 256)
    val description = varchar("description", 256)
}

class TestItemField(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TestItemField>(TestItemFields)

    var testItem by TestItem referencedOn TestItemFields.testItem

    var key by TestItemFields.key
    var dot1 by TestItemFields.dot1
    var dot2 by TestItemFields.dot2
    var description by TestItemFields.description

    override fun toString() = "${testItem.name}"
}
data class TestItemFieldScheme(
    var key: String,
    var dot1: String,
    var dot2: String,
    var description: String
)