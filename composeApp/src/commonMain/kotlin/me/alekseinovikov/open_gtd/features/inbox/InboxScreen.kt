package me.alekseinovikov.open_gtd.features.inbox

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

class InboxScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<InboxScreenModel>()

        val tasks by screenModel.tasks.collectAsState()

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { screenModel.addRandomTask() }) {
                    Text("+")
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(tasks) { task ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(
                            text = task.title,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }

}