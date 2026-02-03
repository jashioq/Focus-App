package di

import createIosDataStore
import data.dataSource.appState.AppStateObserver
import org.koin.dsl.module

actual val dataSourceModule = module {
    single {
        createIosDataStore()
    }

    single {
        AppStateObserver()
    }
}