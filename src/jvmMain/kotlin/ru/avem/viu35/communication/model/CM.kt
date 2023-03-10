package ru.avem.viu35.communication.model

import ru.avem.kserialpooler.Connection
import ru.avem.kserialpooler.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.utils.SerialParameters
import ru.avem.viu35.communication.adapters.serial.SerialAdapter
import ru.avem.viu35.communication.model.devices.avem.avem3.AVEM3
import ru.avem.viu35.communication.model.devices.avem.ikas10.IKAS10
import ru.avem.viu35.communication.model.devices.hpmont.HPMont
import ru.avem.viu35.communication.model.devices.megaohmmeter.cs02021.CS02021
import ru.avem.viu35.communication.model.devices.optimus.Optimus
import ru.avem.viu35.communication.model.devices.owen.pr.PR
import ru.avem.viu35.communication.model.devices.owen.th01.TH01
import ru.avem.viu35.communication.model.devices.owen.trm202.TRM202
import ru.avem.viu35.communication.model.devices.rele.ReleController
import ru.avem.viu35.communication.model.devices.satec.pm130.PM130
import java.lang.Thread.sleep
import kotlin.concurrent.thread

object CM {
    enum class DeviceID(description: String) {
        DD2("ОВЕН ПР102 - Программируемое реле"),
        DD3("РЕЛЕ1 - Программируемое реле"),
        DD4("РЕЛЕ2 - Программируемое реле"),
        PV24("АВЭМ-3-04 - Прибор ВВ"),
    }

    private var isConnected = false

    private val mainConnection = Connection(
        adapterName = "CP2103 USB to RS-485",
        serialParameters = SerialParameters(8, 0, 1, 9600),
        timeoutRead = 100,
        timeoutWrite = 100
    ).apply {
        connect()
        isConnected = true
    }

    private val hpMontConnection = Connection(
        adapterName = "CP2103 USB to RS-485-1",
        serialParameters = SerialParameters(8, 0, 1, 38400),
        timeoutRead = 100,
        timeoutWrite = 100
    ).apply {
        connect()
        isConnected = true
    }

    private val optimusConnection = Connection(
        adapterName = "CP2103 USB to RS-485-2",
        serialParameters = SerialParameters(8, 0, 1, 38400),
        timeoutRead = 100,
        timeoutWrite = 100
    ).apply {
        connect()
        isConnected = true
    }

    private val modbusAdapter = ModbusRTUAdapter(mainConnection)
    private val meggerAdapter = SerialAdapter(mainConnection)
    private val hpmontAdapter = ModbusRTUAdapter(hpMontConnection)
    private val optimusAdapter = ModbusRTUAdapter(optimusConnection)

    private val devices: Map<DeviceID, IDeviceController> = mapOf(
        DeviceID.DD2 to PR(DeviceID.DD2.toString(), modbusAdapter, 2),
        DeviceID.DD3 to ReleController(DeviceID.DD3.toString(), modbusAdapter, 3),
        DeviceID.DD4 to ReleController(DeviceID.DD4.toString(), modbusAdapter, 4),
        DeviceID.PV24 to AVEM3(DeviceID.PV24.toString(), modbusAdapter, 24),
    )

    init {
        with(devices.values.groupBy { it.protocolAdapter.connection }) {
            keys.forEach { connection ->
                thread(isDaemon = true) {
                    while (true) {
                        if (isConnected) {
                            this[connection]!!.forEach {
                                it.readPollingRegisters()
                                sleep(1)
                            }
                        }
                        sleep(1)
                    }
                }
            }
        }
        thread(isDaemon = true) {
            while (true) {
                if (isConnected) {
                    devices.values.forEach {
                        it.writeWritingRegisters()
                        sleep(1)
                    }
                }
                sleep(1)
            }
        }
    }

    fun <T : IDeviceController> device(deviceID: DeviceID): T {
        return devices[deviceID] as T
    }

    fun startPoll(deviceID: DeviceID, registerID: String, block: (Number) -> Unit) {
        val device = device<IDeviceController>(deviceID)
        val register = device.getRegisterById(registerID)
        register.addObserver { _, arg ->
            block(arg as Number)
        }
        device.addPollingRegister(register)
    }

    fun clearPollingRegisters() {
        devices.values.forEach(IDeviceController::removeAllPollingRegisters)
        devices.values.forEach(IDeviceController::removeAllWritingRegisters)
    }

    fun removePollingRegister(deviceID: DeviceID, registerID: String) {
        val device = device<IDeviceController>(deviceID)
        val register = device.getRegisterById(registerID)
        register.deleteObservers()
        device.removePollingRegister(register)
    }

//    fun checkDevices(checkedDevices: List<IDeviceController>): List<DeviceID> {
//        checkedDevices.forEach(IDeviceController::checkResponsibility)
//        return listOfUnresponsiveDevices(checkedDevices)
//    }

    fun listOfUnresponsiveDevices(checkedDevices: List<IDeviceController>) =
        devices.filter { checkedDevices.toList().contains(it.value) && !it.value.isResponding }.keys.toList()

    fun addWritingRegister(deviceID: DeviceID, registerID: String, value: Number) {
        val device = device<IDeviceController>(deviceID)
        val register = device.getRegisterById(registerID)
        device.addWritingRegister(register to value)
    }
}
