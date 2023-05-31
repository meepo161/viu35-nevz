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
import cafe.adriel.voyager.transitions.FadeTransition
import ru.avem.viu35.database.DBManager
import ru.avem.viu35.screens.auth.LoginScreen
import kotlin.system.exitProcess

var operatorLogin = ""
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
        Navigator(LoginScreen()) {
            FadeTransition(it)
        }
    }
}

fun main() = application {
    DBManager.validateDB()
    val windowState = rememberWindowState(placement = WindowPlacement.Fullscreen)

    Window(onCloseRequest = { onExit() }, undecorated = true, resizable = false, state = windowState) {
        App()
    }
}

fun onExit() {
    isTestRunning = false
    exitProcess(0)
}
