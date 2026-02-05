package platform.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.model.TimerMode
import platform.model.TimerToggleState

actual class LiveTimerNotification : KoinComponent {
    private val context: Context by inject()

    actual val toggleState: Flow<TimerToggleState> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                when (intent?.action) {
                    TimerForegroundService.BROADCAST_PAUSE_REQUESTED -> {
                        trySend(TimerToggleState.PAUSE_REQUESTED)
                    }
                    TimerForegroundService.BROADCAST_RESUME_REQUESTED -> {
                        trySend(TimerToggleState.RESUME_REQUESTED)
                    }
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(TimerForegroundService.BROADCAST_PAUSE_REQUESTED)
            addAction(TimerForegroundService.BROADCAST_RESUME_REQUESTED)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    actual fun start(mode: TimerMode, timeLeftSeconds: Int, totalTimeSeconds: Int, isPaused: Boolean) {
        val intent = Intent(context, TimerForegroundService::class.java).apply {
            action = TimerForegroundService.ACTION_START
            putExtra(TimerForegroundService.EXTRA_MODE, mode.name)
            putExtra(TimerForegroundService.EXTRA_TIME_LEFT, timeLeftSeconds)
            putExtra(TimerForegroundService.EXTRA_TOTAL_TIME, totalTimeSeconds)
            putExtra(TimerForegroundService.EXTRA_IS_PAUSED, isPaused)
        }
        context.startForegroundService(intent)
    }

    actual fun update(
        mode: TimerMode?,
        timeLeftSeconds: Int?,
        totalTimeSeconds: Int?,
        isPaused: Boolean?,
    ) {
        val intent = Intent(context, TimerForegroundService::class.java).apply {
            action = TimerForegroundService.ACTION_UPDATE
            mode?.let { putExtra(TimerForegroundService.EXTRA_MODE, it.name) }
            timeLeftSeconds?.let { putExtra(TimerForegroundService.EXTRA_TIME_LEFT, it) }
            totalTimeSeconds?.let { putExtra(TimerForegroundService.EXTRA_TOTAL_TIME, it) }
            isPaused?.let { putExtra(TimerForegroundService.EXTRA_IS_PAUSED, it) }
        }
        context.startService(intent)
    }

    actual fun stop() {
        val intent = Intent(context, TimerForegroundService::class.java).apply {
            action = TimerForegroundService.ACTION_STOP
        }
        context.startService(intent)
    }
}
