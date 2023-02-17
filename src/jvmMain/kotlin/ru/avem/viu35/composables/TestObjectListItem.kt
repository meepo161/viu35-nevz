package ru.avem.viu35.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TestObjectListItem(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    Card(elevation = 4.dp, modifier = modifier.padding(8.dp).clickable {
        onClick()
    }) {
        Row(
            modifier = Modifier.fillMaxWidth().height(48.dp).padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, fontWeight = FontWeight.Bold)
        }
    }
}