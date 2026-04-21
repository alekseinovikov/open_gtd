package me.alekseinovikov.open_gtd.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AreaDao {

    @Query("SELECT * FROM areas ORDER BY title COLLATE NOCASE")
    fun getAll(): Flow<List<Areas>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(area: Areas)

    @Delete
    suspend fun delete(area: Areas)
}
