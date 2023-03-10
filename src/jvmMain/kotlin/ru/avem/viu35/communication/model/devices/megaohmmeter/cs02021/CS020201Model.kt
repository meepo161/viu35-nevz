package ru.avem.viu35.communication.model.devices.megaohmmeter.cs02021

import ru.avem.viu35.communication.model.DeviceRegister
import ru.avem.viu35.communication.model.IDeviceModel

class CS020201Model : IDeviceModel {
    companion object {
        const val RESPONDING_PARAM = "RESPONDING_PARAM"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        RESPONDING_PARAM to DeviceRegister(0, DeviceRegister.RegisterValueType.SHORT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
