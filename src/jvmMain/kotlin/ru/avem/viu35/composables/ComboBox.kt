package ru.avem.viu35.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction

@Composable
fun <T> ComboBox(
    selectedItem: MutableState<T>,
    modifier: Modifier = Modifier,
    onDismissState: () -> Unit = {},
    items: List<T>,
    selectedValue: (T) -> Unit = {},
    isEditable: Boolean = true,
    textAlign: TextAlign = TextAlign.Center,
    fontSize: Int = 20
) {
    var expandedState by remember {
        if (selectedItem.value != null) {
            selectedValue(selectedItem.value)
        }
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    Column(
        modifier = modifier.border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.clickable(enabled = isEditable && !expandedState) {
                expandedState = !expandedState
            },
        ) {
            Text(
                text = if (transaction { selectedItem.value == null }) {
                    ""
                } else {
                    transaction { selectedItem.value.toString() }
                },
                fontSize = fontSize.sp,
                softWrap = false,
                textAlign = textAlign,
                modifier = Modifier.padding(16.dp).weight(0.9f),
            )
            Icon(Icons.Filled.ArrowDropDown, contentDescription = null, modifier = Modifier.weight(0.1f))
        }
        if (isEditable) {
            DropdownMenu(expanded = expandedState, onDismissRequest = {
                expandedState = !expandedState
                onDismissState()
            }) {
                LazyColumn(
                    state = scrollState,
                    modifier = modifier.height(300.dp)
                        .draggable(orientation = Orientation.Vertical, state = rememberDraggableState {
                            scope.launch {
                                scrollState.scrollBy(-it)
                            }
                        })
                ) {
                    items.forEach { item ->
                        item {
                            DropdownMenuItem(modifier = Modifier.height(64.dp), onClick = {
                                selectedItem.value = item
                                selectedValue(item)
                                expandedState = !expandedState
                            }) {
                                Text(
                                    text = transaction { item.toString() },
                                    overflow = TextOverflow.Visible,
                                    fontSize = fontSize.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
