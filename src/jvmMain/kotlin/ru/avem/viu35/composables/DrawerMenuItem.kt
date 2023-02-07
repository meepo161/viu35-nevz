package ru.avem.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun DrawerMenuItem(
    painter: Painter,
    title: String,
    onClick: () -> Any
) {
    Row(
        modifier = Modifier.padding(top = 4.dp).fillMaxWidth().height(64.dp).clickable {
            onClick()
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painter,
            modifier = Modifier.width(32.dp).height(32.dp).padding(start = 4.dp, end = 4.dp),
            tint = Color.Gray,
            contentDescription = null
        )
        Text(title)
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Any
) {
    Row(
        modifier = Modifier.padding(top = 4.dp).fillMaxWidth().height(64.dp).clickable {
            onClick()
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            modifier = Modifier.width(32.dp).height(32.dp).padding(start = 4.dp, end = 4.dp),
            tint = Color.Gray,
            contentDescription = null
        )
        Text(title)
    }
}
