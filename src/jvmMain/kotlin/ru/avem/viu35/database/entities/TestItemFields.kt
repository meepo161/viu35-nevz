package ru.avem.viu35.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object TestItemFields : IntIdTable() {
    val testItem = reference("testItem", TestItems)
    val key = integer("key")
    val nameTest = varchar("nameTest", 2048)
    val uViu = integer("uViu")
    val time = integer("time")
    val uMeger = integer("uMeger")
    val rMeger = integer("rMeger")
}

class TestItemField(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TestItemField>(TestItemFields)

    var testItem by TestItem referencedOn TestItemFields.testItem

    var key by TestItemFields.key
    var nameTest by TestItemFields.nameTest
    var uViu by TestItemFields.uViu
    var time by TestItemFields.time
    var uMeger by TestItemFields.uMeger
    var rMeger by TestItemFields.rMeger

    override fun toString() = "${nameTest}"
}