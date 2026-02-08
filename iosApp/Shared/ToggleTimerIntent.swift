import ActivityKit
import AppIntents

struct ToggleTimerIntent: LiveActivityIntent {
    static var title: LocalizedStringResource = "Toggle Timer"
    static var isDiscoverable: Bool = false
    static var openAppWhenRun: Bool = false

    func perform() async throws -> some IntentResult {
        // Snapshot current state before signaling
        let activity = Activity<FocusActivityAttributes>.activities.first {
            $0.activityState == .active || $0.activityState == .stale
        }
        let wasPaused = activity?.content.state.isPaused

        // Post Darwin notification — KMP layer will handle pause/resume through TimerRepository
        let center = CFNotificationCenterGetDarwinNotifyCenter()
        CFNotificationCenterPostNotification(center, CFNotificationName("com.jan.focus.timerToggled" as CFString), nil, nil, true)

        // Wait 3 seconds, then check if the app responded
        try await Task.sleep(for: .seconds(3))

        if let activity = Activity<FocusActivityAttributes>.activities.first(where: {
            $0.activityState == .active || $0.activityState == .stale
        }) {
            let currentState = activity.content.state
            if currentState.isPaused == wasPaused && !currentState.needsSync {
                // App didn't respond — show sync message
                let syncState = FocusActivityAttributes.ContentState(
                    endDate: currentState.endDate,
                    totalSeconds: currentState.totalSeconds,
                    isPaused: currentState.isPaused,
                    pauseDate: currentState.pauseDate,
                    needsSync: true
                )
                await activity.update(
                    ActivityContent(state: syncState, staleDate: nil),
                    alertConfiguration: .init(title: "Tap to sync", body: "", sound: .default)
                )
            }
        }

        return .result()
    }
}
