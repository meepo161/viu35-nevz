package ru.avem.viu35.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

object Protocols : IntIdTable() {
    val template            = varchar("template", 128)
    val serial              = varchar("serial", 128)
    val testItemType        = varchar("testItemType", 256)
    val testItemField       = varchar("testItemField", 512)
    val point1Name          = varchar("point1Name", 128)
    val point2Name          = varchar("point2Name", 128)
    val date                = varchar("date", 128)
    val time                = varchar("time", 128)
    val result              = varchar("result", 128)
}

class Protocol(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Protocol>(Protocols)

    var template by Protocols.template
    var serial by Protocols.serial
    var testItemType by Protocols.testItemType
    var testItemField by Protocols.testItemField
    var point1Name by Protocols.point1Name
    var point2Name by Protocols.point2Name

    var date by Protocols.date
    var time by Protocols.time

    var result by Protocols.result


    val fields by ProtocolField referrersOn (ProtocolFields.protocol)

    val filledFields
        get() = transaction {
            fields.toList()
        }

    var stringId: String = ""
        get() = id.toString()

    override fun toString() = "$id. $serial:$testItemType|$testItemField - $date $time Результат: $result"

    fun toMills(): Long {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.ENGLISH)
        val localDate = LocalDateTime.parse("$date $time", formatter)
        return localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
}
