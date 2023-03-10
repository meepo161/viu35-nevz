import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import ru.avem.viu35.communication.model.CM
import ru.avem.viu35.communication.model.devices.rele.ReleController
import ru.avem.viu35.database.validateDB
import ru.avem.viu35.screens.MainScreen
import kotlin.system.exitProcess

var isRunning = false

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme(colors = MaterialTheme.colors.copy(primary = Color(0xFF0071bb))) {
//        Navigator(Test1Screen) {
        Navigator(MainScreen) {
            FadeTransition(it)
        }
    }
}

fun main() = application {
    validateDB()
    Window(onCloseRequest = { onExit() }, undecorated = true, resizable = false) {
        window.placement = WindowPlacement.Maximized
        App()
    }
}

fun onExit() {
    isRunning = false
    CM.device<ReleController>(CM.DeviceID.DD3).offAll()
    exitProcess(0)
}

