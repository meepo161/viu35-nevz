package ru.avem.viu35.io.rele

import ru.avem.library.polling.DeviceRegister
import ru.avem.library.polling.IDeviceModel


class ReleModel : IDeviceModel {
        val R1 =  "R1"
        val R2 =  "R2"
        val R3 =  "R3"
        val R4 =  "R4"
        val R5 =  "R5"
        val R6 =  "R6"
        val R7 =  "R7"
        val R8 =  "R8"
        val R9 =  "R9"
        val R10 = "R10"
        val R11 = "R11"
        val R12 = "R12"
        val R13 = "R13"
        val R14 = "R14"
        val R15 = "R15"
        val R16 = "R16"
        val R17 = "R17"
        val R18 = "R18"
        val R19 = "R19"
        val R20 = "R20"
        val R21 = "R21"
        val R22 = "R22"
        val R23 = "R23"
        val R24 = "R24"
        val R25 = "R25"
        val R26 = "R26"
        val R27 = "R27"
        val R28 = "R28"
        val R29 = "R29"
        val R30 = "R30"
        val R31 = "R31"
        val R32 = "R32"

    override val registers: Map<String, DeviceRegister> = mapOf(
        R1 to DeviceRegister(1, DeviceRegister.RegisterValueType.SHORT),
        R2 to DeviceRegister(2, DeviceRegister.RegisterValueType.SHORT),
        R3 to DeviceRegister(3, DeviceRegister.RegisterValueType.SHORT),
        R4 to DeviceRegister(4, DeviceRegister.RegisterValueType.SHORT),
        R5 to DeviceRegister(5, DeviceRegister.RegisterValueType.SHORT),
        R6 to DeviceRegister(6, DeviceRegister.RegisterValueType.SHORT),
        R7 to DeviceRegister(7, DeviceRegister.RegisterValueType.SHORT),
        R8 to DeviceRegister(8, DeviceRegister.RegisterValueType.SHORT),
        R9 to DeviceRegister(9, DeviceRegister.RegisterValueType.SHORT),
        R10 to DeviceRegister(10, DeviceRegister.RegisterValueType.SHORT),
        R11 to DeviceRegister(11, DeviceRegister.RegisterValueType.SHORT),
        R12 to DeviceRegister(12, DeviceRegister.RegisterValueType.SHORT),
        R13 to DeviceRegister(13, DeviceRegister.RegisterValueType.SHORT),
        R14 to DeviceRegister(14, DeviceRegister.RegisterValueType.SHORT),
        R15 to DeviceRegister(15, DeviceRegister.RegisterValueType.SHORT),
        R16 to DeviceRegister(16, DeviceRegister.RegisterValueType.SHORT),
        R17 to DeviceRegister(17, DeviceRegister.RegisterValueType.SHORT),
        R18 to DeviceRegister(18, DeviceRegister.RegisterValueType.SHORT),
        R19 to DeviceRegister(19, DeviceRegister.RegisterValueType.SHORT),
        R20 to DeviceRegister(20, DeviceRegister.RegisterValueType.SHORT),
        R21 to DeviceRegister(21, DeviceRegister.RegisterValueType.SHORT),
        R22 to DeviceRegister(22, DeviceRegister.RegisterValueType.SHORT),
        R23 to DeviceRegister(23, DeviceRegister.RegisterValueType.SHORT),
        R24 to DeviceRegister(24, DeviceRegister.RegisterValueType.SHORT),
        R25 to DeviceRegister(25, DeviceRegister.RegisterValueType.SHORT),
        R26 to DeviceRegister(26, DeviceRegister.RegisterValueType.SHORT),
        R27 to DeviceRegister(27, DeviceRegister.RegisterValueType.SHORT),
        R28 to DeviceRegister(28, DeviceRegister.RegisterValueType.SHORT),
        R29 to DeviceRegister(29, DeviceRegister.RegisterValueType.SHORT),
        R30 to DeviceRegister(30, DeviceRegister.RegisterValueType.SHORT),
        R31 to DeviceRegister(31, DeviceRegister.RegisterValueType.SHORT),
        R32 to DeviceRegister(32, DeviceRegister.RegisterValueType.SHORT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}