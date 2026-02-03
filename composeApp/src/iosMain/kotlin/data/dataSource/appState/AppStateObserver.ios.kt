package data.dataSource.appState

import domain.model.AppFocusState
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationProtectedDataDidBecomeAvailable
import platform.UIKit.UIApplicationProtectedDataWillBecomeUnavailable
import platform.UIKit.UIApplicationState
import platform.UIKit.UIApplicationWillEnterForegroundNotification
import platform.darwin.NSObjectProtocol

actual class AppStateObserver {
    private var foregroundObserver: NSObjectProtocol? = null
    private var backgroundObserver: NSObjectProtocol? = null
    private var protectedDataWillBecomeUnavailableObserver: NSObjectProtocol? = null
    private var protectedDataDidBecomeAvailableObserver: NSObjectProtocol? = null

    private var isDeviceLocking = false

    actual fun startObserving(onStateChange: (AppFocusState) -> Unit) {
        // Emit current state immediately (matches Android's ProcessLifecycleOwner behavior)
        // UIApplicationStateInactive = transitional state (launching/interrupted), treat as Foreground
        // Only UIApplicationStateBackground = truly backgrounded
        val currentState = when (UIApplication.sharedApplication.applicationState) {
            UIApplicationState.UIApplicationStateBackground -> AppFocusState.Background
            else -> AppFocusState.Foreground // Active or Inactive (transitional)
        }
        onStateChange(currentState)

        // Detect when device is about to lock (screen turning off)
        protectedDataWillBecomeUnavailableObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIApplicationProtectedDataWillBecomeUnavailable,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            isDeviceLocking = true
        }

        // Detect when device unlocks
        protectedDataDidBecomeAvailableObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIApplicationProtectedDataDidBecomeAvailable,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            isDeviceLocking = false
        }

        foregroundObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIApplicationWillEnterForegroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            isDeviceLocking = false
            onStateChange(AppFocusState.Foreground)
        }

        backgroundObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            // Only emit Background if device is NOT locking (user switched apps)
            // If device is locking, user just locked the phone - don't emit Background
            if (!isDeviceLocking) {
                onStateChange(AppFocusState.Background)
            }
        }
    }

    actual fun stopObserving() {
        foregroundObserver?.let {
            NSNotificationCenter.defaultCenter.removeObserver(it)
        }
        backgroundObserver?.let {
            NSNotificationCenter.defaultCenter.removeObserver(it)
        }
        protectedDataWillBecomeUnavailableObserver?.let {
            NSNotificationCenter.defaultCenter.removeObserver(it)
        }
        protectedDataDidBecomeAvailableObserver?.let {
            NSNotificationCenter.defaultCenter.removeObserver(it)
        }
        foregroundObserver = null
        backgroundObserver = null
        protectedDataWillBecomeUnavailableObserver = null
        protectedDataDidBecomeAvailableObserver = null
    }
}
