package ru.avem.viu35.io.rele


import ru.avem.kserialpooler.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.utils.TransportException
import ru.avem.kserialpooler.utils.TypeByteOrder
import ru.avem.kserialpooler.utils.allocateOrderedByteBuffer
import ru.avem.library.polling.DeviceController
import ru.avem.library.polling.DeviceRegister
import ru.avem.viu35.io.megaohmmeter.cs02021.CS02021
import java.lang.Thread.sleep
import java.nio.ByteBuffer
import java.nio.ByteOrder


class ReleController(
    override val name: String, override val protocolAdapter: ModbusRTUAdapter, override val id: Byte
) : DeviceController() {
    val model = ReleModel()
    override var isResponding = false
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()
    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                when (register.valueType) {
                    DeviceRegister.RegisterValueType.SHORT -> {
                        val value =
                            protocolAdapter.readHoldingRegisters(id, register.address, 1, 9600).first().toShort()
                        register.value = value
                    }

                    DeviceRegister.RegisterValueType.FLOAT -> {
                        val modbusRegister = protocolAdapter.readHoldingRegisters(id, register.address, 2, 9600)
                            .map(ModbusRegister::toShort)
                        register.value = allocateOrderedByteBuffer(modbusRegister, TypeByteOrder.LITTLE_ENDIAN, 4).float
                    }

                    DeviceRegister.RegisterValueType.INT32 -> {
                        val modbusRegister = protocolAdapter.readHoldingRegisters(id, register.address, 2, 9600)
                            .map(ModbusRegister::toShort)
                        register.value = allocateOrderedByteBuffer(modbusRegister, TypeByteOrder.LITTLE_ENDIAN, 4).int
                    }
                }
            }
            true
        } catch (e: TransportException) {
            false
        }
    }

    override fun readAllRegisters() {
        model.registers.values.forEach {
            readRegister(it)
        }
    }

    override fun <T : Number> writeRegister(register: DeviceRegister, value: T) {
        isResponding = try {
            when (value) {
                is Float -> {
                    val bb = ByteBuffer.allocate(4).putFloat(value).order(ByteOrder.LITTLE_ENDIAN)
                    val registers = listOf(ModbusRegister(bb.getShort(2)), ModbusRegister(bb.getShort(0)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers, 9600)
                    }
                }

                is Int -> {
                    val bb = ByteBuffer.allocate(4).putInt(value).order(ByteOrder.LITTLE_ENDIAN)
                    val registers = listOf(ModbusRegister(bb.getShort(2)), ModbusRegister(bb.getShort(0)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers, 9600)
                    }
                }

                is Short -> {
                    transactionWithAttempts {
                        protocolAdapter.presetSingleRegister(id, register.address, ModbusRegister(value), 9600)
                    }
                }

                else -> {
                    throw UnsupportedOperationException("Method can handle only with Float, Int and Short")
                }
            }
            true
        } catch (e: TransportException) {
            false
        }
    }

    override fun writeRegisters(register: DeviceRegister, values: List<Short>) {
        val registers = values.map { ModbusRegister(it) }
        transactionWithAttempts {
            protocolAdapter.presetMultipleRegisters(id, register.address, registers, 9600)
        }
    }

    override fun writeRequest(request: String) {

    }

    override fun checkResponsibility() {
        try {
            model.registers.values.firstOrNull()?.let {
                readRegister(it)
            }
        } catch (ignored: TransportException) {
        }
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)

    fun on(register: Int) {
        writeRegister(getRegisterById("R${register}"), 0x0100.toShort())
    }

    fun off(register: Int) {
        writeRegister(getRegisterById("R${register}"), 0x0200.toShort())
    }

    fun offAll() {
        for (register in 1..16) {
            writeRegister(getRegisterById("R$register"), 0x0200.toShort())
        }
    }
}
