import Foundation
import ComposeApp

@objc public class LiveActivityBridge: NSObject {
    @objc public static func register() {
        LiveActivityBridgeKt.registerLiveActivityCallbacks(
            onStartOrUpdate: { blockSeconds, blockModes, secondsElapsed, isPaused in
                LiveActivityManager.shared.startOrUpdateActivity(
                    blockSeconds: blockSeconds,
                    blockModes: blockModes,
                    secondsElapsed: secondsElapsed.intValue,
                    isPaused: isPaused.boolValue
                )
            },
            onStop: {
                LiveActivityManager.shared.stopActivity()
            },
            onIsActive: {
                return KotlinBoolean(value: LiveActivityManager.shared.isActivityActive())
            },
            onScheduleRefresh: { afterSeconds in
                LiveActivityManager.shared.scheduleBlockTransitionRefresh(afterSeconds: afterSeconds.intValue)
            }
        )
    }
}
