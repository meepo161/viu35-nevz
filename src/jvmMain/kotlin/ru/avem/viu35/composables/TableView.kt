package ru.avem.viu35.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.mouseClickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
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
import androidx.compose.ui.text.style.TextAlign
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

    Column(modifier = Modifier.fillMaxWidth()) {
        Row {
            if (columnNames.size == columns.size) {
                columnNames.forEach {
                    Box(
                        modifier = Modifier.border(
                            width = 1.dp, color = MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp)
                        ).weight(0.3f).height(64.dp).clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colors.primary), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            it,
                            style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colors.background)
                        )
                    }
                }
            } else {
                columns.forEach {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(10.dp)).weight(0.3f).height(64.dp)
                            .background(MaterialTheme.colors.background), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            it.name,
                            style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colors.background)
                        )
                    }
                }
            }
        }
        ScrollableLazyColumn {
            items(items.size) {
                Row(
                    modifier = Modifier.mouseClickable(onClick = {
                        if (buttons.isPrimaryPressed) {
                            onItemPrimaryPressed(it)
                        } else if (buttons.isSecondaryPressed) {
                            onItemSecondaryPressed(it)
                            isExpandedDropdownMenu.value = true
                        }
                    }).background(
                        if (!isExpandedDropdownMenu.value) {
                            if (hoveredItem == items[it]) {
                                MaterialTheme.colors.secondary.copy(alpha = 0.2f)
                            } else {
                                MaterialTheme.colors.background
                            }
                        } else {
                            MaterialTheme.colors.background
                        }
                    ).pointerMoveFilter( //TODO Deprecated
                        onMove = { _ ->
                            hoveredItem = items[it]
                            false
                        })
                ) {
                    columns.forEach { column ->
                        val field = items[it]!!.getField<Any>(column.name)!!.toString()
                        Box(
                            modifier = Modifier.border(
                                width = 1.dp, color = MaterialTheme.colors.primary, shape = RoundedCornerShape(4.dp)
                            ).weight(0.3f).height(48.dp), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = field,
                                modifier = Modifier.padding(4.dp),
                                textAlign = TextAlign.Center,
//                                fontSize = 30.sp,
                                color = if (selectedItem == items[it])
                                    MaterialTheme.colors.secondary else MaterialTheme.colors.primary
                            )
                        }
                    }
                    if (selectedItem == items[it]) {
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