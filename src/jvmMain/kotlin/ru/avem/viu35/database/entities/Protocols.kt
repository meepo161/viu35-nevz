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
    val serial                      = varchar("serial", 128)
    val dateProduct                 = varchar("dateProduct", 128)
    val operator                    = varchar("operator", 128)
    val operatorPost                = varchar("operatorPost", 128)
    val itemName                    = varchar("itemName", 128)
    val itemType                    = varchar("itemType", 128)
    val pointsName                  = varchar("pointsName", 128)
    val spec_uViu                   = varchar("spec_uViu", 128)
    val spec_uViuFault              = varchar("spec_uViuFault", 128)
    val spec_uViuAmp                = varchar("spec_uViuAmp", 128)
    val spec_uViuAmpFault           = varchar("spec_uViuAmpFault", 128)
    val spec_iViu                   = varchar("spec_iViu", 128)
    val spec_uMgr                   = varchar("spec_uMgr", 128)
    val spec_rMgr                   = varchar("spec_rMgr", 128)
    val uViuAmp                     = varchar("uViuAmp", 128)
    val uViu                        = varchar("uViu", 128)
    val iViu                        = varchar("iViu", 128)
    val uMgr                        = varchar("uMgr", 128)
    val rMgr                        = varchar("rMgr", 128)
    val date                        = varchar("date", 128)
    val time                        = varchar("time", 128)
    val resultViu                   = varchar("resultViu", 128)
    val resultMgr                   = varchar("resultMgr", 128)
}

class Protocol(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Protocol>(Protocols)

    var serial by Protocols.serial
    var dateProduct by Protocols.dateProduct
    var operator by Protocols.operator
    var operatorPost by Protocols.operatorPost
    var itemName by Protocols.itemName
    var itemType by Protocols.itemType
    var pointsName by Protocols.pointsName
    var spec_uViu by Protocols.spec_uViu
    var spec_uViuAmp by Protocols.spec_uViuAmp
    var spec_uViuFault by Protocols.spec_uViuFault
    var spec_uViuAmpFault by Protocols.spec_uViuAmpFault
    var spec_iViu by Protocols.spec_iViu
    var spec_uMgr by Protocols.spec_uMgr
    var spec_rMgr by Protocols.spec_rMgr
    var uViuAmp by Protocols.uViuAmp
    var uViu by Protocols.uViu
    var iViu by Protocols.iViu
    var uMgr by Protocols.uMgr
    var rMgr by Protocols.rMgr
    var date by Protocols.date
    var time by Protocols.time
    var resultViu by Protocols.resultViu
    var resultMgr by Protocols.resultMgr

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
