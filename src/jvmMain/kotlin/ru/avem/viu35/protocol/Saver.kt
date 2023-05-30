//package ru.avem.viu35.protocol
//
//import org.apache.poi.ss.usermodel.CellType
//import org.apache.poi.xssf.usermodel.XSSFWorkbook
//import org.apache.poi.xwpf.usermodel.XWPFDocument
//import org.apache.poi.xwpf.usermodel.XWPFTable
//import ru.avem.viu35.af
//import ru.avem.viu35.database.entities.Protocol
//import ru.avem.viu35.database.entities.ProtocolField
//import java.io.*
//import java.nio.file.Files
//import java.nio.file.Path
//import java.nio.file.Paths
//import java.text.SimpleDateFormat
//
//const val metavariableParts = "\${}"
//
//fun saveProtocolsAsWorkbook(protocols: List<Protocol>, reportPathString: String, targetPath: File? = null): Path {
//    val mills = System.currentTimeMillis()
//    val reportDir =
//        Files.createDirectories(Paths.get("report/${SimpleDateFormat("yyyyMMdd").format(mills)}"))
//    val resultFile = targetPath ?: Paths.get(reportDir.toString(), "$mills.xlsx").toFile()
//    val templateStream = Protocol::class.java.getResourceAsStream("/templates/${reportPathString}")
//    copyFileFromStream(templateStream, resultFile)
//
//    val fields = protocols.map(Protocol::filledFields).flatten()
//
//    XSSFWorkbook(resultFile).use { workBook ->
//        val sheet = workBook.getSheetAt(0)
//        sheet.rowIterator().forEach { row ->
//            row.cellIterator().forEach { cell ->
//                if (cell != null && (cell.cellType == CellType.STRING)) {
//                    val values = fields.filter { cell.stringCellValue.contains(it.key) }
//                    values.forEach {
//                        val fieldValue =
//                            if (it.key == "\${SERIAL_NUMBER}") it.value else { // TODO убрать хард-код, добавить типы сохраняемых ключей
//                                it.value.toDoubleOrNull()?.af()?.replace('.', ',') ?: it.value
//                            }
//                        val value = cell.stringCellValue.replace(it.key, fieldValue)
//                        cell.setCellValue(value.clearMetavariablePart())
//                    }
//
//                    if (values.isEmpty() && cell.stringCellValue.containsAll(metavariableParts)) {
//                        cell.setCellValue(cell.stringCellValue.clearMetavariable())
//                    }
//                }
//            }
//        }
//        val outStream = ByteArrayOutputStream()
//        workBook.write(outStream)
//        outStream.close()
//    }
//
//    return Paths.get(resultFile.absolutePath)
//}
//
//fun String.containsAll(other: String) =
//    with(other.toHashSet()) { this@containsAll.forEach { this -= it }; this }.isEmpty()
//
//fun saveProtocolsAsDocument(reports: List<Protocol>, reportPathString: String, targetPath: File? = null): Path {
//    val mills = System.currentTimeMillis()
//    val reportDir =
//        Files.createDirectories(Paths.get("report/${SimpleDateFormat("yyyyMMdd").format(mills)}"))
//    val resultFile = targetPath ?: Paths.get(reportDir.toString(), "$mills.docx").toFile()
//    resultFile.createNewFile()
//    val templateStream = Protocol::class.java.getResourceAsStream("/templates/${reportPathString}")
//    copyFileFromStream(templateStream, resultFile)
//
//    val fields = reports.map(Protocol::filledFields).flatten()
//
//    XWPFDocument(FileInputStream(resultFile)).use { document ->
//        document.paragraphs.forEach { p ->
//            p.runs.forEach { run ->
//                if (run.getText(0) != null) {
//                    val values = fields.filter { run.getText(0).contains(it.key) }
//                    values.forEach {
//                        val fieldValue = if (it.key == "\${SERIAL_NUMBER}") it.value else {
//                            it.value.toDoubleOrNull()?.af()?.replace('.', ',') ?: it.value
//                        }
//                        val value = run.getText(0).replace(it.key, fieldValue)
//                        run.setText(value, 0)
//                    }
//
//                    if (values.isEmpty() && run.getText(0).containsAll(metavariableParts)) {
//                        run.setText(run.getText(0).clearMetavariable(), 0)
//                    }
//                }
//            }
//        }
//
//        document.tables.fillTable(fields)
//
//        val outStream = FileOutputStream(resultFile)
//        document.write(outStream)
//        outStream.close()
//    }
//
//    return Paths.get(resultFile.absolutePath)
//}
//
//private fun List<XWPFTable>.fillTable(fields: List<ProtocolField>) {
//    forEach { table ->
//        table.rows.forEach { row ->
//            row.tableCells.forEach { cell ->
//                if (cell.tables.size != 0) {
//                    cell.tables.fillTable(fields)
//                }
//                cell.paragraphs.forEach { p ->
//                    p.runs.forEach { run ->
//                        if (run.getText(0) != null) {
//                            val values = fields.filter { run.getText(0).contains(it.key) }
//                            values.forEach {
//                                val fieldValue = if (it.key == "\${SERIAL_NUMBER}") it.value else {
//                                    it.value.toDoubleOrNull()?.af()?.replace('.', ',') ?: it.value
//                                }
//                                val value = run.getText(0).replace(it.key, fieldValue)
//                                run.setText(value, 0)
//                            }
//
//                            if (values.isEmpty() && run.getText(0).containsAll(metavariableParts)) {
//                                run.setText(run.getText(0).clearMetavariable(), 0)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//fun saveProtocolAsWorkbook(report: Protocol, targetPath: File? = null): Path {
//    val reportDir =
//        Files.createDirectories(Paths.get("/report/${SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis())}"))
//    val resultFile = targetPath ?: Paths.get(reportDir.toString(), "${System.currentTimeMillis()}.xlsx").toFile()
//    val templateStream = Protocol::class.java.getResourceAsStream("/templates/${report.template}")
//    copyFileFromStream(templateStream, resultFile)
//
//    val fields = report.filledFields
//
//    XSSFWorkbook(resultFile).use { workBook ->
//        val sheet = workBook.getSheetAt(0)
//        sheet.rowIterator().forEach { row ->
//            row.cellIterator().forEach { cell ->
//                if (cell != null && (cell.cellType == CellType.STRING)) {
//                    fields.find { cell.stringCellValue.contains(it.key) }?.let {
//                        val fieldValue = if (it.key == "\${SERIAL_NUMBER}") it.value else {
//                            it.value.toDoubleOrNull()?.af()?.replace('.', ',') ?: it.value
//                        }
//                        val value = cell.stringCellValue.replace(it.key, fieldValue)
//                        cell.setCellValue(value)
//                    } ?: if (cell.stringCellValue.containsAll(metavariableParts)) {
//                        cell.setCellValue(cell.stringCellValue.clearMetavariable())
//                    } else {
//
//                    }
//                }
//            }
//        }
//        val outStream = ByteArrayOutputStream()
//        workBook.write(outStream)
//        outStream.close()
//    }
//
//    return Paths.get(resultFile.absolutePath)
//}
//
//private fun copyFileFromStream(_inputStream: InputStream, dest: File) {
//    _inputStream.use { inputStream ->
//        val fileOutputStream = FileOutputStream(dest)
//        val buffer = ByteArray(1024)
//        var length = inputStream.read(buffer)
//        while (length > 0) {
//            fileOutputStream.write(buffer, 0, length)
//            length = inputStream.read(buffer)
//        }
//    }
//}
//
//fun String.clearMetavariablePart() = buildString {
//    this@clearMetavariablePart.forEach {
//        if (it !in metavariableParts) append(it)
//    }
//}
//
//fun String.clearMetavariable() = buildString {
//    var isInsideMetavariable = false
//    this@clearMetavariable.forEach {
//        if (it == '$') {
//            isInsideMetavariable = true
//        }
//
//        if (!isInsideMetavariable) append(it)
//
//        if (it == '}') {
//            isInsideMetavariable = false
//        }
//    }
//}
