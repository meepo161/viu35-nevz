package ru.avem.viu35.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import cafe.adriel.voyager.core.model.ScreenModel
import isTestRunning
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.library.polling.IDeviceController
import ru.avem.viu35.af
import ru.avem.viu35.database.entities.Protocol
import ru.avem.viu35.io.DevicePoller
import ru.avem.viu35.io.DevicePoller.ATR240
import ru.avem.viu35.io.DevicePoller.ATR241
import ru.avem.viu35.io.DevicePoller.DD2
import ru.avem.viu35.io.DevicePoller.DD3
import ru.avem.viu35.io.DevicePoller.DD4
import ru.avem.viu35.io.DevicePoller.PA21
import ru.avem.viu35.io.DevicePoller.PR65
import ru.avem.viu35.io.DevicePoller.PV22
import ru.avem.viu35.io.DevicePoller.PV23
import ru.avem.viu35.io.DevicePoller.listPA
import ru.avem.viu35.io.avem.avem9.AVEM9Model
import ru.avem.viu35.ms
import ru.avem.viu35.tests.Field
import ru.avem.viu35.utils.formatDigits
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.experimental.and
import kotlin.math.abs

class TestViewModel(private var mainViewModel: MainScreenViewModel, private val scope: CoroutineScope) : ScreenModel {
    private var buttonPostStartPressed = false

    val fields = listOf<Field>()
    private var voltageLatr1 = 0.0
    private var voltageLatr2 = 0.0
    private var needDevices = mutableListOf<IDeviceController>()

    @Volatile
    private var listCurrentsState = MutableList(10) { false }

    @Volatile
    private var statusMGR = 0

    @Volatile
    private var r15 = 0.0

    @Volatile
    private var chopper = false

    @Volatile
    private var isMgrRunning = false

    @Volatile
    private var isViuRunning = false

    @Volatile
    private var currentDoArn = false

    @Volatile
    private var listMessagesLog = mutableListOf<String>()

