package ru.avem.viu35.tests

import androidx.compose.runtime.MutableState
import ru.avem.library.polling.IDeviceController
import ru.avem.viu35.af
import ru.avem.viu35.io.DevicePoller
import ru.avem.viu35.limit
import kotlin.math.abs

class Field(
    val init: Number = Double.NaN,
    var k: Number = 1.0,
    var b: Number = 0.0,
    var kS: Number = 1.0,
    var bS: Number = 0.0,
    var min: Number = -Double.MAX_VALUE,
    var max: Number = Double.MAX_VALUE,
    var abs: Boolean = false,
    private var onChangeInCreating: Boolean = false,
    val id: String = "",
    val idSlot: Int = 0,
    var onChange: (Field) -> Unit = {}
) {
    private var isCreated = false

    private val bindings = mutableListOf<MutableState<String>>()

    private var rawValue: Number = Double.NaN
    var value: Number = Double.NaN
        set(value) {
            rawValue = value
            field = limit(min, transform(value), max)

            try {
                if (isCreated || onChangeInCreating) onChange(this)
            } catch (_: NullPointerException) {
            } catch (e: Exception) {
                e.printStackTrace()
            }
            bindings.forEach { it.value = s }
        }

    private fun transform(value: Number) = value.toDouble().let {
        val withFactor = it * k.toDouble() + b.toDouble()
        if (abs) abs(withFactor) else withFactor
    }

    var storedValue: String = ""
        private set

    val d: Double
        get() = value.toDouble()
    val i: Int
        get() = value.toInt()
    val s: String
        get() = stringValue ?: (value.toDouble() * kS.toDouble() + bS.toDouble()).af()
    val sRaw: String
        get() = stringValue ?: (rawValue.toDouble() * kS.toDouble() + bS.toDouble()).af()

    var stringValue: String? = null
        set(value) {
            field = value

            try {
                if (isCreated || onChangeInCreating) onChange(this)
            } catch (_: NullPointerException) {
            } catch (e: Exception) {
                e.printStackTrace()
            }
            bindings.forEach { it.value = s }
        }

    init {
        reinit()
        isCreated = true
    }

    fun reinit() {
        value = init
        storedValue = ""
        stringValue = null
    }

    fun store() {
        storedValue = sRaw
    }

    fun restore() {
        value = storedValue.toDoubleOrNull() ?: Double.NaN
    }

    private var deviceToRegister: Pair<IDeviceController, String>? = null
    infix fun pollBy(pair: Pair<IDeviceController, String>): Field {
        deviceToRegister = pair
        return this
    }

    fun poll() {
        deviceToRegister?.let {
            DevicePoller.startPoll(it.first.name, it.second) { value = it.toDouble() }
        }
    }

    infix fun bindTo(state: MutableState<String>): Field {
        bindings += state
        return this
    }

    override fun toString() = s
}
