package me.alekseinovikov.open_gtd

import me.alekseinovikov.open_gtd.db.AppDatabase
import me.alekseinovikov.open_gtd.db.getDatabaseBuilder
import me.alekseinovikov.open_gtd.db.getRoomDatabase
import me.alekseinovikov.open_gtd.features.inbox.ScreenModel
import org.koin.dsl.module

val appModule = module {
    single { getRoomDatabase(getDatabaseBuilder()) }
    single { get<AppDatabase>().taskDao() }
    factory { ScreenModel(dao = get()) }
}
