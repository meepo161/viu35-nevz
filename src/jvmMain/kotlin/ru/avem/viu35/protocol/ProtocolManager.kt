package ru.avem.viu35.protocol

import mu.KotlinLogging
import ru.avem.viu35.database.DBManager
import ru.avem.viu35.database.entities.Protocol
import java.awt.Desktop
import java.io.File

object ProtocolManager {
    val all: List<Protocol>
        get() = DBManager.getAllProtocols()

    var selectedProtocol: Protocol? = null

    fun save(): File? {
        if (selectedProtocol == null) errorNotification("Выберите протокол для сохранения")
        val testTitlesToReports = all.groupBy(Protocol::id)

        val testToReports = mutableMapOf<String, Protocol>()

        testTitlesToReports.forEach { (testTitle, all) ->
            testToReports[testTitle.toString()] = all.maxByOrNull(Protocol::id)!!
        }
        val needReports = testToReports.values.toList()

        try {
            val chooseFiles = listOf(File("temp.xlsx"))
//                chooseFile( // todo FIXME
//                    title = "Сохранить как...",
//                    filters = arrayOf(FileChooser.ExtensionFilter("AVEM Protocol (*.xlsx)", "*.xlsx")),
//                    mode = FileChooserMode.Save,
//                    owner = view.currentWindow
//                )
            if (chooseFiles.isNotEmpty()) {
                try {
                    val path = saveProtocolsAsWorkbook(
                        needReports,
                        "protocol_template.xlsx",
                        chooseFiles.first()
                    )
                    infoNotification(
                        title = "Инфо",
                        text = "Протокол сохранён по пути $path"
                    )
                    return path.toFile()
                } catch (e: Exception) {
                    e.printStackTrace()
                    errorNotification(
                        "Не удалось сохранить протокол",
                        "Ошибка файловой системы или шаблон отсутствует"
                    )
                }
            }
        } catch (e: Exception) {
            errorNotification(
                title = "Не удалось сохранить протокол",
                text = "Причина: $e"
            )
        }

        return null
    }

    fun open(protocol: File?) {
        protocol?.let {
            Desktop.getDesktop().open(it)
        } ?: errorNotification(
            "Не удалось открыть протокол"
        )
    }

    private fun errorNotification(title: String = "", text: String = "") {
        KotlinLogging.logger("TAG").info("error: $title $text") //todo FIXME
    }

    private fun infoNotification(title: String = "", text: String = "") {
        KotlinLogging.logger("TAG").info("info: $title $text") //todo FIXME
    }
}
