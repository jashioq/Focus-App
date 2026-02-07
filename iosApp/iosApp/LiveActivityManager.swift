import ActivityKit
import BackgroundTasks
import ComposeApp
import Foundation

@objc public class LiveActivityManager: NSObject {
    @objc public static let shared = LiveActivityManager()

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

    func startOrUpdateActivity(blockSeconds: String, blockModes: String, secondsElapsed: Int, isPaused: Bool) {
        guard ActivityAuthorizationInfo().areActivitiesEnabled else { return }

        let blockSecArr = blockSeconds.split(separator: ",").compactMap { Int($0) }
        let totalTime = blockSecArr.reduce(0, +)

        // Find current block and remaining seconds in it
        var cumulative = 0
        var blockRemaining = 0
        for blockDuration in blockSecArr {
            cumulative += blockDuration
            if secondsElapsed < cumulative {
                let secondsInBlock = secondsElapsed - (cumulative - blockDuration)
                blockRemaining = blockDuration - secondsInBlock
                break
            }
        }
        if blockRemaining <= 0 {
            blockRemaining = totalTime - secondsElapsed
        }

        let now = Date()
        let lastUpdated = now.timeIntervalSince1970
        let endDate = now.addingTimeInterval(TimeInterval(blockRemaining))
        let pauseDate: Date? = isPaused ? now : nil
        let staleDate: Date? = isPaused ? nil : endDate

        let state = FocusActivityAttributes.ContentState(
            endDate: endDate,
            isPaused: isPaused,
            pauseDate: pauseDate,
            blockSeconds: blockSeconds,
            blockModes: blockModes,
            secondsElapsed: secondsElapsed,
            lastUpdated: lastUpdated
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

    func scheduleBlockTransitionRefresh(afterSeconds: Int) {
        let request = BGAppRefreshTaskRequest(identifier: "com.jan.focus.blockTransition")
        request.earliestBeginDate = Date().addingTimeInterval(TimeInterval(afterSeconds))
        try? BGTaskScheduler.shared.submit(request)
    }

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
