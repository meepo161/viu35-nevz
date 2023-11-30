package ru.avem.viu35.io


import ru.avem.kserialpooler.Connection
import ru.avem.kserialpooler.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.utils.SerialParameters
import ru.avem.library.polling.IDeviceController
import ru.avem.library.polling.SimplePollingModel
import ru.avem.viu35.addTo
import ru.avem.viu35.io.avem.avem3.AVEM
import ru.avem.viu35.io.avem.avem9.AVEM9
import ru.avem.viu35.io.avem.latr.LatrController
import ru.avem.viu35.io.owen.pr.PR
import ru.avem.viu35.io.rele.ReleController

object DevicePoller : SimplePollingModel() {
    val connection = Connection(
        adapterName = "CP2103 USB to RS-485",
        serialParameters = SerialParameters(8, 0, 1, 38400),
        timeoutRead = 100,
        timeoutWrite = 100
    ).apply {
        connect()
    }

    private val main = ModbusRTUAdapter(connection)

    var devs = mutableListOf<IDeviceController>()

    val DD2 = PR("DD2", main, 2).addTo(devs)
    val DD3 = ReleController("DD3", main, 3).addTo(devs)
    val DD4 = ReleController("DD4", main, 4).addTo(devs)

    val PV22 = AVEM("PV22", main, 22).addTo(devs)
    val PV23 = AVEM("PV23", main, 23).addTo(devs)

    val PA11 = AVEM("PA11", main, 11).addTo(devs)
    val PA12 = AVEM("PA12", main, 12).addTo(devs)
    val PA13 = AVEM("PA13", main, 13).addTo(devs)
    val PA14 = AVEM("PA14", main, 14).addTo(devs)
    val PA15 = AVEM("PA15", main, 15).addTo(devs)
    val PA16 = AVEM("PA16", main, 16).addTo(devs)
    val PA17 = AVEM("PA17", main, 17).addTo(devs)
    val PA18 = AVEM("PA18", main, 18).addTo(devs)
    val PA19 = AVEM("PA19", main, 19).addTo(devs)
    val PA20 = AVEM("PA20", main, 20).addTo(devs)
    val PA21 = AVEM("PA21", main, 21).addTo(devs)

    var listPA = listOf<AVEM>(
        PA11,
        PA12,
        PA13,
        PA14,
        PA15,
        PA16,
        PA17,
        PA18,
        PA19,
        PA20
    )
    val PR65 = AVEM9("PR65", main, 65).addTo(devs)
    val ATR240 = LatrController("ATR240", main, (240).toByte()).addTo(devs)
    val ATR241 = LatrController("ATR241", main, (241).toByte()).addTo(devs)

//    val PR65 = CS02021("PR65", cS0202Adapter, 65).addTo(devs)

    override val deviceControllers: Map<String, IDeviceController> = devs.associateBy { it.name }

    init {
        start()
    }

    fun startCP2103() {
        start()
    }
    fun stopCP2103() {
        stop()
    }
}