    @Volatile
    var cause: String = ""
        set(value) {
            if (value.isNotEmpty()) {
                isTestRunning = false
                if (!field.contains(value)) field += "${if (field != "") "/" else ""}$value"
//                appendOneMessageToLog(cause)
            } else {
                field = value
            }
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
        chopper = false
        DD2.checkResponsibility()
        if (!DD2.isResponding) cause = "ПР102 не отвечает"
        if (isTestRunning) {
            with(DD2) {
                needDevices.add(this)
                offAllKMs()
                init()
                DevicePoller.addWritingRegister(name, model.CMD, 1.toShort())
                DevicePoller.startPoll(name, model.STATE) {}

                DevicePoller.startPoll(name, model.DI_01_16_TRIG) { value ->
                    val isStopPressed = value.toShort() and 0b100 != 0.toShort()
                    if (isStopPressed) cause = "Нажали кнопку СТОП на кнопочном посту"
                }

                DevicePoller.startPoll(name, model.DI_01_16_TRIG_INV) { value ->
                    if (value.toShort() and 0b10000 != 0.toShort())
                        if (mainViewModel.listCheckBoxesViu[0].value) protectionCurrent(1, "> 100 мА")//5
                    if (value.toShort() and 0b100000 != 0.toShort())
                        if (mainViewModel.listCheckBoxesViu[1].value) protectionCurrent(1, "> 100 мА")//5
                    if (value.toShort() and 0b1000000 != 0.toShort())
                        if (mainViewModel.listCheckBoxesViu[2].value) protectionCurrent(2, "> 100 мА")//6
                    if (value.toShort() and 0b10000000 != 0.toShort())
                        if (mainViewModel.listCheckBoxesViu[3].value) protectionCurrent(3, "> 100 мА")//7
                    if (value.toShort() and 0b100000000 != 0.toShort())
                        if (mainViewModel.listCheckBoxesViu[4].value) protectionCurrent(4, "> 100 мА")//8
                    if (value.toShort() and 0b1000000000 != 0.toShort())
                        if (mainViewModel.listCheckBoxesViu[5].value) protectionCurrent(5, "> 100 мА")//9
                    if (value.toShort() and 0b10000000000 != 0.toShort())
                        if (mainViewModel.listCheckBoxesViu[6].value) protectionCurrent(6, "> 100 мА")//10
                    if (value.toShort() and 0b100000000000 != 0.toShort())
                        if (mainViewModel.listCheckBoxesViu[7].value) protectionCurrent(7, "> 100 мА")//11
                    if (value.toShort() and 0b1000000000000 != 0.toShort())
                        if (mainViewModel.listCheckBoxesViu[8].value) protectionCurrent(8, "> 100 мА")//12
                    if (value.toShort() and 0b10000000000000 != 0.toShort())
                        if (mainViewModel.listCheckBoxesViu[9].value) protectionCurrent(9, "> 100 мА")//13
//                    if (value.toShort() and 0b100000000000000 != 0.toShort()) protectionCurrent(index)
                }

                DevicePoller.startPoll(name, model.DI_01_16_RAW) { value ->
                    chopper = (value.toShort() and 0b1 != 0.toShort())
                    buttonPostStartPressed = (value.toShort() and 0b10 != 0.toShort())
                    if (buttonPostStartPressed && !chopper) {
                        cause = "Разомкнут рубильник во время испытания"
                    }
                    if (value.toShort() and 0b1000 != 0.toShort()) appendOneMessageToLog("4")
                }

                DevicePoller.startPoll(name, model.DI_17_32_TRIG_INV) { value ->
                    if (value.toShort() and 0b10 != 0.toShort()) {
                        mainViewModel.colorCurrent.value = Color.Red
                        cause = "Сработала защита: Токовая защита после АРН"
                    }
                }
                DevicePoller.startPoll(name, model.DI_17_32_RAW) { value ->
                    if (value.toShort() and 0b1 != 0.toShort()) {
                        mainViewModel.colorCurrent.value = Color.Green
                        currentDoArn = false
                    } else {
                        mainViewModel.colorCurrent.value = Color.Red
                        currentDoArn = true
                    }
                    if (value.toShort() and 0b100 != 0.toShort()) {
                        mainViewModel.colorSCO.value = Color.Green
                        DD2.offLightDoorSCO()
                    } else {
                        mainViewModel.colorSCO.value = Color.Red
                        cause = "Сработала защита: Концевик двери ШСО"
                        DD2.onLightDoorSCO()
                    }
                    if (value.toShort() and 0b1000 != 0.toShort()) {
                        mainViewModel.colorZone.value = Color.Green
                        DD2.offLightDoorZone()
                    } else {
                        mainViewModel.colorZone.value = Color.Red
                        cause = "Сработала защита: Концевик двери Зоны"
                        DD2.onLightDoorZone()
                    }
                }

            }
        }
        if (isTestRunning) DD2.onLightGround()
        if (isTestRunning) DD2.onDoorLockOper()
        if (isTestRunning) DD2.onDoorLockIsp()
        if (isTestRunning) DD2.onLightTablo()
        if (isTestRunning) DD2.onLight()
    }

