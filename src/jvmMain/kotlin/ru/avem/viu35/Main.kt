
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import ru.avem.viu35.database.DBManager
import ru.avem.viu35.file
import ru.avem.viu35.screens.auth.LoginScreen
import kotlin.system.exitProcess

var operatorLogin = ""
var operatorPostString = ""
var isTestRunning = false
var isDarkTheme = mutableStateOf(false)

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme(
        if (isDarkTheme.value) {
            darkColors()
        } else {
            lightColors()
        }
    ) {
        Navigator(LoginScreen()) { navigator ->
            SlideTransition(navigator = navigator)
        }
    }
}

fun main() = application {
    if (!file.exists()) {
        file.createNewFile()
    }
    isDarkTheme.value = file.readText() == "1"
    DBManager.validateDB()
    val windowState = rememberWindowState(placement = WindowPlacement.Maximized)

    Window(onCloseRequest = { onExit() }, undecorated = true, resizable = false, state = windowState) {
        App()
    }
}

fun onExit() {
    isTestRunning = false
    exitProcess(0)
}
