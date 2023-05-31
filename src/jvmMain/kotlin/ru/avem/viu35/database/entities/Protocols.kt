package ru.avem.viu35.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

object Protocols : IntIdTable() {
    val serial              = varchar("serial", 128)
    val operator            = varchar("operator", 128)
    val itemName            = varchar("itemName", 128)
    val pointsName          = varchar("pointsName", 128)
    val uViu                = varchar("uViu", 128)
    val iViu                = varchar("iViu", 128)
    val uMgr                = varchar("uMgr", 128)
    val rMgr                = varchar("rMgr", 128)
    val date                = varchar("date", 128)
    val time                = varchar("time", 128)
    val result              = varchar("result", 128)
}

class Protocol(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Protocol>(Protocols)

    var serial by Protocols.serial
    var operator by Protocols.operator
    var itemName by Protocols.itemName
    var pointsName by Protocols.pointsName
    var uViu by Protocols.uViu
    var iViu by Protocols.iViu
    var uMgr by Protocols.uMgr
    var rMgr by Protocols.rMgr
    var date by Protocols.date
    var time by Protocols.time
    var result by Protocols.result

//
//    val fields by ProtocolField referrersOn (ProtocolFields.protocol)
//
//    val filledFields
//        get() = transaction {
//            fields.toList()
//        }
//
//    var stringId: String = ""
//        get() = id.toString()

//    override fun toString() = "$id. $serial:$testItemType|$testItemField - $date $time Результат: $result"
    override fun toString() = "$itemName-$serial-$date $time".replace(":", "-")

    fun toMills(): Long {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.ENGLISH)
        val localDate = LocalDateTime.parse("$date $time", formatter)
        return localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
}