    fun initDevices() {

        if (isTestRunning) {
            with(DD3) {
                checkResponsibility()
                if (!isResponding) {
                    cause = "$name не отвечает"
                }
            }
        }

        if (isTestRunning) {
            with(DD4) {
                checkResponsibility()
                if (!isResponding) {
                    cause = "$name не отвечает"
                }
            }
        }

        if (isTestRunning) {
            with(PV22) {
                needDevices.add(this)
                DevicePoller.startPoll(name, model.U_TRMS) { value ->
                    mainViewModel.measuredU.value = value.toDouble().af()
                    if (value.toDouble() > 440) cause = "Несоответствие коэффициента трансформации"
                }
            }
        }

        if (isTestRunning) {
            with(PV23) {
                needDevices.add(this)
                DevicePoller.startPoll(name, model.U_TRMS) { value ->
                    if (isViuRunning) {
                        mainViewModel.measuredUViu.value = (value.toDouble() * 1000).af()
                    }
                }
            }
        }

        if (isTestRunning) {
            listPA.forEachIndexed { index, aveM3 ->
                if (mainViewModel.listCheckBoxesViu[index].value) {
                    mainViewModel.listViu[index] = mainViewModel.listCheckBoxesViu[index].value
                    with(aveM3) {
                        needDevices.add(this)
                        DevicePoller.startPoll(name, model.U_TRMS) { value ->
                            if (listCurrentsState[index]) {
                                mainViewModel.listCurrents[index].value = value.toDouble().formatDigits(1)
                                var specifiedI = mainViewModel.specifiedI.value.toDouble()
                                var colorTF = (130 - 130 / specifiedI * value.toDouble()).toFloat()
                                if (colorTF < 0) colorTF = 0f
                                mainViewModel.listColorsCurrentTF[index].value = Color.hsv(colorTF, 0.7f, 1f)
                                if (!mainViewModel.listViu.any { it }) {
                                    cause = "Превышение по току по всем выбранным Постам"
                                }
                            }
                            if (value.toDouble() > mainViewModel.specifiedI.value.toDouble() && listCurrentsState[index]) {
                                protectionCurrent(index)
                            }
                        }
                    }
                }
            }
        }

        if (isTestRunning) {
            with(PA21) {
                needDevices.add(this)
                DevicePoller.startPoll(name, model.U_TRMS) { value ->
                    if ((value.toDouble() * 1000) > 5) {
                        mainViewModel.measuredI.value = (value.toDouble() * 1000).af()
                    } else {
                        mainViewModel.measuredI.value = "0.0"
                    }
                    if (value.toDouble() * 1000 > 1000) cause = "Превышение тока утечки"
                }
            }
        }

        if (isTestRunning) {
            with(ATR240) {
                needDevices.add(this)
                DevicePoller.startPoll(name, model.ENDS_STATUS_REGISTER) { value ->
                    if (value.toInt() == 2) {
//                        cause = "Нажат верхний концевик АРН1" //todo potom
                    }
                    if (value.toInt() == 3) {
                        cause = "Нажаты оба концевика АРН1"
                    }
                }
                DevicePoller.startPoll(name, model.U_RMS_REGISTER) { value ->
                    voltageLatr1 = value.toDouble()
                }
            }
        }
        if (isTestRunning) {
            with(ATR241) {
                needDevices.add(this)
                DevicePoller.startPoll(name, model.ENDS_STATUS_REGISTER) { value ->
                    if (value.toInt() == 2) {
//                        cause = "Нажат верхний концевик АРН2" //todo potom
                    }
                    if (value.toInt() == 3) {
                        cause = "Нажаты оба концевика АРН2"
                    }
                }
                DevicePoller.startPoll(name, model.U_RMS_REGISTER) { value ->
                    voltageLatr2 = value.toDouble()
                }
            }
        }
        if (isTestRunning) {
            thread {
                while (isTestRunning) {
                    needDevices.forEach {
                        if (isTestRunning) {
                            it.checkResponsibilityAndNotify()
                            if (!it.isResponding) {
                                cause = "Возможно нажат аварийной стоп"
                            }
                        }
                    }
                    wait(1)
                }
            }
        }
        if (isTestRunning) fields.forEach(Field::poll)
    }

    private fun protectionCurrent(index: Int, msg: String = "") {
        disassembleViu(index)
        mainViewModel.listColorsProtection[index].value = Color.Red
        mainViewModel.listViu[index] = false
        listCurrentsState[index] = false
        appendOneMessageToLog("Пост ${index + 1}: Превышение по току $msg")
    }

    fun start() {
        if (!isTestRunning && !checkTestSettings()) {
            thread {
                initTest()
                appendOneMessageToLog("Инициализация стенда")
                DD2.checkResponsibility()
                if (!DD2.isResponding) cause = "ПР102 не отвечает"
                mainViewModel.listColorsProtection.forEach {
                    it.value = Color(0xFF0071bb)
                }
                if (isTestRunning) DD3.offAll()
                if (isTestRunning) DD4.offAll()
                if (isTestRunning) appendOneMessageToLog("Инициализация ПР")
                if (isTestRunning) initDD2()

                thread(isDaemon = true) {
                    if (isTestRunning) DD2.onSound()
                    if (isTestRunning) sleep(6000)
                    if (isTestRunning) DD2.offSound()
                }

                if (isTestRunning && mainViewModel.allCheckBoxesMeger.value != ToggleableState.Off) {
                    meger()
                }

                if (isTestRunning && mainViewModel.allCheckBoxesViu.value != ToggleableState.Off) {
                    viu()
                }

                DD2.checkResponsibility()
                if (DD2.isResponding) DD2.offAllKMs()
                if (DD2.isResponding) DD3.offAll()
                if (DD2.isResponding) DD4.offAll()

                finalizeTest()
            }
        }
    }

