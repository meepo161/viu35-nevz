import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import ru.avem.viu35.screens.MainScreen

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
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
