package me.alekseinovikov.open_gtd.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.time.Duration.Companion.milliseconds
import me.alekseinovikov.open_gtd.db.Areas
import me.alekseinovikov.open_gtd.db.ListType
import me.alekseinovikov.open_gtd.db.Project
import me.alekseinovikov.open_gtd.db.TaskEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPane(
    selection: Selection,
    tasks: List<TaskEntity>,
    projects: List<Project>,
    areas: List<Areas>,
    showMenuButton: Boolean,
    onOpenDrawer: () -> Unit,
    onAddTask: () -> Unit,
    onTaskClick: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = titleFor(selection, projects, areas)
    val showDatePlaceholder = selection is Selection.ListSel &&
        (selection.type == ListType.TODAY || selection.type == ListType.UPCOMING)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(title, fontWeight = FontWeight.SemiBold)
                        if (tasks.isNotEmpty()) {
                            Text(
                                text = "  ${tasks.size}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (showMenuButton) {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(Icons.Default.Menu, contentDescription = "Open navigation")
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Icon(Icons.Default.Add, contentDescription = "Add task to Inbox")
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                showDatePlaceholder -> EmptyState(
                    title = "Due-date scheduling is coming soon.",
                    subtitle = "For now, capture tasks in Inbox and route them to Anytime or Someday.",
                )
                tasks.isEmpty() -> EmptyState(
                    title = emptyTitleFor(selection),
                    subtitle = null,
                )
                else -> TaskList(
                    tasks = tasks,
                    projects = projects,
                    selection = selection,
                    onTaskClick = onTaskClick,
                    onDeleteTask = onDeleteTask,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskList(
    tasks: List<TaskEntity>,
    projects: List<Project>,
    selection: Selection,
    onTaskClick: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
) {
    val projectById = projects.associateBy { it.id }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items(tasks, key = { it.id }) { task ->
            val dismissState = rememberSwipeToDismissBoxState()

            LaunchedEffect(dismissState.currentValue) {
                if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                    kotlinx.coroutines.delay(250.milliseconds)
                    onDeleteTask(task)
                }
            }

            SwipeToDismissBox(
                state = dismissState,
                modifier = Modifier.animateItem(),
                enableDismissFromStartToEnd = false,
                enableDismissFromEndToStart = true,
                backgroundContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                },
                content = {
                    TaskRow(
                        task = task,
                        project = task.projectId?.let { projectById[it] },
                        selection = selection,
                        onClick = { onTaskClick(task) },
                    )
                },
            )
        }
    }
}

@Composable
private fun TaskRow(
    task: TaskEntity,
    project: Project?,
    selection: Selection,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.RadioButtonUnchecked,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
            Column(
                modifier = Modifier.padding(start = 12.dp).fillMaxWidth(),
            ) {
                Text(task.title, style = MaterialTheme.typography.bodyLarge)
                if (!task.notes.isNullOrBlank()) {
                    Text(
                        text = task.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                val chipLabel = secondaryLabel(task, project, selection)
                if (chipLabel != null) {
                    AssistChip(
                        onClick = onClick,
                        label = { Text(chipLabel) },
                        modifier = Modifier.padding(top = 6.dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String?) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(40.dp),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp),
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

private fun titleFor(
    selection: Selection,
    projects: List<Project>,
    areas: List<Areas>,
): String = when (selection) {
    is Selection.ListSel -> when (selection.type) {
        ListType.INBOX -> "Inbox"
        ListType.TODAY -> "Today"
        ListType.UPCOMING -> "Upcoming"
        ListType.ANYTIME -> "Anytime"
        ListType.SOMEDAY -> "Someday"
    }
    is Selection.ProjectSel -> projects.firstOrNull { it.id == selection.projectId }?.title ?: "Project"
    is Selection.AreaSel -> areas.firstOrNull { it.id == selection.areaId }?.title ?: "Area"
}

private fun emptyTitleFor(selection: Selection): String = when (selection) {
    is Selection.ListSel -> when (selection.type) {
        ListType.INBOX -> "Inbox zero — nice."
        else -> "No tasks here yet."
    }
    is Selection.ProjectSel -> "No tasks in this project yet."
    is Selection.AreaSel -> "No tasks across this area's projects."
}

private fun secondaryLabel(
    task: TaskEntity,
    project: Project?,
    selection: Selection,
): String? = when (selection) {
    is Selection.ListSel -> project?.title
    is Selection.ProjectSel -> runCatching { ListType.valueOf(task.listType) }.getOrNull()?.prettyName()
    is Selection.AreaSel -> project?.title
}

private fun ListType.prettyName(): String = when (this) {
    ListType.INBOX -> "Inbox"
    ListType.TODAY -> "Today"
    ListType.UPCOMING -> "Upcoming"
    ListType.ANYTIME -> "Anytime"
    ListType.SOMEDAY -> "Someday"
}
