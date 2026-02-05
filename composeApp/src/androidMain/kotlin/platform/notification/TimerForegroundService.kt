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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import platform.model.TimerMode

class TimerForegroundService : Service() {

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "timer_channel"

        const val ACTION_START = "platform.notification.ACTION_START"
        const val ACTION_UPDATE = "platform.notification.ACTION_UPDATE"
        const val ACTION_STOP = "platform.notification.ACTION_STOP"
        const val ACTION_DISMISSED = "platform.notification.ACTION_DISMISSED"

        const val ACTION_TOGGLE_REQUEST = "platform.notification.ACTION_TOGGLE_REQUEST"

        const val EXTRA_MODE = "extra_mode"
        const val EXTRA_TIME_LEFT = "extra_time_left"
        const val EXTRA_TOTAL_TIME = "extra_total_time"
        const val EXTRA_IS_PAUSED = "extra_is_paused"

        const val BROADCAST_PAUSE_REQUESTED = "com.jan.focus.PAUSE_REQUESTED"
        const val BROADCAST_RESUME_REQUESTED = "com.jan.focus.RESUME_REQUESTED"
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var countdownJob: Job? = null

    private var mode: String = TimerMode.FOCUS.name
    private var timeLeft: Int = 0
    private var totalTime: Int = 0
    private var isPaused: Boolean = false
    private var isInForeground: Boolean = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                mode = intent.getStringExtra(EXTRA_MODE) ?: TimerMode.FOCUS.name
                timeLeft = intent.getIntExtra(EXTRA_TIME_LEFT, 0)
                totalTime = intent.getIntExtra(EXTRA_TOTAL_TIME, 0)
                isPaused = intent.getBooleanExtra(EXTRA_IS_PAUSED, false)

                startForeground(NOTIFICATION_ID, buildNotification())
                isInForeground = true

                if (!isPaused) {
                    startCountdown()
                }
            }
            ACTION_UPDATE -> {
                if (!isInForeground) return START_STICKY

                intent.getStringExtra(EXTRA_MODE)?.let { mode = it }
                if (intent.hasExtra(EXTRA_TIME_LEFT)) {
                    timeLeft = intent.getIntExtra(EXTRA_TIME_LEFT, timeLeft)
                }
                if (intent.hasExtra(EXTRA_TOTAL_TIME)) {
                    totalTime = intent.getIntExtra(EXTRA_TOTAL_TIME, totalTime)
                }
                if (intent.hasExtra(EXTRA_IS_PAUSED)) {
                    val wasPaused = isPaused
                    isPaused = intent.getBooleanExtra(EXTRA_IS_PAUSED, isPaused)

                    if (wasPaused && !isPaused) {
                        startCountdown()
                    } else if (!wasPaused && isPaused) {
                        countdownJob?.cancel()
                    }
                }

                notificationManager.notify(NOTIFICATION_ID, buildNotification())
            }
            ACTION_TOGGLE_REQUEST -> {
                if (isPaused) {
                    sendBroadcast(Intent(BROADCAST_RESUME_REQUESTED).setPackage(packageName))
                } else {
                    sendBroadcast(Intent(BROADCAST_PAUSE_REQUESTED).setPackage(packageName))
                }
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
            while (timeLeft > 0 && !isPaused) {
                delay(1000)
                timeLeft--
                if (isInForeground) {
                    notificationManager.notify(NOTIFICATION_ID, buildNotification())
                }
            }
        }
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

        val minutes = timeLeft / 60
        val seconds = timeLeft % 60
        val timerText = String.format("%02d:%02d", minutes, seconds)
        remoteViews.setTextViewText(R.id.tv_timer, timerText)

        if (isPaused) {
            remoteViews.setFloat(R.id.tv_timer, "setAlpha", 0.5f)
        } else {
            remoteViews.setFloat(R.id.tv_timer, "setAlpha", 1.0f)
        }

        val modeText = if (mode == TimerMode.BREAK.name) "Take a break" else "Focus!"
        remoteViews.setTextViewText(R.id.tv_mode, modeText)

        val progress = if (totalTime > 0) (totalTime - timeLeft) * 100 / totalTime else 0
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
