package di

import data.repository.DataStoreRepository
import org.koin.dsl.module

val dataModule = module {
    single<domain.repository.DataStoreRepository> {
        DataStoreRepository(
            dataStore = get(),
        )
    }

    single<domain.repository.AppStateRepository> {
        data.repository.AppStateRepository(
            appStateObserver = get(),
        )
    }
}
