package me.alekseinovikov.open_gtd.features.inbox

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.alekseinovikov.open_gtd.db.ListType
import me.alekseinovikov.open_gtd.db.TaskDao
import me.alekseinovikov.open_gtd.db.TaskEntity
import me.alekseinovikov.open_gtd.db.TaskStatus
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class InboxScreenModel(private val dao: TaskDao) : ScreenModel {

    val tasks: StateFlow<List<TaskEntity>> = dao.getTasksByList(ListType.INBOX.name)
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalUuidApi::class)
    fun addRandomTask() {
        screenModelScope.launch {
            val newTask = TaskEntity(
                id = Uuid.random().toString(),
                title = "New task from ${Clock.System.now()}",
                status = TaskStatus.PENDING.name,
                listType = ListType.INBOX.name,
            )

            dao.insertTask(newTask)
        }
    }

}