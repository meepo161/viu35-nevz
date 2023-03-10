package ru.avem.viu35.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import isRunning
import ru.avem.viu35.communication.model.CM
import ru.avem.viu35.communication.model.CM.DeviceID.DD2
import ru.avem.viu35.communication.model.CM.device
import ru.avem.viu35.communication.model.IDeviceController
import ru.avem.viu35.communication.model.devices.owen.pr.PR
import ru.avem.viu35.communication.model.devices.owen.pr.PRModel
import ru.avem.viu35.communication.model.devices.rele.ReleController
import ru.avem.viu35.composables.ConfirmDialog
import java.text.SimpleDateFormat
import kotlin.concurrent.thread
import kotlin.experimental.and

class TestViewModel(private var mainViewModel: MainScreenViewModel) : ScreenModel {

    private val checkableDevices: MutableList<IDeviceController> = mutableListOf()

    @Volatile
    var cause: String = ""
        set(value) {
            if (value.isNotEmpty()) {
                isRunning = false
                if (!field.contains(value)) field += "${if (field != "") "/" else ""}$value"
            } else {
                field = value
            }
        }

    var isStartPressed = false

    fun init() {
        isRunning = true
        var cause = ""
    }

    fun initPR() {
        addCheckableDevice(DD2)
        CM.addWritingRegister(
            DD2,
            PRModel.CMD,
            1.toShort()
        )
        device<PR>(DD2).initWithoutProtections()
    }

    fun startPollControlUnit() {
        if (isRunning) {
            CM.startPoll(DD2, PRModel.STATE) {}
            CM.startPoll(DD2, PRModel.DI_01_16_TRIG) { value ->
                val isStopPressed = value.toShort() and 0b1 != 0.toShort() // 1
                if (isStopPressed) stop()

                isStartPressed = value.toShort() and 0b10 != 0.toShort() // 2
            }
            CM.startPoll(DD2, PRModel.DI_01_16_TRIG_INV) { value ->
                if (value.toShort() and 0b100 != 0.toShort()) { // 3
                    cause = "сработала токовая защита ВИУ"
//                    testModel.protections.overCurrentVIU.set()
                }
                if (value.toShort() and 0b10000 != 0.toShort()) { // 5
                    cause = "сработала токовая защита ОИ"
//                    testModel.protections.overCurrentOI.set()
                }
                if (value.toShort() and 0b1000000 != 0.toShort()) { // 7
                    cause = "открыты двери ШСО"
//                    testModel.protections.doorsSHSO.set()
                }
                if (value.toShort() and 0b10000000 != 0.toShort()) { // 8
                    cause = "открыты двери зоны"
//                    testModel.protections.doorsZone.set()
                }
            }
        }
    }

    fun initDevices() {
        appendMessageToLog("Инициализация приборов")

        thread(isDaemon = true) {
            while (isRunning) {
                val list = CM.listOfUnresponsiveDevices(checkableDevices)
                if (list.isNotEmpty()) {
                    val listNameDevices = mutableListOf<String>()
                    list.forEach {
                        when (it.name) {
                            "DD2" -> {
                                listNameDevices.add("DD2 ОВЕН ПР102 - Программируемое реле ")
                            }

                            "PV24" -> {
                                listNameDevices.add("PV24 АВЭМ-3-04 - Прибор ВВ ")
                            }
                        }
                    }
                    cause =
                        "следующие приборы не отвечают на запросы: $listNameDevices"
                            .replace("[", "")
                            .replace("]", "")
                }
                Thread.sleep(100)
            }
        }
    }

    fun schemeVIU() {
//        CM.device<PR>(CM.DeviceID.DD2).onVIU()
        mainViewModel.listCheckBoxesViu.forEachIndexed { index, mutableState ->
            if (mutableState.value) {
                device<ReleController>(CM.DeviceID.DD3).on(index + 1)
            }
        }

    }

    fun schemeMeger() {
        mainViewModel.listCheckBoxesMeger.forEachIndexed { index, mutableState ->
            if (mutableState.value) {
                device<ReleController>(CM.DeviceID.DD3).on(index + 1)
            }
        }
    }

    fun start() {
        thread {
            mainViewModel.dialogVisibleState.value = true
            init()
            startPollControlUnit()
            println(0)
            initPR()
            println("01")
            initDevices()
            if (isRunning) schemeVIU()
            println("schemeVIU")
            if (isRunning) Thread.sleep(5000)
            println("sleep")
            if (isRunning) device<ReleController>(CM.DeviceID.DD3).offAll()
            println("offAll")
            if (isRunning) schemeMeger()
            println("schemeMeger")
            if (isRunning) Thread.sleep(5000)
            println("sleep")
            device<ReleController>(CM.DeviceID.DD3).offAll()
            println("offAll")
            finalizeDevices()
        }
    }

    fun stop() {
        cause = "Остановлено оператором"
        isRunning = false
    }

    fun addCheckableDevice(id: CM.DeviceID) {
        with(device<IDeviceController>(id)) {
            checkResponsibility()
            checkableDevices.add(this)
        }
    }

    fun removeCheckableDevice(id: CM.DeviceID) {
        checkableDevices.remove(device(id))
    }

    fun appendMessageToLog(msg: String) {
        println("${SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())} | $msg")
    }

    fun finalizeDevices() {
        checkableDevices.clear()
        CM.clearPollingRegisters()
    }
}