package ru.avem.viu35.io.avem.avem3

import ru.avem.library.polling.DeviceRegister
import ru.avem.library.polling.IDeviceModel

class AVEMModel : IDeviceModel {
    val U_TRMS = "U_TRMS"
    val U_AMP = "U_AMP"
    val RELAY_VALUE = "RELAY_VALUE"
    val RELAY_ON = "RELAY_ON"
    val RELAY_OFF = "RELAY_OFF"
    val RELAY_MODE = "RELAY_MODE"
    val SHUNT_MODE = "SHUNT_MODE"
    val PGA_MODE = "PGA_MODE"
    val SERIAL_NUMBER = "SERIAL_NUMBER"

    override val registers: Map<String, DeviceRegister> = mapOf(
        U_AMP to DeviceRegister(
            0x1000,
            DeviceRegister.RegisterValueType.FLOAT
        ),
        U_TRMS to DeviceRegister(
            0x1004,
            DeviceRegister.RegisterValueType.FLOAT
        ),
        RELAY_VALUE to DeviceRegister(
            0x1130,
            DeviceRegister.RegisterValueType.INT32
        ),
        RELAY_ON to DeviceRegister(
            0x1132,
            DeviceRegister.RegisterValueType.INT32
        ),
        RELAY_OFF to DeviceRegister(
            0x1134,
            DeviceRegister.RegisterValueType.INT32
        ),
        RELAY_MODE to DeviceRegister(
            0x113A,
            DeviceRegister.RegisterValueType.INT32
        ),
        SHUNT_MODE to DeviceRegister(
            0x11A0,
            DeviceRegister.RegisterValueType.FLOAT
        ),
        PGA_MODE to DeviceRegister(
            0x10C4,
            DeviceRegister.RegisterValueType.INT32
        ),
        SERIAL_NUMBER to DeviceRegister(
            0x1108,
            DeviceRegister.RegisterValueType.INT32
        )

    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
