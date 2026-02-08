import ActivityKit
import Foundation

struct FocusActivityAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        var endDate: Date
        var totalSeconds: Int
        var isPaused: Bool
        var pauseDate: Date?
        var needsSync: Bool = false
    }
}
