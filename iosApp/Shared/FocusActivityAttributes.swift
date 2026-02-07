import ActivityKit
import Foundation

struct FocusActivityAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        var endDate: Date
        var isPaused: Bool
        var pauseDate: Date?
        var blockSeconds: String
        var blockModes: String
        var secondsElapsed: Int
        var lastUpdated: Double
    }
}
