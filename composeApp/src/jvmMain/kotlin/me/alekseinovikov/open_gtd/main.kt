package me.alekseinovikov.open_gtd

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import open_gtd.composeapp.generated.resources.Res
import open_gtd.composeapp.generated.resources.icon
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Open GTD",
        icon = painterResource(Res.drawable.icon),
    ) {
        App()
    }
}