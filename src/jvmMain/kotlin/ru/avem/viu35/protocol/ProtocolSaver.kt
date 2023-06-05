package ru.avem.viu35.protocol

import androidx.compose.ui.res.useResource
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ru.avem.viu35.copyFileFromStream
import ru.avem.viu35.database.entities.Protocol
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

fun saveProtocolAsWorkbook(listProtocols: List<Protocol>, path: String = "lastOpened.xlsx") {

    val template = File(path)

    if (File("protocol.xlsx").exists()) {
        copyFileFromStream(File("protocol.xlsx").inputStream(), template)
    } else {
        useResource("protocol.xlsx") {
            copyFileFromStream(it, File("protocol.xlsx"))
        }
        copyFileFromStream(File("protocol.xlsx").inputStream(), template)
    }

    try {
        XSSFWorkbook(template).use { wb ->
            val sheet = wb.getSheetAt(0)
            for (iRow in 0 until 150) {
                val row = sheet.getRow(iRow)
                if (row != null) {
                    for (iCell in 0 until 150) {
                        val cell = row.getCell(iCell)
                        if (cell != null && (cell.cellType == CellType.STRING)) {
                            when (cell.stringCellValue) {
                                "#number#" -> cell.setCellValue(listProtocols.last().id.toString())
                                "#serial#" -> cell.setCellValue(listProtocols.last().serial)
                                "#operator#" -> cell.setCellValue(listProtocols.last().operator)
                                "#itemName#" -> cell.setCellValue(listProtocols.last().itemName)
                                "#pointsName#" -> cell.setCellValue(listProtocols.last().pointsName)
                                "#uViu#" -> cell.setCellValue(listProtocols.last().uViu)
                                "#iViu#" -> cell.setCellValue(listProtocols.last().iViu)
                                "#uMgr#" -> cell.setCellValue(listProtocols.last().uMgr)
                                "#rMgr#" -> cell.setCellValue(listProtocols.last().rMgr)
                                "#result#" -> cell.setCellValue(listProtocols.last().result)
                                "#date#" -> cell.setCellValue(listProtocols.last().date)
                                "#time#" -> cell.setCellValue(listProtocols.last().time)
                                else -> {
                                    if (cell.stringCellValue.contains("#")) {
                                        cell.setCellValue("")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            val outStream = ByteArrayOutputStream()
            wb.write(outStream)
            outStream.close()
        }
    } catch (e: FileNotFoundException) {
    }
}