    private fun meger() {
        isMgrRunning = true
        if (isTestRunning) appendOneMessageToLog("Начало испытания МГР")
        if (isTestRunning) appendOneMessageToLog("Сбор схемы")
        if (isTestRunning) DD2.onAVEM9()
        if (isTestRunning) sleep(100)
        if (isTestRunning) DD2.offAVEM9()
        if (isTestRunning) sleep(3000)
        if (isTestRunning) DD4.on(6) //ЗЕМЛЯ
        if (isTestRunning) DD2.offLightGround()
        if (isTestRunning) DD4.on(5) //Мегер
        if (isTestRunning) DD2.onLightMeger()
        if (PR65.isResponding) PR65.stopTest()
        if (isTestRunning) {
            with(PR65) {
                needDevices.add(this)
                DevicePoller.startPoll(name, model.STATUS) { value ->
                    statusMGR = value.toInt()
                }
                DevicePoller.startPoll(name, model.R15_MEAS) { value ->
                    r15 = value.toDouble()
                }
                DevicePoller.startPoll(name, model.VOLTAGE) { value ->
                    if (isMgrRunning) {
                        mainViewModel.measuredUViu.value = value.toDouble().af()
                    }
                }
            }
        }
        mainViewModel.listCheckBoxesMeger.forEachIndexed { index, state ->
            if (state.value) {
                if (isTestRunning) appendOneMessageToLog("Испытание МГР Пост ${index + 1}")
                if (isTestRunning) DD2.onKM2BP()
                if (isTestRunning) assembleMgr(index)
                if (isTestRunning) mainViewModel.listColorsProtection[index].value = Color.Green
                if (isTestRunning) sleep(500)
                if (isTestRunning) DD2.offKM2BP()

                if (isTestRunning && mainViewModel.allCheckBoxesMeger.value != ToggleableState.Off) {
                    with(PR65) {
                        startMeasurement(
                            AVEM9Model.MeasurementMode.Resistance, when {
                                mainViewModel.specifiedUMeger.value.toInt() == 2500 -> {
                                    AVEM9Model.SpecifiedVoltage.V2500
                                }

                                mainViewModel.specifiedUMeger.value.toInt() == 1000 -> {
                                    AVEM9Model.SpecifiedVoltage.V1000
                                }

                                mainViewModel.specifiedUMeger.value.toInt() == 500 -> {
                                    AVEM9Model.SpecifiedVoltage.V500
                                }

                                else -> {
                                    AVEM9Model.SpecifiedVoltage.V500
                                }
                            }
                        )
                    }
                }

                if (isTestRunning) sleep(1000)
                if (isTestRunning) mainViewModel.listRs[index].value = "Измерение"
                thread(isDaemon = true) {
                    var color = 0f
                    var top = false
                    var bot = true
                    while (statusMGR != 4 && isTestRunning) {
                        if (bot) {
                            color += 0.1f
                            if (color > 129.0f) {
                                bot = false
                                top = true
                            }
                        }
                        if (top) {
                            color -= 0.1f
                            if (color < 0.2f) {
                                bot = true
                                top = false
                            }
                        }
                        if (isTestRunning) mainViewModel.listColorsRsTF[index].value = Color.hsv(color, 0.7f, 1f)
                        sleep(1)
                    }
                }

                if (isTestRunning) sleep(3000)
                var time = 30
                while (isTestRunning && statusMGR != 4 && time-- > 0) {
                    sleep(1000)
                }
                if (PR65.isResponding) PR65.stopTest()
                if (isTestRunning) disassembleMgr(index)
                if (isTestRunning) mainViewModel.listRs[index].value =
                    if (r15 == 200000.0) ">200000" else if (r15 < 0.3) "<0.2" else r15.af()
                if (r15 < 0.3) {
                    if (isTestRunning) mainViewModel.listColorsProtection[index].value = Color.Red
                    if (isTestRunning) mainViewModel.listCheckBoxesViu[index].value = false
                    if (isTestRunning) mainViewModel.listViu[index] = false
                    if (isTestRunning && !mainViewModel.listCheckBoxesViu.any { it.value }) {
                        mainViewModel.allCheckBoxesViu.value = ToggleableState.Off
                    }
                }
                var colorTF = (130 / mainViewModel.specifiedRMeger.value.toDouble() * r15).toFloat()
                if (isTestRunning) if (colorTF > 130f) colorTF = 130f
                if (isTestRunning) mainViewModel.listColorsRsTF[index].value = Color.hsv(colorTF, 0.7f, 1f)
            }
            if (!isTestRunning) mainViewModel.listRs[index].value = ""
            if (!isTestRunning) mainViewModel.listColorsRsTF[index].value = Color.Transparent
        }
        if (isTestRunning) DD4.off(5) //Мегер
        if (isTestRunning) DD2.offLightMeger()
        isMgrRunning = false
    }

