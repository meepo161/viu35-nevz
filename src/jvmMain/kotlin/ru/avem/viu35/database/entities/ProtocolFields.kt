package ru.avem.viu35.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object ProtocolFields : IntIdTable() {
    val protocol = reference("protocol", Protocols)
    val key = varchar("key", 128)
    val value = varchar("value", 512)
}

class ProtocolField(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ProtocolField>(ProtocolFields)

    var protocol by Protocol referencedOn ProtocolFields.protocol
    var key by ProtocolFields.key
    var value by ProtocolFields.value
}
