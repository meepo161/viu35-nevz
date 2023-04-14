package ru.avem.viu35.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import isTestRunning
import ru.avem.library.polling.IDeviceController
import ru.avem.viu35.io.DevicePoller
import ru.avem.viu35.io.DevicePoller.DD2
import ru.avem.viu35.io.DevicePoller.DD3
import ru.avem.viu35.io.DevicePoller.DD4
import ru.avem.viu35.io.DevicePoller.PV21
import ru.avem.viu35.ms
import ru.avem.viu35.tests.Field
import ru.avem.viu35.utils.formatDigits
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import kotlin.concurrent.thread
import kotlin.experimental.and

class TestViewModel(private var mainViewModel: MainScreenViewModel) : ScreenModel {

    private var buttonPostStartPressed = false

    val checkedDevices = listOf(DD3)//listOf(DD2, DD3, DD4, PV21)

    val fields = listOf<Field>()

    @Volatile
    var cause: String = ""
        set(value) {
            if (value.isNotEmpty()) {
                isTestRunning = false
                if (!field.contains(value)) field += "${if (field != "") "/" else ""}$value"
            } else {
                field = value
            }
        }

    var isStartPressed = false

    fun init() {
        isTestRunning = true
        var cause = ""
    }

    private fun IDeviceController.checkResponsibilityAndNotify() {
        if (isTestRunning) {
            with(this) {
                checkResponsibility()
                if (!isResponding) cause = "$name ${"не отвечает"}"
            }
        }
    }

    fun initDD2() {
        buttonPostStartPressed = false
        with(DD2) {
            checkResponsibilityAndNotify()
            offAllKMs()
            init()
            DevicePoller.addWritingRegister(name, model.CMD, 1.toShort())
            DevicePoller.startPoll(name, model.STATE) {}

            DevicePoller.startPoll(name, model.DI_01_16_TRIG) { value ->
                val isStopPressed = value.toShort() and 0b1 != 0.toShort() //1
                if (isStopPressed) cause = "Нажали кнопку СТОП на кнопочном посту"

                buttonPostStartPressed = value.toShort() and 0b10 != 0.toShort() //2
            }

            DevicePoller.startPoll(name, model.DI_01_16_TRIG_INV) { value ->
                if (value.toShort() and 0b100 != 0.toShort()) cause = "Сработала защита: Концевик двери Зоны" //3
                if (value.toShort() and 0b10000 != 0.toShort()) cause = "Сработала защита: Концевик двери ШСО" //5
                if (value.toShort() and 0b100000 != 0.toShort()) cause = "Сработала защита: Токовая защита ОИ" //6
                if (value.toShort() and 0b1000000 != 0.toShort()) cause = "Сработала защита: Токовая защита ВИУ" //7
            }
        }
    }

    fun initDevices() {
//        if (isTestRunning) initDD2() TODO
        if (isTestRunning) {
            thread {
                while (isTestRunning) {
                    checkedDevices.forEach { it.checkResponsibilityAndNotify() }
                    wait(1)
                }
            }
        }
        if (isTestRunning) fields.forEach(Field::poll)
    }

    fun schemeVIU() {
//        CM.device<PR>(CM.DeviceID.DD2).onVIU()
        mainViewModel.listCheckBoxesViu.forEachIndexed { index, mutableState ->
            if (mutableState.value) {
                DD3.on(index + 1)
            }
        }

    }

    fun schemeMeger() {
        mainViewModel.listCheckBoxesMeger.forEachIndexed { index, mutableState ->
            if (mutableState.value) {
                DD3.on(index + 1)
            }
        }
    }

    fun start() {
        if (!isTestRunning && !checkTestSettings()) {
            thread(isDaemon = true) {
                initTest()
                if (isTestRunning) {
                    mainViewModel.listCheckBoxesViu.forEachIndexed { index, isChecked ->
                        if (isChecked.value) {
                            DD3.on(index + 1)
                        }
                    }
                    if (isTestRunning) {
                        sleepTime(mainViewModel.specifiedTime.value.toDouble())
                    }
                    DD3.offAll()
                }
                if (isTestRunning) {
                    mainViewModel.listCheckBoxesMeger.forEachIndexed { index, isChecked ->
                        if (isChecked.value) {
                            DD3.on(index + 1)
                        }
                    }
                }
                if (isTestRunning) {
                    sleepTime(mainViewModel.specifiedTime.value.toDouble())
                }
                DD3.offAll()
                finalizeTest()
                println("cause = $cause")
            }
        }
    }

    private fun checkTestSettings(): Boolean {
        mainViewModel.titleDialog.value = "Ошибка"
        mainViewModel.textDialog.value = ""
        if (mainViewModel.listCheckBoxesMeger.none { it.value } && mainViewModel.listCheckBoxesViu.none { it.value }) {
            mainViewModel.textDialog.value += "Не выбрано ни одно испытание\n"
            mainViewModel.dialogVisibleState.value = true
        } else if (mainViewModel.selectedObject.value == null) {
            mainViewModel.textDialog.value += "Не выбран объект испытания\n"
            mainViewModel.dialogVisibleState.value = true
        } else if (mainViewModel.selectedField.value == null) {
            mainViewModel.textDialog.value += "Не выбран тип объекта испытания\n"
            mainViewModel.dialogVisibleState.value = true
        } else if (mainViewModel.dot1.value.isEmpty()) {
            mainViewModel.textDialog.value += "Не указана точка 1\n"
            mainViewModel.dialogVisibleState.value = true
        } else if (mainViewModel.dot2.value.isEmpty()) {
            mainViewModel.textDialog.value += "Не указана точка 2\n"
            mainViewModel.dialogVisibleState.value = true
        }
        return mainViewModel.dialogVisibleState.value
    }

    private fun initTest() {
        cause = ""
        isTestRunning = true
        mainViewModel.mutableStateIsRunning.value = false
        initDevices()
    }

    private fun finalizeTest() {
        isTestRunning = false
        DevicePoller.clearPollingRegisters()
        DevicePoller.stop()
        mainViewModel.mutableStateIsRunning.value = true
    }

    fun stop() {
        cause = "Остановлено оператором"
        isTestRunning = false
    }

    fun sleepTime(seconds: Double) {
        val time = seconds * 1000 + System.currentTimeMillis()
        while (isTestRunning && System.currentTimeMillis() < time) {
            mainViewModel.measuredTime.value = ((time - System.currentTimeMillis()) / 1000).formatDigits(0)
            sleep(100)
        }
    }

    fun wait(
        seconds: Number, step: Long = 10,
        isNeedContinue: () -> Boolean = { isTestRunning },
        onTick: (Double) -> Unit = {}
    ) {
        val startStamp = ms()
        while (isNeedContinue()) {
            val progress = (ms() - startStamp) / (seconds.toDouble() * 1000)
            if (progress < 1.0) {
                onTick(progress * seconds.toDouble())
                sleep(step)
            } else {
                onTick(seconds.toDouble())
                break
            }
        }
    }


    fun appendMessageToLog(msg: String) {
        println("${SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())} | $msg")
    }
}