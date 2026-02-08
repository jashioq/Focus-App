package platform.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import domain.model.Timer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class LiveTimerNotification : KoinComponent {
    private val context: Context by inject()

    actual val timerToggleFlow: Flow<Unit> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action == TimerForegroundService.BROADCAST_TOGGLE_REQUEST) {
                    trySend(Unit)
                }
            }
        }

        val filter = IntentFilter(TimerForegroundService.BROADCAST_TOGGLE_REQUEST)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    actual val notificationDismissedFlow: Flow<Unit> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action == TimerForegroundService.BROADCAST_DISMISSED) {
                    trySend(Unit)
                }
            }
        }

        val filter = IntentFilter(TimerForegroundService.BROADCAST_DISMISSED)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    actual fun isNotificationActive(): Boolean {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return nm.activeNotifications.any { it.id == TimerForegroundService.NOTIFICATION_ID }
    }

    actual fun set(timer: Timer) {
        val intent = Intent(context, TimerForegroundService::class.java).apply {
            action = TimerForegroundService.ACTION_START
            putExtra(
                TimerForegroundService.EXTRA_BLOCK_SECONDS,
                timer.sequence.map { it.seconds }.toIntArray(),
            )
            putExtra(
                TimerForegroundService.EXTRA_BLOCK_MODES,
                timer.sequence.map { it.mode.name }.toTypedArray(),
            )
            putExtra(TimerForegroundService.EXTRA_SECONDS_ELAPSED, timer.secondsElapsed)
            putExtra(TimerForegroundService.EXTRA_IS_PAUSED, timer.isPaused)
        }
        if (isNotificationActive()) {
            context.startService(intent)
        } else {
            context.startForegroundService(intent)
        }
    }

    actual fun clear() {
        val intent = Intent(context, TimerForegroundService::class.java).apply {
            action = TimerForegroundService.ACTION_STOP
        }
        context.startService(intent)
    }
}
