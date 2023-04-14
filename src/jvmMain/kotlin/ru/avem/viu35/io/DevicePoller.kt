package ru.avem.viu35.io

import ru.avem.kserialpooler.Connection
import ru.avem.kserialpooler.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.adapters.serial.SerialAdapter
import ru.avem.kserialpooler.utils.SerialParameters
import ru.avem.library.polling.IDeviceController
import ru.avem.library.polling.SimplePollingModel
import ru.avem.viu35.addTo
import ru.avem.viu35.io.avem.avem3.AVEM3
import ru.avem.viu35.io.owen.pr.PR
import ru.avem.viu35.io.rele.ReleController

object DevicePoller : SimplePollingModel() {
    private val connectionSlow = Connection(
        adapterName = "CP2103 USB to RS-485\n",
        serialParameters = SerialParameters(8, 0, 1, 9600),
        timeoutRead = 100,
        timeoutWrite = 100
    ).apply { connect() }

    private val main = ModbusRTUAdapter(Connection().apply { connect() })
    private val seri = SerialAdapter(connectionSlow)

    private val devs = mutableListOf<IDeviceController>()

    val DD2 = PR("DD2", main, 2).addTo(devs)
    val DD3 = ReleController("DD3", main, 3).addTo(devs)
    val DD4 = ReleController("DD3", main, 4).addTo(devs)
    val PV21 = AVEM3("PV21", main, 21).addTo(devs)

    override val deviceControllers: Map<String, IDeviceController> = devs.associateBy { it.name }

    init {
        start()
    }
}
