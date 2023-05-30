package ru.avem.viu35.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object TestItemFields : IntIdTable() {
    var testItem = reference("testItem", TestItems)
    var key = integer("key")
    var nameTest = varchar("nameTest", 2048)
    var uViu = integer("uViu")
    var time = integer("time")
    var uMeger = integer("uMeger")
    var rMeger = varchar("rMeger", 128)
    var current = integer("current")
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
    var current by TestItemFields.current

    override fun toString() = "${nameTest}"
}