    private fun viu() {
        isViuRunning = true
        if (isTestRunning) appendOneMessageToLog("Начало испытания ВИУ")
        if (isTestRunning) appendOneMessageToLog("Сбор схемы")
        if (isTestRunning) DD2.onKM2BP()
        if (isTestRunning) sleep(3000)
        mainViewModel.listCheckBoxesViu.forEachIndexed { index, state ->
            if (mainViewModel.listColorsProtection[index].value != Color.Red) {
                if (isTestRunning) listCurrentsState[index] = state.value
                if (isTestRunning && state.value) assembleViu(index)
                if (isTestRunning && state.value) mainViewModel.listColorsProtection[index].value = Color.Green
            }
        }
        if (isTestRunning) sleep(3000)
        if (isTestRunning) DD2.offKM2BP()

        mainViewModel.listCheckBoxesViu.forEachIndexed { index, state ->
            if (mainViewModel.listColorsProtection[index].value != Color.Red) {
                if (isTestRunning && state.value) setAVEMConfiguration(index)
            }
        }

        if (isTestRunning) appendOneMessageToLog("Инициализация устройств")
        if (isTestRunning) initDevices()

        if (isTestRunning) appendOneMessageToLog("Взведите рубильник")
        if (isTestRunning) mainViewModel.titleDialog.value = "Внимание"
        if (isTestRunning) mainViewModel.textDialog.value = "Взведите рубильник"
        if (isTestRunning) mainViewModel.dialogVisibleState.value = true
        var timer = System.currentTimeMillis() + 5 * 60 * 1000
        while (!chopper && isTestRunning) {
            if (System.currentTimeMillis() > timer) {
                cause = "Не взведен рубильник"
            }
            sleep(10)
        }
        if (isTestRunning) mainViewModel.dialogVisibleState.value = false

        if (isTestRunning) DD2.onAllowStart()
        if (isTestRunning) appendOneMessageToLog("Нажмите кнопку ПУСК")
        if (isTestRunning) mainViewModel.titleDialog.value = "Внимание"
        if (isTestRunning) mainViewModel.textDialog.value = "Нажмите кнопку ПУСК"
        if (isTestRunning) mainViewModel.dialogVisibleState.value = true
        timer = System.currentTimeMillis() + 5 * 60 * 1000
        while (!buttonPostStartPressed && isTestRunning) {
            if (System.currentTimeMillis() > timer) {
                cause = "Не нажата кнопка ПУСК"
            }
            sleep(10)
        }
        if (isTestRunning) mainViewModel.dialogVisibleState.value = false


        while (currentDoArn && isTestRunning) {
            if (isTestRunning) appendOneMessageToLog("Повторно нажмите кнопку ПУСК")
            if (isTestRunning) mainViewModel.titleDialog.value = "Внимание"
            if (isTestRunning) mainViewModel.textDialog.value = "Повторно нажмите кнопку ПУСК"
            if (isTestRunning) mainViewModel.dialogVisibleState.value = true
            timer = System.currentTimeMillis() + 5 * 60 * 1000
            while (!buttonPostStartPressed && isTestRunning) {
                if (System.currentTimeMillis() > timer) {
                    cause = "Не нажата кнопка ПУСК"
                }
                sleep(10)
            }
            if (isTestRunning) mainViewModel.dialogVisibleState.value = false
            if (isTestRunning) sleep(3000)
        }

        if (isTestRunning) DD2.onLightViu()
        if (isTestRunning) DD4.on(7) //ВИУ

        if (isTestRunning) DD4.on(6) //ЗЕМЛЯ
        if (isTestRunning) DD2.offLightGround()

        if (isTestRunning) DD2.onLightViu()
        if (isTestRunning) appendOneMessageToLog("Ожидание поднятия напряжения")
        val timerLatrs = System.currentTimeMillis()
        if (isTestRunning) ATR240.resetLATR()
        if (isTestRunning) ATR241.resetLATR()

        if (isTestRunning) ATR240.startUpLATRPulseInit(6f, 20f)
        if (isTestRunning) ATR241.startUpLATRPulseInit(6f, 20f)

        while ((voltageLatr1 < 5.0 || voltageLatr2 < 5.0) && isTestRunning) {
            if (System.currentTimeMillis() - timerLatrs > 30000) cause = "Застревание АРН"
            sleep(100)
        }

        if (isTestRunning) sleep(3000)

        appendOneMessageToLog("Выставление заданного напряжения")

        repeat(3) {
            if (isTestRunning) voltageRegulation(mainViewModel.specifiedUViu.value.toDouble())
            if (isTestRunning) sleep(1000)
        }

        if (isTestRunning) appendOneMessageToLog("Выдержка напряжения")
        val startTime = System.currentTimeMillis()
        val time = mainViewModel.specifiedTime.value.toDouble() * 1000 + startTime
        while (isTestRunning && System.currentTimeMillis() < (time)) {
            mainViewModel.measuredTime.value =
                "%.1f".format(Locale.ENGLISH, abs(time - System.currentTimeMillis()) / 1000)
            sleep(100)
        }
        mainViewModel.measuredTime.value = "%.1f".format(Locale.ENGLISH, 0.0)

        mainViewModel.storedUViu = mainViewModel.measuredUViu.value

        for (listCurrent in listCurrentsState.indices) {
            listCurrentsState[listCurrent] = false
        }

        appendOneMessageToLog("Снижения напряжения")
        ATR240.resetLATR()
        ATR241.resetLATR()

        timer = System.currentTimeMillis() + 30000
        while (mainViewModel.measuredUViu.value.toDouble() > 500) {
            if (System.currentTimeMillis() > timer) {
                cause = "Превышено время ожидания"
                break
            }
            sleep(10)
        }
        isViuRunning = false
    }

