package ru.avem.viu35.io.avem.avem3

import ru.avem.library.polling.DeviceRegister
import ru.avem.library.polling.IDeviceModel

class AVEM3Model : IDeviceModel {
    companion object {
        const val U_TRMS = "U_TRMS"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        U_TRMS to DeviceRegister(
            0x1004,
            DeviceRegister.RegisterValueType.FLOAT
        )
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
