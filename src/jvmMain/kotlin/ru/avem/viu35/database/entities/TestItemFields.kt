package ru.avem.viu35.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object TestItemFields : IntIdTable() {
    val testItem = reference("testItem", TestItems)
    val key1 = varchar("key1", 256)
    val key2 = varchar("key2", 256)
    val description = varchar("description", 256)
}

class TestItemField(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TestItemField>(TestItemFields)

    var testItem by TestItem referencedOn TestItemFields.testItem

    var key1 by TestItemFields.key1
    var key2 by TestItemFields.key2
    var description by TestItemFields.description

    override fun toString() = "${testItem.name}"
}
data class TestItemFieldScheme(
    var key1: String,
    var key2: String,
    var description: String,
    var title: String = ""
)