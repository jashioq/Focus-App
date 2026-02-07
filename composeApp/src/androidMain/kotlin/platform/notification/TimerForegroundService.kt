package platform.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.jan.focus.MainActivity
import com.jan.focus.R
import domain.model.TimerMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerForegroundService : Service() {

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "timer_channel"

        const val ACTION_START = "platform.notification.ACTION_START"
        const val ACTION_PAUSE = "platform.notification.ACTION_PAUSE"
        const val ACTION_RESUME = "platform.notification.ACTION_RESUME"
        const val ACTION_STOP = "platform.notification.ACTION_STOP"
        const val ACTION_DISMISSED = "platform.notification.ACTION_DISMISSED"
        const val ACTION_TOGGLE_REQUEST = "platform.notification.ACTION_TOGGLE_REQUEST"

        const val EXTRA_BLOCK_SECONDS = "extra_block_seconds"
        const val EXTRA_BLOCK_MODES = "extra_block_modes"
        const val EXTRA_SECONDS_ELAPSED = "extra_seconds_elapsed"
        const val EXTRA_IS_PAUSED = "extra_is_paused"

        const val BROADCAST_TIMER_UPDATE = "com.jan.focus.TIMER_UPDATE"
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var countdownJob: Job? = null

    private var blockSeconds: IntArray = intArrayOf()
    private var blockModes: Array<String> = emptyArray()
    private var secondsElapsed: Int = 0
    private var isPaused: Boolean = false
    private var isInForeground: Boolean = false

    private val totalTime: Int
        get() = blockSeconds.sum()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                blockSeconds = intent.getIntArrayExtra(EXTRA_BLOCK_SECONDS) ?: intArrayOf()
                blockModes = intent.getStringArrayExtra(EXTRA_BLOCK_MODES) ?: emptyArray()
                secondsElapsed = intent.getIntExtra(EXTRA_SECONDS_ELAPSED, 0)
                isPaused = intent.getBooleanExtra(EXTRA_IS_PAUSED, false)

                if (isInForeground) {
                    notificationManager.notify(NOTIFICATION_ID, buildNotification())
                } else {
                    startForeground(NOTIFICATION_ID, buildNotification())
                    isInForeground = true
                }

                countdownJob?.cancel()
                if (!isPaused) {
                    startCountdown()
                }
            }
            ACTION_PAUSE -> {
                if (!isInForeground) return START_STICKY
                isPaused = true
                countdownJob?.cancel()
                notificationManager.notify(NOTIFICATION_ID, buildNotification())
            }
            ACTION_RESUME -> {
                if (!isInForeground) return START_STICKY
                isPaused = false
                notificationManager.notify(NOTIFICATION_ID, buildNotification())
                startCountdown()
            }
            ACTION_TOGGLE_REQUEST -> {
                if (!isInForeground) return START_STICKY
                isPaused = !isPaused
                if (isPaused) {
                    countdownJob?.cancel()
                } else {
                    startCountdown()
                }
                notificationManager.notify(NOTIFICATION_ID, buildNotification())
                broadcastTimerUpdate()
            }
            ACTION_DISMISSED -> {
                isInForeground = false
                countdownJob?.cancel()
            }
            ACTION_STOP -> {
                countdownJob?.cancel()
                stopForeground(STOP_FOREGROUND_REMOVE)
                isInForeground = false
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = serviceScope.launch {
            while (secondsElapsed < totalTime && !isPaused) {
                delay(1000)
                secondsElapsed++
                if (isInForeground) {
                    if (secondsElapsed >= totalTime) {
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        isInForeground = false
                        stopSelf()
                        return@launch
                    }
                    notificationManager.notify(NOTIFICATION_ID, buildNotification())
                }
            }
        }
    }

    private fun broadcastTimerUpdate() {
        val intent = Intent(BROADCAST_TIMER_UPDATE).apply {
            setPackage(packageName)
            putExtra(EXTRA_BLOCK_SECONDS, blockSeconds)
            putExtra(EXTRA_BLOCK_MODES, blockModes)
            putExtra(EXTRA_SECONDS_ELAPSED, secondsElapsed)
            putExtra(EXTRA_IS_PAUSED, isPaused)
        }
        sendBroadcast(intent)
    }

    private fun getCurrentBlock(): Pair<String, Int>? {
        var cumulative = 0
        for (i in blockSeconds.indices) {
            cumulative += blockSeconds[i]
            if (secondsElapsed < cumulative) {
                val secondsInBlock = secondsElapsed - (cumulative - blockSeconds[i])
                return Pair(blockModes[i], secondsInBlock)
            }
        }
        return null
    }

    private val notificationManager: NotificationManager
        get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Timer",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "Focus timer notifications"
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val remoteViews = RemoteViews(packageName, R.layout.notification_timer)

        val currentBlock = getCurrentBlock()
        val currentMode = currentBlock?.first ?: TimerMode.FOCUS.name
        val secondsInBlock = currentBlock?.second ?: 0

        var blockIndex = 0
        var cumulative = 0
        for (i in blockSeconds.indices) {
            cumulative += blockSeconds[i]
            if (secondsElapsed < cumulative) {
                blockIndex = i
                break
            }
        }
        val blockTotalSeconds = blockSeconds.getOrElse(blockIndex) { 1 }
        val timeLeftInBlock = blockTotalSeconds - secondsInBlock

        val minutes = timeLeftInBlock / 60
        val seconds = timeLeftInBlock % 60
        val timerText = String.format("%02d:%02d", minutes, seconds)
        remoteViews.setTextViewText(R.id.tv_timer, timerText)

        if (isPaused) {
            remoteViews.setFloat(R.id.tv_timer, "setAlpha", 0.5f)
        } else {
            remoteViews.setFloat(R.id.tv_timer, "setAlpha", 1.0f)
        }

        val modeText = if (currentMode == TimerMode.BREAK.name) "Take a break" else "Focus!"
        remoteViews.setTextViewText(R.id.tv_mode, modeText)

        val progress = if (blockTotalSeconds > 0) secondsInBlock * 100 / blockTotalSeconds else 0
        remoteViews.setProgressBar(R.id.progress_bar, 100, progress, false)

        val toggleIcon = if (isPaused) R.drawable.ic_play else R.drawable.ic_pause
        remoteViews.setImageViewResource(R.id.btn_toggle, toggleIcon)

        val toggleIntent = Intent(this, TimerForegroundService::class.java).apply {
            action = ACTION_TOGGLE_REQUEST
        }
        val togglePendingIntent = PendingIntent.getService(
            this,
            0,
            toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        remoteViews.setOnClickPendingIntent(R.id.btn_toggle, togglePendingIntent)

        val contentIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val deleteIntent = Intent(this, TimerForegroundService::class.java).apply {
            action = ACTION_DISMISSED
        }
        val deletePendingIntent = PendingIntent.getService(
            this,
            1,
            deleteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setContentIntent(contentPendingIntent)
            .setDeleteIntent(deletePendingIntent)
            .setSilent(true)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setColor(0xFF000000.toInt())
            .setColorized(true)
            .build()
    }
}
