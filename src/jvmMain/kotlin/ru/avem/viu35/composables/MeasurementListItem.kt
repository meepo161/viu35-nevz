package ru.avem.viu35.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MeasurementListItem(item: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.height(200.dp).padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(color = MaterialTheme.colors.primary) {
            Icon(Icons.Filled.Android, modifier = Modifier.size(140.dp), contentDescription = null, tint = Color.White)
        }
        Text(item, modifier = Modifier.width(200.dp), textAlign = TextAlign.Center)
    }
}