package ru.avem.viu35.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ConfirmDialog(
    title: String,
    text: String,
    yesCallback: () -> Unit,
    noCallback: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.width(500.dp),
        onDismissRequest = {},
        title = { Text(title, style = MaterialTheme.typography.h5) },
        text = { Text(text, style = MaterialTheme.typography.h5) },
        buttons = {
            Column(
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Button(onClick = {
                    yesCallback()
                }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ДА", style = MaterialTheme.typography.h5)
                    }
                }
                Button(onClick = {
                    noCallback()
                }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("НЕТ", style = MaterialTheme.typography.h5)
                    }
                }
            }
        }
    )
}
