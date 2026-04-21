package me.alekseinovikov.open_gtd.features.home

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import kotlinx.coroutines.launch
import me.alekseinovikov.open_gtd.db.TaskEntity

class HomeScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<HomeScreenModel>()
        val selection by model.selection.collectAsState()
        val areas by model.areas.collectAsState()
        val projects by model.projects.collectAsState()
        val tasks by model.tasks.collectAsState()

        var showAddTask by remember { mutableStateOf(false) }
        var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
        var showCreateArea by remember { mutableStateOf(false) }
        var showCreateProject by remember { mutableStateOf(false) }

        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        val sidebar: @Composable () -> Unit = {
            Sidebar(
                selection = selection,
                areas = areas,
                projects = projects,
                onSelect = { sel ->
                    model.select(sel)
                    if (drawerState.isOpen) scope.launch { drawerState.close() }
                },
                onAddArea = { showCreateArea = true },
                onAddProject = { showCreateProject = true },
            )
        }

        BoxWithConstraints(Modifier.fillMaxSize()) {
            val isWide = maxWidth >= 840.dp

            if (isWide) {
                Row(Modifier.fillMaxSize()) {
                    Surface(
                        modifier = Modifier.width(280.dp).fillMaxSize(),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                    ) {
                        sidebar()
                    }
                    VerticalDivider()
                    MainPane(
                        selection = selection,
                        tasks = tasks,
                        projects = projects,
                        areas = areas,
                        showMenuButton = false,
                        onOpenDrawer = {},
                        onAddTask = { showAddTask = true },
                        onTaskClick = { editingTask = it },
                        onDeleteTask = model::deleteTask,
                        modifier = Modifier.weight(1f).fillMaxSize(),
                    )
                }
            } else {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
                            sidebar()
                        }
                    },
                ) {
                    MainPane(
                        selection = selection,
                        tasks = tasks,
                        projects = projects,
                        areas = areas,
                        showMenuButton = true,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onAddTask = { showAddTask = true },
                        onTaskClick = { editingTask = it },
                        onDeleteTask = model::deleteTask,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        if (showAddTask) {
            AddTaskSheet(
                onDismiss = { showAddTask = false },
                onSave = { title, notes ->
                    model.addTaskToInbox(title, notes)
                    showAddTask = false
                },
            )
        }

        editingTask?.let { task ->
            TaskDetailSheet(
                task = task,
                projects = projects,
                areas = areas,
                onDismiss = { editingTask = null },
                onSave = {
                    model.updateTask(it)
                    editingTask = null
                },
                onComplete = {
                    model.completeTask(task)
                    editingTask = null
                },
                onDelete = {
                    model.deleteTask(task)
                    editingTask = null
                },
            )
        }

        if (showCreateArea) {
            CreateAreaDialog(
                onDismiss = { showCreateArea = false },
                onConfirm = { title ->
                    model.createArea(title)
                    showCreateArea = false
                },
            )
        }

        if (showCreateProject) {
            CreateProjectDialog(
                areas = areas,
                onDismiss = { showCreateProject = false },
                onConfirm = { title, areaId ->
                    model.createProject(title, areaId)
                    showCreateProject = false
                },
            )
        }
    }
}
