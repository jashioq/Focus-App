package di

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
}