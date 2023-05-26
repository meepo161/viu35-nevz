package ru.avem.viu35.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier


@Composable
fun LogText(
    modifier: Modifier,
    log: SnapshotStateList<String>,
    logScrollState: LazyListState
) {
    ScrollableLazyColumn(modifier = modifier, scrollState = logScrollState) {
        items(log.size) { index ->
            Text(log[index], modifier = Modifier.fillMaxWidth())
        }
    }
}