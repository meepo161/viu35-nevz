package ru.avem.viu35.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomDialog(
    modifier: Modifier = Modifier.width(500.dp),
    title: String,
    text: String,
    yesButton: String,
    noButton: String,
    yesCallback: () -> Unit,
    noCallback: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {},
        title = { Text(title, style = MaterialTheme.typography.h5) },
        text = { Text(text, style = MaterialTheme.typography.h5) },
        buttons = {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) { content() }
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = {
                        yesCallback()
                    }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = yesButton, style = MaterialTheme.typography.h5)
                        }
                    }
                    Button(onClick = {
                        noCallback()
                    }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = noButton, style = MaterialTheme.typography.h5)
                        }
                    }
                }
            }
        }
    )
}
