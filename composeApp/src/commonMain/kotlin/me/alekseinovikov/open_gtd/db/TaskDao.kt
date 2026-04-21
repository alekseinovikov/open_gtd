package me.alekseinovikov.open_gtd.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE listType = :listType AND status != :excludedStatus ORDER BY title COLLATE NOCASE")
    fun getTasksByList(
        listType: String,
        excludedStatus: String = TaskStatus.COMPLETED.name
    ): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE projectId = :projectId AND status != :excludedStatus ORDER BY title COLLATE NOCASE")
    fun getTasksByProject(
        projectId: String,
        excludedStatus: String = TaskStatus.COMPLETED.name
    ): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT t.* FROM tasks t
        INNER JOIN projects p ON t.projectId = p.id
        WHERE p.areaId = :areaId AND t.status != :excludedStatus
        ORDER BY t.title COLLATE NOCASE
        """
    )
    fun getTasksByArea(
        areaId: String,
        excludedStatus: String = TaskStatus.COMPLETED.name
    ): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks SET status = :newStatus WHERE id = :taskId")
    suspend fun completeTask(taskId: String, newStatus: String = TaskStatus.COMPLETED.name)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

}