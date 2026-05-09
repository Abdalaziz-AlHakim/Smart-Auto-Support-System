import androidx.compose.ui.window.application
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import behavioral.observer.*
import creational.singleton.TicketSystem
import ui.AutoSupportApp
import ui.AppState

/**
 * Application entry point for Compose Multiplatform Desktop.
 *
 * 1. Wires Observer listeners to AppState (Compose-reactive state).
 * 2. Opens the main window.
 */
fun main() = application {
    val appState = AppState()

    // ── Register Observer listeners that update Compose state ─────────────────

    val ts = TicketSystem.getInstance()

    // Each listener mutates AppState's mutableState lists → Compose recomposes
    ts.subscribe { event ->
        appState.onTicketEvent(event)
    }

    Window(
        onCloseRequest = ::exitApplication,
        title          = "AutoSupport — Design Pattern Demo",
        state          = WindowState(size = DpSize(1200.dp, 760.dp))
    ) {
        AutoSupportApp(appState)
    }
}
