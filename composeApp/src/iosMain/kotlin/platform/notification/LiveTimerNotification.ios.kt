package platform.notification

import domain.model.Timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIApplication
import platform.UIKit.UIBackgroundTaskIdentifier
import platform.UIKit.UIBackgroundTaskInvalid

actual class LiveTimerNotification {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var cachedTimer: Timer? = null
    private var lastUpdatedTimestamp: Double = 0.0
    private var blockTransitionJob: Job? = null
    private var backgroundTaskId: UIBackgroundTaskIdentifier = UIBackgroundTaskInvalid

    actual val timerUpdateFlow: Flow<Timer> = emptyFlow()
    actual val notificationDismissedFlow: Flow<Unit> = LiveActivityBridge.dismissedFlow

    actual fun isNotificationActive(): Boolean = LiveActivityBridge.isActive()

    actual fun start(timer: Timer) {
        cachedTimer = timer
        lastUpdatedTimestamp = NSDate().timeIntervalSince1970
        sendTobridge(timer)
        scheduleBlockTransitionUpdate(timer)
    }

    actual fun stop() {
        cachedTimer = null
        blockTransitionJob?.cancel()
        endBackgroundTime()
        LiveActivityBridge.stop()
    }

    actual fun pause() {
        val timer = cachedTimer ?: return
        val now = NSDate().timeIntervalSince1970
        val elapsed = timer.secondsElapsed + (now - lastUpdatedTimestamp).toInt()
        val pausedTimer = Timer(
            sequence = timer.sequence,
            secondsElapsed = elapsed,
            isPaused = true,
        )
        cachedTimer = pausedTimer
        lastUpdatedTimestamp = now
        blockTransitionJob?.cancel()
        sendTobridge(pausedTimer)
    }

    actual fun resume() {
        val timer = cachedTimer ?: return
        val resumedTimer = Timer(
            sequence = timer.sequence,
            secondsElapsed = timer.secondsElapsed,
            isPaused = false,
        )
        cachedTimer = resumedTimer
        lastUpdatedTimestamp = NSDate().timeIntervalSince1970
        sendTobridge(resumedTimer)
        scheduleBlockTransitionUpdate(resumedTimer)
    }

    private fun sendTobridge(timer: Timer) {
        val blockSeconds = timer.sequence.joinToString(",") { it.seconds.toString() }
        val blockModes = timer.sequence.joinToString(",") { it.mode.name }
        LiveActivityBridge.startOrUpdate(blockSeconds, blockModes, timer.secondsElapsed, timer.isPaused)
    }

    private fun scheduleBlockTransitionUpdate(timer: Timer) {
        blockTransitionJob?.cancel()
        val (currentBlock, secondsInBlock) = timer.getCurrentBlock() ?: return
        val blockRemaining = currentBlock.seconds - secondsInBlock
        if (blockRemaining <= 0) return

        requestBackgroundTime()
        LiveActivityBridge.scheduleBackgroundRefresh(blockRemaining)

        blockTransitionJob = scope.launch {
            delay(blockRemaining * 1000L)
            val now = NSDate().timeIntervalSince1970
            val elapsed = timer.secondsElapsed + (now - lastUpdatedTimestamp).toInt()
            val totalTime = timer.sequence.sumOf { it.seconds }
            if (elapsed >= totalTime) return@launch

            val updatedTimer = Timer(
                sequence = timer.sequence,
                secondsElapsed = elapsed,
                isPaused = false,
            )
            cachedTimer = updatedTimer
            lastUpdatedTimestamp = now
            sendTobridge(updatedTimer)
            scheduleBlockTransitionUpdate(updatedTimer)
        }
    }

    private fun requestBackgroundTime() {
        if (backgroundTaskId != UIBackgroundTaskInvalid) return
        backgroundTaskId = UIApplication.sharedApplication.beginBackgroundTaskWithExpirationHandler {
            UIApplication.sharedApplication.endBackgroundTask(backgroundTaskId)
            backgroundTaskId = UIBackgroundTaskInvalid
        }
    }

    private fun endBackgroundTime() {
        if (backgroundTaskId == UIBackgroundTaskInvalid) return
        UIApplication.sharedApplication.endBackgroundTask(backgroundTaskId)
        backgroundTaskId = UIBackgroundTaskInvalid
    }
}
