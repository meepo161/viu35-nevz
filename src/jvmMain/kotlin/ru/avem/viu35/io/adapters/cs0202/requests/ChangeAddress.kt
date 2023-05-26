package ru.avem.viu35.io.adapters.cs0202.requests

import ru.avem.kserialpooler.utils.InvalidCommandException
import ru.avem.kserialpooler.utils.LogicException
import ru.avem.viu35.io.adapters.cs0202.requests.Cs0202Request.Companion.BYTE_SIZE_OF_CRC
import ru.avem.viu35.io.adapters.cs0202.requests.Cs0202Request.Companion.BYTE_SIZE_OF_DATA_RESPONSE
import ru.avem.viu35.io.adapters.cs0202.requests.Cs0202Request.Companion.BYTE_SIZE_OF_DEVICE_ID
import ru.avem.viu35.io.adapters.cs0202.requests.Cs0202Request.Companion.BYTE_SIZE_OF_FUNCTION
import ru.avem.viu35.io.adapters.cs0202.requests.Cs0202Request.Companion.REQUEST_SIZE
import ru.avem.viu35.io.adapters.cs0202.utils.CRC
import java.nio.ByteBuffer
import kotlin.experimental.or

class ChangeAddress(override val deviceId: Byte, newAddressRequire: Int) : Cs0202Request {
    companion object {
        const val FUNCTION_CODE: Byte = 0x08
    }

    override val function: Byte = FUNCTION_CODE
    override val dataRequest: Byte = checkDataRequest(newAddressRequire)

    private fun checkDataRequest(dataRequestRequire: Int) =
        when (dataRequestRequire) {
            in 1..250 -> dataRequestRequire.toByte()
            else -> throw InvalidCommandException("Ошибка команды: предпринята попытка установить некорректный адрес [${dataRequestRequire}], который должен быть 1..250")
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
        if (function == (response[Cs0202Request.FUNCTION_POSITION] or 0x80.toByte())) {
            when (response[Cs0202Request.ERROR_POSITION]) {
                0x03.toByte() -> throw LogicException("Ошибка прибора: не корректное значение нового сетевого адреса, изменение сетевого адреса не произошло")
                else -> throw LogicException("Ошибка прибора: Неизвестная ошибка [${response[Cs0202Request.ERROR_POSITION]}]")
            }
        }
    }
}
