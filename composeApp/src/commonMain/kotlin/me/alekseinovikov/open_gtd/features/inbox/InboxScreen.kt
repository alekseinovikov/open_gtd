package me.alekseinovikov.open_gtd.features.inbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import kotlin.time.Duration.Companion.milliseconds

class InboxScreen : Screen {

    @Preview
    @Preview
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ScreenModel>()
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
                items(tasks, key = { it.id }) { task ->
                    val dismissState = rememberSwipeToDismissBoxState()

                    LaunchedEffect(dismissState.currentValue) {
                        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                            kotlinx.coroutines.delay(300.milliseconds)
                            screenModel.deleteTask(task)
                        }
                    }

                    SwipeToDismissBox(
                        state = dismissState,
                        modifier = Modifier.animateItem().padding(vertical = 4.dp, horizontal = 8.dp),
                        enableDismissFromStartToEnd = false,
                        enableDismissFromEndToStart = true,
                        backgroundContent = {
                            val direction = dismissState.dismissDirection

                            if (direction == SwipeToDismissBoxValue.EndToStart) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(Color.Red.copy(alpha = 0.8f))
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.White
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Transparent)
                                )
                            }
                        },
                        content = {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = task.title,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    )
                }
            }
        }
    }

}