package data.dataSource.appState

import domain.model.AppFocusState
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationWillEnterForegroundNotification
import platform.darwin.NSObjectProtocol

actual class AppStateObserver {
    private var foregroundObserver: NSObjectProtocol? = null
    private var backgroundObserver: NSObjectProtocol? = null

    actual fun startObserving(onStateChange: (AppFocusState) -> Unit) {
        foregroundObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIApplicationWillEnterForegroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            onStateChange(AppFocusState.Foreground)
        }

        backgroundObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            onStateChange(AppFocusState.Background)
        }
    }

    actual fun stopObserving() {
        foregroundObserver?.let {
            NSNotificationCenter.defaultCenter.removeObserver(it)
        }
        backgroundObserver?.let {
            NSNotificationCenter.defaultCenter.removeObserver(it)
        }
        foregroundObserver = null
        backgroundObserver = null
    }
}