    private fun setAVEMConfiguration(index: Int) {
        val avem = listPA[index]
        val registerPGA = avem.getRegisterById(avem.model.PGA_MODE)
        val registerSHUNT = avem.getRegisterById(avem.model.SHUNT_MODE)
        avem.toggleProgrammingMode()
        avem.readRegister(registerPGA)
        if (registerPGA.value.toShort() != 0.toShort()) {
            avem.writeRegister(registerPGA, 0.toShort())
        }
        avem.readRegister(registerSHUNT)
        if (registerSHUNT.value.toFloat() != 10f) {
            avem.writeRegister(registerSHUNT, 10f)
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
        needDevices.clear()
        listMessagesLog.clear()
        mainViewModel.storedUViu = ""
        mainViewModel.measuredI.value = ""
        mainViewModel.measuredUViu.value = ""
        mainViewModel.measuredU.value = ""
        mainViewModel.measuredTime.value = ""
        mainViewModel.listColorsCurrentTF.forEach {
            it.value = Color.Transparent
        }
        mainViewModel.listColorsRsTF.forEach {
            it.value = Color.Transparent
        }
        for (listCurrent in listCurrentsState.indices) {
            listCurrentsState[listCurrent] = false
        }
        mainViewModel.listCurrents.forEach {
            it.value = ""
        }
        mainViewModel.listRs.forEach {
            it.value = ""
        }

        mainViewModel.listColorsProtection.forEach {
            it.value = Color(0xFF0071bb)
        }
        cause = ""
        isTestRunning = true
        mainViewModel.mutableStateIsRunning.value = false
        mainViewModel.isTestRunningState.value = true
    }

    private fun finalizeTest() {
        isTestRunning = false
        if (PR65.isResponding) PR65.reset()
        DevicePoller.clearPollingRegisters()
        DevicePoller.clearWritingRegisters()
        mainViewModel.mutableStateIsRunning.value = true
        mainViewModel.dialogVisibleState.value = false
        if (cause.isNotEmpty()) {
            mainViewModel.titleDialog.value = "Ошибка"
            mainViewModel.textDialog.value = cause
            mainViewModel.dialogVisibleState.value = true
        }
        if (isTestRunning) appendOneMessageToLog("Испытание завершено")
        transaction {
            repeat(10) {
                if (mainViewModel.listColorsProtection[it].value != Color(0xFF0071bb)) {
                    Protocol.new {
                        serial = mainViewModel.listSerialNumbers[it].value
                        operator = "Тестовый оператор"
                        itemName = mainViewModel.selectedObject.value!!.name
                        pointsName = mainViewModel.selectedField.value!!.nameTest
                        uViu = mainViewModel.storedUViu
                        iViu = mainViewModel.listCurrents[it].value
                        uMgr = mainViewModel.specifiedUMeger.value
                        rMgr = mainViewModel.listRs[it].value
                        date =  SimpleDateFormat("dd.MM.y").format(System.currentTimeMillis()).toString()
                        time = SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()).toString()
                        result = when (mainViewModel.listColorsProtection[it].value) {
                            Color.Red -> "Неуспешно"
                            Color.Green -> "Успешно"
                            else -> "Неизвестно"
                        }
                    }

                }
            }
        }
        mainViewModel.listColorsProtection.forEach {
            if (it.value == Color.Green) it.value = Color(0xFF0071bb)
        }
        mainViewModel.isTestRunningState.value = false
        mainViewModel.measuredUViu.value = ""
        mainViewModel.measuredTime.value = ""
        mainViewModel.measuredI.value = ""
        mainViewModel.measuredU.value = ""

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

    fun assembleMgr(index: Int) {
        when (index) {
            0 -> {
                DD3.on(3)
                DD4.on(8)
            }

            1 -> {
                DD3.on(4)
                DD4.on(9)
            }

            2 -> {
                DD3.on(5)
                DD4.on(10)
            }

            3 -> {
                DD3.on(6)
                DD4.on(11)
            }

            4 -> {
                DD3.on(7)
                DD4.on(12)
            }

            5 -> {
                DD3.on(8)
                DD4.on(13)
            }

            6 -> {
                DD4.on(1)
                DD4.on(14)
            }

            7 -> {
                DD4.on(2)
                DD4.on(15)
            }

            8 -> {
                DD4.on(3)
                DD4.on(16)
            }

            9 -> {
                DD4.on(4)
                DD2.onLightPost10()
            }
        }
    }

    fun disassembleMgr(index: Int) {
        when (index) {
            0 -> {
                DD3.off(3)
                DD4.off(8)
            }

            1 -> {
                DD3.off(4)
                DD4.off(9)
            }

            2 -> {
                DD3.off(5)
                DD4.off(10)
            }

            3 -> {
                DD3.off(6)
                DD4.off(11)
            }

            4 -> {
                DD3.off(7)
                DD4.off(12)
            }

            5 -> {
                DD3.off(8)
                DD4.off(13)
            }

            6 -> {
                DD4.off(1)
                DD4.off(14)
            }

            7 -> {
                DD4.off(2)
                DD4.off(15)
            }

            8 -> {
                DD4.off(3)
                DD4.off(16)
            }

            9 -> {
                DD4.off(4)
                DD2.offLightPost10()
            }
        }
    }

    fun assembleViu(index: Int) {
        when (index) {
            0 -> {
                DD3.on(9)
                DD4.on(8)
            }

            1 -> {
                DD3.on(10)
                DD4.on(9)
            }

            2 -> {
                DD3.on(11)
                DD4.on(10)
            }

            3 -> {
                DD3.on(12)
                DD4.on(11)
            }

            4 -> {
                DD3.on(13)
                DD4.on(12)
            }

            5 -> {
                DD3.on(14)
                DD4.on(13)
            }

            6 -> {
                DD3.on(15)
                DD4.on(14)
            }

            7 -> {
                DD3.on(16)
                DD4.on(15)
            }

            8 -> {
                DD3.on(1)
                DD4.on(16)
            }

            9 -> {
                DD3.on(2)
                DD2.onLightPost10()
            }
        }
    }

    fun disassembleViu(index: Int) {
        when (index) {
            0 -> {
                DD3.off(9)
                DD4.off(8)
            }

            1 -> {
                DD3.off(10)
                DD4.off(9)
            }

            2 -> {
                DD3.off(11)
                DD4.off(10)
            }

            3 -> {
                DD3.off(12)
                DD4.off(11)
            }

            4 -> {
                DD3.off(13)
                DD4.off(12)
            }

            5 -> {
                DD3.off(14)
                DD4.off(13)
            }

            6 -> {
                DD3.off(15)
                DD4.off(14)
            }

            7 -> {
                DD3.off(16)
                DD4.off(15)
            }

            8 -> {
                DD3.off(1)
                DD4.off(16)
            }

            9 -> {
                DD3.off(2)
                DD2.offLightPost10()
            }
        }
    }

    private fun voltageRegulation(voltage: Double) {
        var deviation = if (voltage > 2000) 50 else 20
        var timer = 0L
        val single = 1500.0
        val both = 5000.0
        var speedPerc = 35f
        var timePulsePerc = 20f
        val coefLatr2 = 1.0f
        val up = 300f
        val down = 1f
        var direction: Float
        val speedFastUp = 18f
        val speedFastDown = 24f
        val speedSlowUp = 8f
        val speedSlowDown = 10f
        timer = System.currentTimeMillis()
        while (abs(mainViewModel.measuredUViu.value.toDouble() - voltage) > both && isTestRunning) {
            if (mainViewModel.measuredUViu.value.toDouble() < voltage) {
                direction = up
                speedPerc = speedFastUp
            } else {
                direction = down
                speedPerc = speedFastDown
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "Превышено время регулирования"
            ATR240.startUpLATRPulse(direction, speedPerc)
            ATR241.startUpLATRPulse(direction, speedPerc * coefLatr2)
            if (isTestRunning) sleep(500)
        }
        ATR240.stopLATR()
        ATR241.stopLATR()
        timer = System.currentTimeMillis()
        while (abs(mainViewModel.measuredUViu.value.toDouble() - voltage) > single && isTestRunning) {
            if (mainViewModel.measuredUViu.value.toDouble() < voltage) {
                direction = up
                timePulsePerc = speedSlowUp
            } else {
                direction = down
                timePulsePerc = speedSlowDown
            }
            if (System.currentTimeMillis() - timer > 60000) cause = "Превышено время регулирования"
            ATR240.startUpLATRPulse(direction, timePulsePerc)
            ATR241.startUpLATRPulse(direction, timePulsePerc * coefLatr2)
            if (isTestRunning) sleep(500)
        }
        ATR240.stopLATR()
        ATR241.stopLATR()
        timer = System.currentTimeMillis()
        while (abs(mainViewModel.measuredUViu.value.toDouble() - voltage) > deviation && isTestRunning) {
            if (mainViewModel.measuredUViu.value.toDouble() < voltage) {
                direction = up
                timePulsePerc = speedSlowUp
            } else {
                direction = down
                timePulsePerc = speedSlowDown
            }
            if (abs(mainViewModel.measuredUViu.value.toDouble() - voltage) < single) {
                if (direction == up) {
                    if (voltageLatr1 < voltageLatr2) {
                        ATR240.startUpLATRPulse(direction, timePulsePerc)
                        ATR241.stopLATR()
                    } else {
                        ATR241.startUpLATRPulse(direction, timePulsePerc * coefLatr2)
                        ATR240.stopLATR()
                    }
                } else {
                    if (voltageLatr1 > voltageLatr2) {
                        ATR240.startUpLATRPulse(direction, timePulsePerc)
                        ATR241.stopLATR()
                    } else {
                        ATR241.startUpLATRPulse(direction, timePulsePerc * coefLatr2)
                        ATR240.stopLATR()
                    }
                }
            }
            if (isTestRunning) sleep(500)
        }
        if (System.currentTimeMillis() - timer > 60000) cause = "Превышено время регулирования"
        ATR240.stopLATR()
        ATR241.stopLATR()
    }

    fun appendMessageToLog(msg: String) {
        listMessagesLog.add(msg)
        mainViewModel.logMessages.add("${SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())} | $msg")
        scope.launch {
            mainViewModel.logScrollState.scrollToItem(mainViewModel.logMessages.lastIndex)
        }
    }

    fun appendOneMessageToLog(msg: String) {
        if (listMessagesLog.lastOrNull() != null) {
            if (!listMessagesLog.any { it == msg }) {
                appendMessageToLog(msg)
            }
        } else {
            appendMessageToLog(msg)
        }
    }
}