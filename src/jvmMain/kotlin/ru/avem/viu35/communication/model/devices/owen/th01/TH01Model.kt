package ru.avem.viu35.communication.model.devices.owen.th01

import ru.avem.viu35.communication.model.DeviceRegister
import ru.avem.viu35.communication.model.IDeviceModel

class TH01Model : IDeviceModel {
    companion object {
        const val RPM = "RPM"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        RPM to DeviceRegister(0x0029, DeviceRegister.RegisterValueType.INT32)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
