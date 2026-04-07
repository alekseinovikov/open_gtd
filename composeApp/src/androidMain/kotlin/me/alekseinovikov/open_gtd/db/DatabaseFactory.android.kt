package me.alekseinovikov.open_gtd.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

lateinit var appContext: Context

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = appContext.getDatabasePath(DB_NAME)
    return Room.databaseBuilder(
        context = appContext,
        name = dbFile.absolutePath
    )
}
