package me.alekseinovikov.open_gtd.db

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val os = System.getProperty("os.name").lowercase()
    val userHome = System.getProperty("user.home")

    val appDataDir = when {
        os.contains("win") -> File(System.getenv("APPDATA"), "OpenGTD")
        os.contains("mac") -> File(userHome, "Library/Application Support/OpenGTD")
        else -> File(userHome, ".local/share/OpenGTD")
    }

    if(!appDataDir.exists()) {
        appDataDir.mkdirs()
    }

    val dbFile = File(appDataDir, DB_NAME)

    return Room.databaseBuilder(name = dbFile.absolutePath)
}
