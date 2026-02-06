import ActivityKit
import Foundation

@objc public class LiveActivityManager: NSObject {
    @objc public static let shared = LiveActivityManager()

    private override init() {
        super.init()
    }

    private var allActivities: [Activity<FocusActivityAttributes>] {
        return Activity<FocusActivityAttributes>.activities
    }

    @objc public func startActivity(leftText: String, rightText: String) {
        guard ActivityAuthorizationInfo().areActivitiesEnabled else { return }

        let hasRunning = allActivities.contains { $0.activityState == .active || $0.activityState == .stale }
        if hasRunning { return }

        let attributes = FocusActivityAttributes()
        let state = FocusActivityAttributes.ContentState(leftText: leftText, rightText: rightText)

        do {
            _ = try Activity<FocusActivityAttributes>.request(
                attributes: attributes,
                content: .init(state: state, staleDate: nil),
                pushType: nil
            )
        } catch {
            print("Error starting Live Activity: \(error)")
        }
    }

    @objc public func stopActivity() {
        for activity in allActivities {
            Task {
                await activity.end(nil, dismissalPolicy: .immediate)
            }
        }
    }
}
