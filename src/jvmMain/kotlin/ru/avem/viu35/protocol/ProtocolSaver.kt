package ru.avem.viu35.protocol

import androidx.compose.ui.res.useResource
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ru.avem.viu35.copyFileFromStream
import ru.avem.viu35.database.entities.Protocol
import ru.avem.viu35.utils.autoformat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

fun saveProtocolAsWorkbook(listProtocols: List<Protocol>, path: String = "lastOpened.xlsx") {

    val template = File(path)
    val size = listProtocols.size
    if (File("protocol$size.xlsx").exists()) {
        copyFileFromStream(File("protocol$size.xlsx").inputStream(), template)
    } else {
        useResource("protocol$size.xlsx") {
            copyFileFromStream(it, File("protocol$size.xlsx"))
        }
        copyFileFromStream(File("protocol$size.xlsx").inputStream(), template)
    }

    try {
        XSSFWorkbook(template).use { wb ->
            val sheet = wb.getSheetAt(0)
            for (iRow in 0 until 150) {
                val row = sheet.getRow(iRow)
                if (row != null) {
                    listProtocols.forEachIndexed { index, protocol ->
                        for (iCell in 0 until 250) {
                            val cell = row.getCell(iCell)
                            if (cell != null && (cell.cellType == CellType.STRING)) {
                                when (cell.stringCellValue) {
                                    "#itemName#" -> cell.setCellValue(listProtocols.last().itemName)
                                    "#itemType#" -> cell.setCellValue(listProtocols.last().itemType)
                                    "#date#" -> cell.setCellValue(listProtocols.last().date)
                                    "#time#" -> cell.setCellValue(listProtocols.last().time)
                                    "#operator#" -> cell.setCellValue(listProtocols.last().operator)
                                    "#operatorPost#" -> cell.setCellValue(listProtocols.last().operatorPost)
                                    "#spec_uViu#" -> cell.setCellValue(listProtocols.last().spec_uViu)
                                    "#spec_uViuFault#" -> cell.setCellValue("±" + listProtocols.last().spec_uViuFault)
                                    "#serial$index#" -> cell.setCellValue(listProtocols[index].serial)
                                    "#date_product$index#" -> cell.setCellValue(listProtocols[index].dateProduct)
                                    "#number$index#" -> cell.setCellValue(listProtocols[index].id.toString())
                                    "#pointsName$index#" -> cell.setCellValue(listProtocols[index].pointsName)
                                    "#spec_uViuAmp$index#" -> cell.setCellValue(listProtocols[index].spec_uViuAmp)
                                    "#spec_uViuAmpFault$index#" -> cell.setCellValue("±" + (listProtocols[index].spec_uViuAmpFault.toInt() * 1.41).autoformat())
                                    "#spec_iViu$index#" -> cell.setCellValue(listProtocols[index].spec_iViu)
                                    "#spec_uMgr$index#" -> cell.setCellValue(listProtocols[index].spec_uMgr)
                                    "#spec_rMgr$index#" -> cell.setCellValue(listProtocols[index].spec_rMgr)
                                    "#uViuAMP$index#" -> cell.setCellValue(listProtocols[index].uViuAmp)
                                    "#uViu$index#" -> cell.setCellValue(listProtocols[index].uViu)
                                    "#iViu$index#" -> cell.setCellValue(listProtocols[index].iViu)
                                    "#uMgr$index#" -> cell.setCellValue(listProtocols[index].uMgr)
                                    "#rMgr$index#" -> cell.setCellValue(listProtocols[index].rMgr)
                                    "#resultViu$index#" -> cell.setCellValue(listProtocols[index].resultViu)
                                    "#resultMgr$index#" -> cell.setCellValue(listProtocols[index].resultMgr)
                                    "#result$index#" -> cell.setCellValue(if (listProtocols[index].resultViu != "Не выдержано" && listProtocols[index].resultMgr != "Не соответствует") "успешно прошел" else "не прошел")
                                    "#goden$index#" -> cell.setCellValue(if (listProtocols[index].resultViu != "Не выдержано" && listProtocols[index].resultMgr != "Не соответствует") "годен" else "не годен")

//                                    else -> {
//                                        if (cell.stringCellValue.contains("#")) {
//                                            cell.setCellValue("")
//                                        }
//                                    }
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
