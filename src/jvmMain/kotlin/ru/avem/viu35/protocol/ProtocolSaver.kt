package ru.avem.viu35.protocol

import androidx.compose.ui.res.useResource
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ru.avem.viu35.copyFileFromStream
import ru.avem.viu35.database.entities.Protocol
import ru.avem.viu35.sp
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

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
//                                "#pointsName#" -> cell.setCellValue(listProtocols.last().pointsName)
//                                "#uViu#" -> cell.setCellValue(listProtocols.last().uViu)
//                                "#iViu#" -> cell.setCellValue(listProtocols.last().iViu)
//                                "#uMgr#" -> cell.setCellValue(listProtocols.last().uMgr)
//                                "#rMgr#" -> cell.setCellValue(listProtocols.last().rMgr)
//                                "#result#" -> cell.setCellValue(listProtocols.last().result)
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
//                            fillData(listProtocols)
                        }
                    }
                }
            }
            val outStream = ByteArrayOutputStream()
            wb.write(outStream)
            outStream.close()
        }
    } catch (e: FileNotFoundException) {
//        errorNotification(
//            "Ошибка",
//            "Не удалось сохранить протокол на диск",
//            Pos.BOTTOM_CENTER
//        )
    }
}

//fun fillData(listProtocols: List<Protocol>) {
////    try {
//    listProtocols.forEachIndexed { index, d ->
//        pointsName.sheet.getRow(pointsName.address.row + index).createCell(pointsName.address.column)
//            .setCellValue(d.pointsName)
//        uViu.sheet.getRow(uViu.address.row + index).createCell(uViu.address.column).setCellValue(d.uViu)
//        iViu.sheet.getRow(iViu.address.row + index).createCell(iViu.address.column).setCellValue(d.iViu)
//        uMgr.sheet.getRow(uMgr.address.row + index).createCell(uMgr.address.column).setCellValue(d.uMgr)
//        rMgr.sheet.getRow(rMgr.address.row + index).createCell(rMgr.address.column).setCellValue(d.rMgr)
//        result.sheet.getRow(result.address.row + index).createCell(result.address.column).setCellValue(d.result)
////        pointsName.setCellValue(d.pointsName)
////        uViu.setCellValue(d.uViu)
////        iViu.setCellValue(d.iViu)
////        uMgr.setCellValue(d.uMgr)
////        rMgr.setCellValue(d.rMgr)
////        result.setCellValue(d.result)
//    }
////    } catch (e: Exception) {
////        println(e)
////    }
//}