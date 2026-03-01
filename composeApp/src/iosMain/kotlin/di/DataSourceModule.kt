package di

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.jan.focus.database.AppDatabase
import createIosDataStore
import data.dataSource.appState.AppStateObserver
import org.koin.dsl.module
import platform.notification.LiveTimerNotification

actual val dataSourceModule = module {
    single {
        createIosDataStore()
    }

    single {
        AppStateObserver()
    }

    single {
        LiveTimerNotification()
    }

    single<app.cash.sqldelight.db.SqlDriver> {
        NativeSqliteDriver(AppDatabase.Schema, "app.db")
    }
}