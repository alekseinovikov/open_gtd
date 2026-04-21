package me.alekseinovikov.open_gtd

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import me.alekseinovikov.open_gtd.features.home.HomeScreen
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinApplication(
            configuration = koinConfiguration {
                modules(appModule)
            }
        ) {
            Navigator(screen = HomeScreen())
        }
    }
}
