package me.alekseinovikov.open_gtd.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val notes: String? = null,
    val status: String, // Saving enum as a string (sqlite compatibility)
    val listType: String,
    val projectId: String? = null,
    val dueDate: Long? = null, // Saving date as a timestamp to make it universal
)

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey val id: String,
    val title:  String,
    val areaId: String? = null, // Grouping by areas
)

@Entity(tableName = "areas")
data class Areas(
    @PrimaryKey val id: String,
    val title: String,
)

enum class TaskStatus {
    PENDING, COMPLETED, CANCELLED
}

enum class ListType {
    INBOX, TODAY, UPCOMING, ANYTIME, SOMEDAY
}

class TaskEntityConverters {
    @TypeConverter
    fun fromStatus(status: TaskStatus): String = status.name
    @TypeConverter
    fun toStatus(status: String): TaskStatus = TaskStatus.valueOf(status)

    @TypeConverter
    fun fromListType(type: ListType): String = type.name
    @TypeConverter
    fun toListType(value: String): ListType = ListType.valueOf(value)
}
