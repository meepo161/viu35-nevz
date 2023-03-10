package ru.avem.viu35.communication.model.devices.megaohmmeter.cs02021

//import mu.KotlinLogging
import ru.avem.viu35.communication.adapters.CRC16
import ru.avem.viu35.communication.adapters.serial.SerialAdapter
import ru.avem.viu35.communication.model.DeviceController
import ru.avem.viu35.communication.model.DeviceRegister
import java.lang.Thread.sleep
import java.nio.ByteBuffer

class CS02021(
    override val name: String,
    override val protocolAdapter: SerialAdapter,
    override val id: Byte
) : DeviceController() {
    val model = CS020201Model()
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()

    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    companion object {
        private val BAUDRATE = 9600
    }

    fun setVoltage(u: Int): Boolean {
        synchronized(protocolAdapter.connection) {
            protocolAdapter.connection.baudrate = BAUDRATE
            val byteU = (u / 10).toByte()
            val outputBuffer = ByteBuffer.allocate(5)
                .put(id)
                .put(0x01.toByte())
                .put(byteU)
            CRC16.signReversWithSlice(outputBuffer)
            protocolAdapter.write(outputBuffer.array())
            val inputArray = ByteArray(40)
            val inputBuffer = ByteBuffer.allocate(40)
            var attempt = 0
            var frameSize: Int
            do {
                sleep(2)
                frameSize = protocolAdapter.read(inputArray)
                inputBuffer.put(inputArray, 0, frameSize)
            } while (inputBuffer.position() < 5 && ++attempt < 10)
            protocolAdapter.connection.baudrate = 38400
            return frameSize > 0
        }
    }

    fun readData(): FloatArray {
        synchronized(protocolAdapter.connection) {
            protocolAdapter.connection.baudrate = BAUDRATE
            val data = FloatArray(4)
            val outputBuffer = ByteBuffer.allocate(5)
                .put(id)
                .put(0x07.toByte())
                .put(0x71.toByte())
                .put(0x64.toByte())
                .put(0x7F.toByte())
            val inputBuffer = ByteBuffer.allocate(40)
            val finalBuffer = ByteBuffer.allocate(40)
            inputBuffer.clear()
            protocolAdapter.write(outputBuffer.array())
            val inputArray = ByteArray(40)
            var attempt = 0
            do {
                sleep(2)
                val frameSize: Int = protocolAdapter.read(inputArray)
                inputBuffer.put(inputArray, 0, frameSize)
            } while (inputBuffer.position() < 16 && ++attempt < 15)
            if (inputBuffer.position() == 16) {
                inputBuffer.flip().position(2)
                finalBuffer.put(inputBuffer.get())
                finalBuffer.put(inputBuffer.get())
                finalBuffer.put(inputBuffer.get())
                finalBuffer.put(0.toByte())
                finalBuffer.put(inputBuffer.get())
                finalBuffer.put(inputBuffer.get())
                finalBuffer.put(inputBuffer.get())
                finalBuffer.put(0.toByte())
                finalBuffer.put(inputBuffer.get())
                finalBuffer.put(inputBuffer.get())
                finalBuffer.put(inputBuffer.get())
                finalBuffer.put(0.toByte())
                finalBuffer.put(inputBuffer.get())
                finalBuffer.put(inputBuffer.get())
                finalBuffer.put(inputBuffer.get())
                finalBuffer.put(0.toByte())
                finalBuffer.flip()
                data[0] = finalBuffer.float
                data[1] = finalBuffer.float
                data[2] = finalBuffer.float
                data[3] = finalBuffer.float
            }
            protocolAdapter.connection.baudrate = 38400
            return data
        }
    }

    override var isResponding: Boolean = false
        get() {
            synchronized(protocolAdapter.connection) {
                protocolAdapter.connection.baudrate = BAUDRATE
                val outputBuffer = ByteBuffer.allocate(5)
                    .put(id)
                    .put(0x07.toByte())
                    .put(0x71.toByte())
                    .put(0x64.toByte())
                    .put(0x7F.toByte())
                val inputBuffer = ByteBuffer.allocate(40)
                inputBuffer.clear()
                val writtenBytes: Int = protocolAdapter.write(outputBuffer.array())
//                KotlinLogging.logger("TAG").info("writtenBytes=$writtenBytes")

                val inputArray = ByteArray(40)
                var attempt = 0
                do {
                    sleep(2)
                    val frameSize = protocolAdapter.read(inputArray)
                    if (frameSize > 0) {
                        inputBuffer.put(inputArray, 0, frameSize)
                    }
                } while (inputBuffer.position() < 16 && ++attempt < 15)
                protocolAdapter.connection.baudrate = 38400
                return inputBuffer.position() >= 16
            }
        }

    override fun getRegisterById(idRegister: String): DeviceRegister {
        return model.registers[CS020201Model.RESPONDING_PARAM]!!
    }

    override fun checkResponsibility() {
        isResponding
    }
}
