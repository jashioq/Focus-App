package platform.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import domain.model.Timer
import domain.model.TimerBlock
import domain.model.TimerMode
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class LiveTimerNotification : KoinComponent {
    private val context: Context by inject()

    actual val timerUpdateFlow: Flow<Timer> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action != TimerForegroundService.BROADCAST_TIMER_UPDATE) return

                val blockSecondsArray = intent.getIntArrayExtra(TimerForegroundService.EXTRA_BLOCK_SECONDS) ?: return
                val blockModesArray = intent.getStringArrayExtra(TimerForegroundService.EXTRA_BLOCK_MODES) ?: return
                val secondsElapsed = intent.getIntExtra(TimerForegroundService.EXTRA_SECONDS_ELAPSED, 0)
                val isPaused = intent.getBooleanExtra(TimerForegroundService.EXTRA_IS_PAUSED, false)

                val sequence = blockSecondsArray.zip(blockModesArray.toList()) { seconds, mode ->
                    TimerBlock(
                        mode = TimerMode.valueOf(mode),
                        seconds = seconds,
                    )
                }

                val timer = Timer(
                    sequence = sequence,
                    secondsElapsed = secondsElapsed,
                    isPaused = isPaused,
                )

                trySend(timer)
            }
        }

        val filter = IntentFilter(TimerForegroundService.BROADCAST_TIMER_UPDATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    actual fun start(timer: Timer) {
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
        context.startForegroundService(intent)
    }

    actual fun stop() {
        val intent = Intent(context, TimerForegroundService::class.java).apply {
            action = TimerForegroundService.ACTION_STOP
        }
        context.startService(intent)
    }

    actual fun pause() {
        val intent = Intent(context, TimerForegroundService::class.java).apply {
            action = TimerForegroundService.ACTION_PAUSE
        }
        context.startService(intent)
    }

    actual fun resume() {
        val intent = Intent(context, TimerForegroundService::class.java).apply {
            action = TimerForegroundService.ACTION_RESUME
        }
        context.startService(intent)
    }
}
