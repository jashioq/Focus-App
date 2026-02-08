import Foundation
import ComposeApp

@objc public class LiveActivityBridge: NSObject {
    @objc public static func register() {
        LiveActivityBridgeKt.registerLiveActivityCallbacks(
            onStartOrUpdate: { totalSeconds, secondsElapsed, isPaused in
                LiveActivityManager.shared.startOrUpdateActivity(
                    totalSeconds: totalSeconds.intValue,
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
            onGetActivityState: {
                guard let state = LiveActivityManager.shared.getActivityState() else {
                    return nil
                }
                return KotlinPair(first: KotlinBoolean(value: state.isPaused), second: KotlinInt(value: Int32(state.secondsElapsed)))
            },
            onToggle: { callback in
                LiveActivityManager.shared.setToggleCallback {
                    callback()
                }
                LiveActivityManager.shared.startListeningForToggle()
            }
        )
    }
}
