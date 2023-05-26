package ru.avem.viu35.io.adapters.cs0202.requests

import ru.avem.kserialpooler.utils.LogicException
import ru.avem.viu35.io.adapters.cs0202.requests.Cs0202Request.Companion.BYTE_SIZE_OF_CRC
import ru.avem.viu35.io.adapters.cs0202.requests.Cs0202Request.Companion.BYTE_SIZE_OF_DATA_RESPONSE
import ru.avem.viu35.io.adapters.cs0202.requests.Cs0202Request.Companion.BYTE_SIZE_OF_DEVICE_ID
import ru.avem.viu35.io.adapters.cs0202.requests.Cs0202Request.Companion.BYTE_SIZE_OF_FUNCTION
import ru.avem.viu35.io.adapters.cs0202.requests.Cs0202Request.Companion.REQUEST_SIZE
import ru.avem.viu35.io.adapters.cs0202.utils.CRC
import java.nio.ByteBuffer

class ImitationPressedButtons(override val deviceId: Byte, dataRequestRequire: Byte) : Cs0202Request {
    companion object {
        const val FUNCTION_CODE: Byte = 0x05

        const val BUTTON_RX: Byte = 0xFE.toByte()
        const val BUTTON_UST: Byte = 0xFD.toByte()
        const val BUTTON_UP: Byte = 0xF7.toByte()
        const val BUTTON_DOWN: Byte = 0xFB.toByte()
        const val BUTTON_IZM: Byte = 0xEF.toByte()
        const val BUTTON_PIT: Byte = 0xDF.toByte()
        const val BUTTON_EMK: Byte = 0xFC.toByte()
        const val BUTTON_PEREDACHA: Byte = 0x55.toByte()
    }

    override val function: Byte = FUNCTION_CODE
    override val dataRequest: Byte = checkDataRequest(dataRequestRequire)

    private fun checkDataRequest(dataRequestRequire: Byte) =
        when (dataRequestRequire) {
            BUTTON_RX -> BUTTON_RX
            BUTTON_UST -> BUTTON_UST
            BUTTON_UP -> BUTTON_UP
            BUTTON_DOWN -> BUTTON_DOWN
            BUTTON_IZM -> BUTTON_IZM
            BUTTON_PIT -> BUTTON_PIT
            BUTTON_EMK -> BUTTON_EMK
            BUTTON_PEREDACHA -> BUTTON_PEREDACHA
            else -> throw LogicException("Ошибка прибора: Неизвестный button Require [${dataRequestRequire}]")
        }

    override fun getResponseSize() =
        BYTE_SIZE_OF_DEVICE_ID + BYTE_SIZE_OF_FUNCTION + BYTE_SIZE_OF_DATA_RESPONSE + BYTE_SIZE_OF_CRC

    override fun getRequestBytes(): ByteArray = ByteBuffer.allocate(REQUEST_SIZE).apply {
        put(deviceId)
        put(function)
        put(dataRequest)

    }.also {
        CRC.sign(it)
    }.array()

    fun parseResponse(response: ByteArray) {
        checkResponse(response)
    }

    override fun checkFunctionIsError(response: ByteArray) {
        when (response[Cs0202Request.ERROR_POSITION]) {
            0x03.toByte() -> throw LogicException("Ошибка прибора: некорректный код кнопки, выбранной для имитации нажатия")
            0x04.toByte() -> throw LogicException("Ошибка прибора: попытка имитации нажатия кнопки ИЗМ когда есть напряжение в цепи измерения, процесс измерения сопротивления изоляции не активизирован")
            else -> throw LogicException("Ошибка прибора: Неизвестная ошибка [${response[Cs0202Request.ERROR_POSITION]}]")
        }
    }
}
