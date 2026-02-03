package di

import com.jan.focus.createAndroidDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val dataSourceModule = module {
    single {
        createAndroidDataStore(
            context = androidContext(),
        )
    }
}
