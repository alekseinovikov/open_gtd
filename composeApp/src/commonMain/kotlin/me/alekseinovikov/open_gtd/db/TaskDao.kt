package me.alekseinovikov.open_gtd.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE listType = :listType AND status != :excludedStatus")
    fun getTasksByList(
        listType: String,
        excludedStatus: String = TaskStatus.COMPLETED.name
    ): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Query("UPDATE tasks SET status = :newStatus WHERE id = :taskId")
    suspend fun completeTask(taskId: String, newStatus: String = TaskStatus.COMPLETED.name)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

}