package me.alekseinovikov.open_gtd.features.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.alekseinovikov.open_gtd.db.Areas
import me.alekseinovikov.open_gtd.db.AreaDao
import me.alekseinovikov.open_gtd.db.ListType
import me.alekseinovikov.open_gtd.db.Project
import me.alekseinovikov.open_gtd.db.ProjectDao
import me.alekseinovikov.open_gtd.db.TaskDao
import me.alekseinovikov.open_gtd.db.TaskEntity
import me.alekseinovikov.open_gtd.db.TaskStatus
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenModel(
    private val taskDao: TaskDao,
    private val projectDao: ProjectDao,
    private val areaDao: AreaDao,
) : ScreenModel {

    private val _selection = MutableStateFlow<Selection>(Selection.Default)
    val selection: StateFlow<Selection> = _selection.asStateFlow()

    val areas: StateFlow<List<Areas>> = areaDao.getAll()
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projects: StateFlow<List<Project>> = projectDao.getAll()
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskEntity>> = _selection
        .flatMapLatest { sel ->
            when (sel) {
                is Selection.ListSel -> taskDao.getTasksByList(sel.type.name)
                is Selection.ProjectSel -> taskDao.getTasksByProject(sel.projectId)
                is Selection.AreaSel -> taskDao.getTasksByArea(sel.areaId)
            }
        }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun select(newSelection: Selection) {
        _selection.value = newSelection
    }

    @OptIn(ExperimentalUuidApi::class)
    fun addTaskToInbox(title: String, notes: String?) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) return
        screenModelScope.launch {
            taskDao.insertTask(
                TaskEntity(
                    id = Uuid.random().toString(),
                    title = trimmedTitle,
                    notes = notes?.trim()?.takeIf { it.isNotEmpty() },
                    status = TaskStatus.PENDING.name,
                    listType = ListType.INBOX.name,
                    projectId = null,
                )
            )
        }
    }

    fun updateTask(task: TaskEntity) {
        screenModelScope.launch {
            taskDao.updateTask(task)
        }
    }

    fun completeTask(task: TaskEntity) {
        screenModelScope.launch {
            taskDao.completeTask(task.id)
        }
    }

    fun deleteTask(task: TaskEntity) {
        screenModelScope.launch {
            taskDao.deleteTask(task)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun createArea(title: String) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return
        screenModelScope.launch {
            areaDao.upsert(Areas(id = Uuid.random().toString(), title = trimmed))
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun createProject(title: String, areaId: String?) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return
        screenModelScope.launch {
            projectDao.upsert(
                Project(
                    id = Uuid.random().toString(),
                    title = trimmed,
                    areaId = areaId,
                )
            )
        }
    }
}
