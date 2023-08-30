package ru.avem.viu35

import java.awt.Desktop
import java.io.*
import java.util.*
import kotlin.math.abs

val sp = File.separatorChar

val file = File("conf.txt")

fun Number.af() = with(abs(String.format(Locale.US, "%.4f", toDouble()).toDouble())) {
    val format = when {
        this.isNaN() -> return ""
        this < 100.0 -> "%.2f"
        this < 1000.0 -> "%.1f"
        else -> "%.0f"
    }
    String.format(Locale.US, format, this@af.toDouble())
}

fun ms() = System.currentTimeMillis()

fun <T : Number> limit(min: T, value: T, max: T): T = when {
    value.toDouble() < min.toDouble() -> min
    value.toDouble() > max.toDouble() -> max
    else -> value
}

fun openFile(file: File) {
    try {
        Desktop.getDesktop().open(file)
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
fun String.num() = toDoubleOrNull() ?: 0.0

fun <C : S, S> C.addTo(list: MutableList<S>): C {
    list.add(this)
    return this
}

fun leastSquaresFit(xs: DoubleArray, ys: DoubleArray, n: Int = xs.size): Pair<Double, Double> {
    val d = n.toDouble() * xs.sumOf { it * it } - xs.sum() * xs.sum()

    val da = n.toDouble() * xs.zip(ys).sumOf { it.first * it.second } - xs.sum() * ys.sum()
    val db = xs.sumOf { it * it } * ys.sum() - xs.sum() * xs.zip(ys).sumOf { it.first * it.second }

    return da / d to db / d
}

fun <T> List<T>.second(): T {
    if (isEmpty() && size < 2) {
        throw NoSuchElementException("List invalid size.")
    }
    return this[1]
}

fun copyFileFromStream(inputStream: InputStream, dest: File) {
    inputStream.use { inputStream ->
        try {
            val fileOutputStream = FileOutputStream(dest)
            val buffer = ByteArray(1024)
            var length = inputStream.read(buffer)
            while (length > 0) {
                fileOutputStream.write(buffer, 0, length)
                length = inputStream.read(buffer)
            }
        } catch (_: FileNotFoundException) {
        }
    }
}
