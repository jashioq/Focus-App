package di

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.jan.focus.createAndroidDataStore
import com.jan.focus.database.AppDatabase
import data.dataSource.appState.AppStateObserver
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import platform.notification.LiveTimerNotification

actual val dataSourceModule = module {
    single {
        createAndroidDataStore(
            context = androidContext(),
        )
    }

    single {
        AppStateObserver()
    }

    single {
        LiveTimerNotification()
    }

    single<app.cash.sqldelight.db.SqlDriver> {
        AndroidSqliteDriver(AppDatabase.Schema, androidContext(), "app.db")
    }
}
