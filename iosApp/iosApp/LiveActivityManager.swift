import ActivityKit
import ComposeApp
import Foundation

@objc public class LiveActivityManager: NSObject {
    @objc public static let shared = LiveActivityManager()

    private var toggleCallback: (() -> Void)?

    private override init() {
        super.init()
    }

    private var currentActivity: Activity<FocusActivityAttributes>? {
        return Activity<FocusActivityAttributes>.activities.first {
            $0.activityState == .active || $0.activityState == .stale
        }
    }

    func isActivityActive() -> Bool {
        return currentActivity != nil
    }

    func startOrUpdateActivity(totalSeconds: Int, secondsElapsed: Int, isPaused: Bool) {
        guard ActivityAuthorizationInfo().areActivitiesEnabled else { return }

        let remaining = totalSeconds - secondsElapsed
        let now = Date()
        let endDate = now.addingTimeInterval(TimeInterval(remaining))
        let pauseDate: Date? = isPaused ? now : nil
        let staleDate: Date? = isPaused ? nil : endDate

        let state = FocusActivityAttributes.ContentState(
            endDate: endDate,
            totalSeconds: totalSeconds,
            isPaused: isPaused,
            pauseDate: pauseDate,
            needsSync: false
        )

        if let existing = currentActivity {
            Task {
                await existing.update(
                    ActivityContent(state: state, staleDate: staleDate)
                )
            }
        } else {
            let attributes = FocusActivityAttributes()
            do {
                let activity = try Activity<FocusActivityAttributes>.request(
                    attributes: attributes,
                    content: ActivityContent(state: state, staleDate: staleDate),
                    pushType: nil
                )
                observeDismissal(activity)
            } catch {
                print("Error starting Live Activity: \(error)")
            }
        }
    }

    @objc public func stopActivity() {
        for activity in Activity<FocusActivityAttributes>.activities {
            Task {
                await activity.end(nil, dismissalPolicy: .immediate)
            }
        }
    }

    // MARK: - Activity State (for foreground sync)

    struct ActivityState {
        let isPaused: Bool
        let secondsElapsed: Int
    }

    func getActivityState() -> ActivityState? {
        guard let activity = currentActivity else { return nil }
        let state = activity.content.state
        let now = Date()

        let remaining: Int
        if state.isPaused, let pauseDate = state.pauseDate {
            remaining = Int(state.endDate.timeIntervalSince(pauseDate))
        } else {
            remaining = Int(state.endDate.timeIntervalSince(now))
        }

        let elapsed = state.totalSeconds - max(remaining, 0)
        return ActivityState(isPaused: state.isPaused, secondsElapsed: elapsed)
    }

    // MARK: - Toggle (Darwin notification from widget intent)

    func setToggleCallback(_ callback: @escaping () -> Void) {
        self.toggleCallback = callback
    }

    func startListeningForToggle() {
        let center = CFNotificationCenterGetDarwinNotifyCenter()
        CFNotificationCenterAddObserver(
            center,
            Unmanaged.passUnretained(self).toOpaque(),
            { _, observer, _, _, _ in
                guard let observer = observer else { return }
                let manager = Unmanaged<LiveActivityManager>.fromOpaque(observer).takeUnretainedValue()
                DispatchQueue.main.async {
                    manager.toggleCallback?()
                }
            },
            "com.jan.focus.timerToggled" as CFString,
            nil,
            .deliverImmediately
        )
    }

    // MARK: - Dismissal observation

    private func observeDismissal(_ activity: Activity<FocusActivityAttributes>) {
        Task {
            for await state in activity.activityStateUpdates {
                if state == .dismissed || state == .ended {
                    LiveActivityBridgeKt.notifyActivityDismissed()
                    break
                }
            }
        }
    }
}
