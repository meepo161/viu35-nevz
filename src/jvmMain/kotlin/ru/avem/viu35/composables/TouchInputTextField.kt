package ru.avem.viu35.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.avem.viu35.composables.keyboards.AlphabeticalKeyboard
import ru.avem.viu35.composables.keyboards.KeyboardLanguage

@Composable
fun TouchInputTextField(
    value: MutableState<String>,
    isCanBeEmpty: Boolean = false,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    errorString: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        disabledBorderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
        disabledLabelColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
        disabledLeadingIconColor = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.IconOpacity),
        disabledPlaceholderColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium),
        disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
        disabledTrailingIconColor = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.IconOpacity),
    )
) {
    val navigator = LocalNavigator.currentOrThrow
    Column {
        OutlinedTextField(
            value = value.value,
            onValueChange = {},
            modifier = modifier.clickable {
                navigator.push(AlphabeticalKeyboard(value, isCanBeEmpty, KeyboardLanguage.RU))
            },
            enabled = false,
            readOnly = readOnly,
            textStyle = textStyle,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors
        )
        if (isError) {
            Text(
                text = errorString,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp, top = 0.dp)
            )
        }
    }
}