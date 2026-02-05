package di

import com.jan.focus.createAndroidDataStore
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
}
