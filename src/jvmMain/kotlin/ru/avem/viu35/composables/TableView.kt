package ru.avem.viu35.composables

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun <T> TableView(
    selectedItem: T?,
    items: List<T>,
    columns: List<KProperty1<T, Any>>,
    columnNames: List<String> = emptyList(),
    onItemPrimaryPressed: (Int) -> Unit,
    onItemSecondaryPressed: (Int) -> Unit,
    contextMenuContent: @Composable () -> Unit,
    isExpandedDropdownMenu: MutableState<Boolean>
) {
    var hoveredItem by remember { mutableStateOf(selectedItem) }
    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(bottom = 60.dp)) {
        Row {
            if (columnNames.size == columns.size) {
                columnNames.forEach {
                    Box(
                        modifier = Modifier.border(
                            width = 1.dp, color = MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp)
                        ).weight(0.3f).height(64.dp).clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colors.primary), contentAlignment = Alignment.Center
                    ) {
                        Text(it, style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colors.surface))
                    }
                }
            } else {
                columns.forEach {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(10.dp)).weight(0.3f).height(64.dp)
                            .background(MaterialTheme.colors.primary), contentAlignment = Alignment.Center
                    ) {
                        Text(it.name, style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colors.surface))
                    }
                }
            }
        }

        items.forEachIndexed { i, item ->
            Row(modifier = Modifier.mouseClickable(onClick = {
                if (buttons.isPrimaryPressed) {
                    onItemPrimaryPressed(i)
                } else if (buttons.isSecondaryPressed) {
                    onItemSecondaryPressed(i)
                    isExpandedDropdownMenu.value = true
                }
            }).background(
                if (!isExpandedDropdownMenu.value) {
                    if (hoveredItem == item) {
                        MaterialTheme.colors.secondary
                    } else {
                        MaterialTheme.colors.background
                    }
                } else {
                    MaterialTheme.colors.background
                }
            ).pointerMoveFilter( //TODO Deprecated
                onMove = {
                    hoveredItem = item
                    false
                })) {
                columns.forEach { column ->
                    val field = item!!.getField<Any>(column.name)!!.toString()
                    Box(
                        modifier = Modifier.border(
                            width = 1.dp, color = MaterialTheme.colors.primary, shape = RoundedCornerShape(4.dp)
                        ).weight(0.3f).height(48.dp), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = field,
                            fontSize = 30.sp,
                            color = if (selectedItem == item)
                                Color.Gray else MaterialTheme.colors.onSurface
                        )
                    }
                }
                if (selectedItem == item) {
                    DropdownMenu(expanded = isExpandedDropdownMenu.value, onDismissRequest = {
                        isExpandedDropdownMenu.value = false
                    }) {
                        contextMenuContent()
                    }
                }
            }
        }
    }
}

@Throws(IllegalAccessException::class, ClassCastException::class)
inline fun <reified T> Any.getField(fieldName: String): T? {
    this::class.memberProperties.forEach { kCallable ->
        if (fieldName == kCallable.name) {
            return kCallable.getter.call(this) as T?
        }
    }
    return null
}