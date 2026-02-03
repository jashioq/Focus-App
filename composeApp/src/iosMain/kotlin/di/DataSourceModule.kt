package di

import createIosDataStore
import org.koin.dsl.module

actual val dataSourceModule = module {
    single {
        createIosDataStore()
    }
}