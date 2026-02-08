import ActivityKit
import AppIntents
import SwiftUI
import WidgetKit

struct FocusLiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: FocusActivityAttributes.self) { context in
            // Lock Screen / Notification Center view
            VStack(spacing: 8) {
                HStack(alignment: .center, spacing: 16) {
                    VStack(alignment: .leading, spacing: 8) {
                        timerView(state: context.state)
                            .font(.system(size: 48, weight: .bold, design: .monospaced))
                            .foregroundColor(.white)

                        progressBar(state: context.state)
                    }

                    if !context.state.needsSync {
                        Button(intent: ToggleTimerIntent()) {
                            Image(systemName: context.state.isPaused ? "play.fill" : "pause.fill")
                                .font(.title2)
                                .foregroundColor(.white)
                                .frame(width: 44, height: 44)
                                .background(Color.white.opacity(0.2))
                                .clipShape(Circle())
                        }
                        .buttonStyle(.plain)
                    }
                }

                if context.state.needsSync {
                    Text("Tap to sync")
                        .font(.caption)
                        .foregroundColor(.white.opacity(0.7))
                }
            }
            .padding()
            .activityBackgroundTint(Color.black.opacity(0.8))
            .activitySystemActionForegroundColor(Color.white)

        } dynamicIsland: { context in
            DynamicIsland {
                DynamicIslandExpandedRegion(.leading) {
                    timerView(state: context.state)
                        .font(.system(size: 24, weight: .bold, design: .monospaced))
                }

                DynamicIslandExpandedRegion(.trailing) {
                    if !context.state.needsSync {
                        Button(intent: ToggleTimerIntent()) {
                            Image(systemName: context.state.isPaused ? "play.fill" : "pause.fill")
                                .font(.title3)
                        }
                        .tint(.white)
                    }
                }

                DynamicIslandExpandedRegion(.center) { EmptyView() }

                DynamicIslandExpandedRegion(.bottom) {
                    if context.state.needsSync {
                        Text("Tap to sync")
                            .font(.caption)
                            .foregroundColor(.white.opacity(0.7))
                    }
                }

            } compactLeading: {
                timerView(state: context.state)
                    .font(.system(.caption, design: .monospaced))
                    .fontWeight(.semibold)

            } compactTrailing: {
                EmptyView()
            } minimal: {
                timerView(state: context.state)
                    .font(.system(size: 12, weight: .bold, design: .monospaced))
            }
        }
    }

    @ViewBuilder
    private func timerView(state: FocusActivityAttributes.ContentState) -> some View {
        if state.needsSync {
            Text("--:--")
        } else if state.isPaused, let pauseDate = state.pauseDate {
            let remaining = Int(state.endDate.timeIntervalSince(pauseDate))
            let m = max(remaining, 0) / 60
            let s = max(remaining, 0) % 60
            Text("\(m):\(s)")
        } else if Date() < state.endDate {
            Text(timerInterval: Date()...state.endDate, countsDown: true, showsHours: false)
        } else {
            Text("Done")
        }
    }

    @ViewBuilder
    private func progressBar(state: FocusActivityAttributes.ContentState) -> some View {
        if state.isPaused, let pauseDate = state.pauseDate {
            let remaining = max(state.endDate.timeIntervalSince(pauseDate), 0)
            let elapsed = Double(state.totalSeconds) - remaining
            let progress = state.totalSeconds > 0 ? elapsed / Double(state.totalSeconds) : 0
            ProgressView(value: min(max(progress, 0), 1))
                .tint(.white)
                .labelsHidden()
        } else {
            let start = state.endDate.addingTimeInterval(-Double(state.totalSeconds))
            ProgressView(timerInterval: start...state.endDate, countsDown: false)
                .tint(.white)
                .labelsHidden()
        }
    }
}
