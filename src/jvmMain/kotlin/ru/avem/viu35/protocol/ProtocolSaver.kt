package ru.avem.viu35.protocol

import androidx.compose.ui.res.useResource
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ru.avem.viu35.copyFileFromStream
import ru.avem.viu35.database.entities.Protocol
import ru.avem.viu35.sp
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

lateinit var pointsName : XSSFCell
lateinit var uViu : XSSFCell
lateinit var iViu : XSSFCell
lateinit var uMgr : XSSFCell
lateinit var rMgr : XSSFCell
lateinit var result : XSSFCell
fun saveProtocolAsWorkbook(listProtocols: List<Protocol>, path: String = "cfg/lastOpened.xlsx") {

    val template = File(path)

    if (File("cfg${sp}protocol.xlsx").exists()) {
        copyFileFromStream(File("cfg/protocol.xlsx").inputStream(), template)
    } else {
        useResource("protocol.xlsx") {
            copyFileFromStream(it, File("cfg/protocol.xlsx"))
        }
        copyFileFromStream(File("cfg/protocol.xlsx").inputStream(), template)
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
                                "#pointsName#" -> pointsName = cell
                                "#uViu#" -> uViu = cell
                                "#iViu#" -> iViu = cell
                                "#uMgr#" -> uMgr = cell
                                "#rMgr#" -> rMgr = cell
                                "#result#" ->result = cell
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
            fillData(listProtocols)
            val outStream = ByteArrayOutputStream()
            wb.write(outStream)
            outStream.close()
        }
    } catch (e: FileNotFoundException) {
    }
}

fun fillData(listProtocols: List<Protocol>) {
    listProtocols.forEachIndexed { index, d ->
        pointsName.sheet.getRow(pointsName.address.row + index).createCell(pointsName.address.column)
            .setCellValue(d.pointsName)
        uViu.sheet.getRow(uViu.address.row + index).createCell(uViu.address.column).setCellValue(d.uViu)
        iViu.sheet.getRow(iViu.address.row + index).createCell(iViu.address.column).setCellValue(d.iViu)
        uMgr.sheet.getRow(uMgr.address.row + index).createCell(uMgr.address.column).setCellValue(d.uMgr)
        rMgr.sheet.getRow(rMgr.address.row + index).createCell(rMgr.address.column).setCellValue(d.rMgr)
        result.sheet.getRow(result.address.row + index).createCell(result.address.column).setCellValue(d.result)
    }
}