package ru.avem.viu35.composables

import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

@Composable
fun EnabledTextButton(text: String, enabled: Boolean = true, color: Color? = null, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = enabled, colors = ButtonDefaults.buttonColors(backgroundColor = color ?: MaterialTheme.colors.primary)) {
        Text(
            text = text,
            style = MaterialTheme.typography.h4 //TODO вынести отдельным параметром (css)
        )
    }
}
