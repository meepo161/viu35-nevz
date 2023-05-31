package ru.avem.viu35.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.mouseClickable
import androidx.compose.material.DropdownMenu
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
    isExpandedDropdownMenu: MutableState<Boolean>,
    listWeight: List<Float> = listOf(0.6f, 0.1f, 0.1f, 0.1f, 0.16f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f)
) {
    var hoveredItem by remember { mutableStateOf(selectedItem) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row {
            if (columnNames.size == columns.size) {
                columnNames.forEachIndexed { index, columnNames ->
                    Box(
                        modifier = Modifier.border(width = 3.dp, color = MaterialTheme.colors.onBackground)
                            .background(MaterialTheme.colors.primary)
                            .weight(listWeight[index]).height(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            columnNames,
                            style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colors.background)
                        )
                    }
                }
            } else {
                columns.forEach {
                    Box(
                        modifier = Modifier.weight(0.3f).height(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            it.name,
                            style = TextStyle(fontWeight = FontWeight.Bold)
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
                    })
                        .background(
                            if (!isExpandedDropdownMenu.value) {
                                if (selectedItem == items[it]) {
                                    MaterialTheme.colors.onBackground.copy(alpha = 0.4f)
                                } else if (hoveredItem == items[it]) {
                                    MaterialTheme.colors.onBackground.copy(alpha = 0.2f)
                                } else {
                                    MaterialTheme.colors.background
                                }
                            } else {
                                MaterialTheme.colors.background
                            }
                        )
                        .pointerMoveFilter(
                            onMove = { _ ->
                                hoveredItem = items[it]
                                false
                            },
                            onExit = {
                                hoveredItem = null
                                false
                            })
                ) {
                    columns.forEachIndexed { index, column ->

                        val field = items[it]!!.getField<Any>(column.name)!!.toString()
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colors.onBackground
                                ).weight(listWeight[index]).height(48.dp), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = field,
                                modifier = Modifier.padding(4.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 28.sp
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