import Foundation
import ComposeApp

@objc public class LiveActivityBridge: NSObject {
    @objc public static func register() {
        LiveActivityBridgeKt.registerLiveActivityCallbacks(
            onStart: { leftText, rightText in
                LiveActivityManager.shared.startActivity(leftText: leftText, rightText: rightText)
            },
            onStop: {
                LiveActivityManager.shared.stopActivity()
            }
        )
    }
}
