import ActivityKit
import Foundation

@objc public class LiveActivityManager: NSObject {
    @objc public static let shared = LiveActivityManager()
    private var currentActivity: Activity<FocusActivityAttributes>?

    private override init() {
        super.init()
    }

    @objc public func startActivity(leftText: String, rightText: String) {
        guard ActivityAuthorizationInfo().areActivitiesEnabled else {
            print("Live Activities are not enabled")
            return
        }

        stopActivity()

        let attributes = FocusActivityAttributes()
        let state = FocusActivityAttributes.ContentState(leftText: leftText, rightText: rightText)

        do {
            currentActivity = try Activity<FocusActivityAttributes>.request(
                attributes: attributes,
                content: .init(state: state, staleDate: nil),
                pushType: nil
            )
            print("Live Activity started with id: \(currentActivity?.id ?? "unknown")")
        } catch {
            print("Error starting Live Activity: \(error.localizedDescription)")
        }
    }

    @objc public func stopActivity() {
        guard let activity = currentActivity else { return }

        Task {
            await activity.end(nil, dismissalPolicy: .immediate)
            print("Live Activity ended")
        }

        currentActivity = nil
    }
}
