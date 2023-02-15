package ru.avem.viu35.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.avem.viu35.database.entities.TestItemFieldScheme
import ru.avem.viu35.database.entities.TestItemScheme
import ru.avem.viu35.futureTVM

@Composable
fun TestObjectListItem(text: String, testObject: TestItemScheme) {
    val navigator = LocalNavigator.currentOrThrow

    Card(elevation = 4.dp, modifier = Modifier.padding(8.dp).clickable {
        futureTVM.value = testObject.tests
    }) {
        Row(
            modifier = Modifier.fillMaxWidth().height(64.dp).padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, fontWeight = FontWeight.Bold)
        }
    }
}