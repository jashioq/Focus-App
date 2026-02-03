package data.dataSource.appState

import android.content.Context
import android.os.PowerManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import domain.model.AppFocusState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class AppStateObserver : KoinComponent {
    private val context: Context by inject()
    private var lifecycleObserver: LifecycleEventObserver? = null

    actual fun startObserving(onStateChange: (AppFocusState) -> Unit) {
        lifecycleObserver = LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    onStateChange(AppFocusState.Foreground)
                }
                Lifecycle.Event.ON_STOP -> {
                    // Only emit Background if screen is on (user switched apps)
                    // If screen is off, user just locked the phone
                    if (isScreenOn()) {
                        onStateChange(AppFocusState.Background)
                    }
                }
                else -> {}
            }
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver!!)
    }

    actual fun stopObserving() {
        lifecycleObserver?.let {
            ProcessLifecycleOwner.get().lifecycle.removeObserver(it)
        }
        lifecycleObserver = null
    }

    private fun isScreenOn(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isInteractive
    }
}
