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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun <T> ComboBox(
    selectedItem: MutableState<T>,
    modifier: Modifier = Modifier,
    onDismissState: () -> Unit = {},
    items: List<T>,
    selectedValue: (T) -> Unit = {},
    isEditable: Boolean = true
) {
    var expandedState by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    Column(modifier = modifier.border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp)).width(280.dp).height(60.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().clickable {
                expandedState = true
            }.fillMaxWidth().height(64.dp),
        ) {
            Text(selectedItem.value.toString(), modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.h5)
            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
        }
        if (isEditable) {
            DropdownMenu(
                expanded = expandedState,
                onDismissRequest = {
                    expandedState = false
                    onDismissState()
                }) {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.width(500.dp).height(300.dp)
                        .draggable(orientation = Orientation.Vertical, state = rememberDraggableState {
                            scope.launch {
                                scrollState.scrollBy(-it)
                            }
                        })
                ) {
                    items.forEach { item ->
                        item {
                            DropdownMenuItem(modifier = Modifier.fillMaxWidth().height(64.dp), onClick = {
                                selectedItem.value = item
                                selectedValue(item)
                                expandedState = false
                            }) {
                                Text(item.toString(), style = MaterialTheme.typography.h5)
                            }
                        }
                    }
                }
            }
        }
    }
}
