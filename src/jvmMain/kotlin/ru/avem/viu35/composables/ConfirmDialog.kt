package ru.avem.viu35.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import ru.avem.viu35.composables.animatedimage.Blank
import ru.avem.viu35.composables.animatedimage.animate
import ru.avem.viu35.composables.animatedimage.loadOrNull
import ru.avem.viu35.composables.animatedimage.loadResourceAnimatedImage
import java.io.File

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ConfirmDialog(
    title: String,
    text: String,
    nameGif: String = "",
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
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (nameGif != "") {
                    Image(
                        bitmap = loadOrNull { loadResourceAnimatedImage(File(nameGif).path) }?.animate()
                            ?: ImageBitmap.Blank,
                        contentDescription = null,
                        modifier = Modifier.size(300.dp)
                    )
                }
                Button(onClick = {
                    yesCallback()
                }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ок", style = MaterialTheme.typography.h5)
                    }
                }
//                Button(onClick = {
//                    noCallback()
//                }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
//                    Row(
//                        horizontalArrangement = Arrangement.spacedBy(4.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text("НЕТ", style = MaterialTheme.typography.h5)
//                    }
//                }
            }
        }
    )
}

