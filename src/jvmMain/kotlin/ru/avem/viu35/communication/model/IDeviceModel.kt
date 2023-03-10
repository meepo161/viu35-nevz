package ru.avem.viu35.communication.model

interface IDeviceModel {
    val registers: Map<String, DeviceRegister>

    fun getRegisterById(idRegister: String): DeviceRegister
}
