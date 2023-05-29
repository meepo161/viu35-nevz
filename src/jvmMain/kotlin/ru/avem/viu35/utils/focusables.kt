@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package ru.avem.viu35.utils

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key

fun keyboardActionNext(focusManager: FocusManager) = KeyboardActions(
    onNext = { focusManager.moveFocus(FocusDirection.Next) }
)

@OptIn(ExperimentalComposeUiApi::class)
fun keyEventNext(
    it: KeyEvent,
    focusManager: FocusManager
) = if (it.key == Key.Tab) {
    focusManager.moveFocus(FocusDirection.Next)
    true
} else {
    false
}
