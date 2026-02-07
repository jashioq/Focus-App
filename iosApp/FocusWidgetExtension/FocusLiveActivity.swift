import ActivityKit
import SwiftUI
import WidgetKit

struct FocusLiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: FocusActivityAttributes.self) { context in
            // Lock Screen / Banner view
            HStack {
                Spacer()
                timerView(state: context.state)
                    .font(.system(size: 48, weight: .bold, design: .monospaced))
                    .foregroundColor(.white)
                Spacer()
            }
            .padding()
            .activityBackgroundTint(Color.black.opacity(0.8))
            .activitySystemActionForegroundColor(Color.white)

        } dynamicIsland: { context in
            DynamicIsland {
                DynamicIslandExpandedRegion(.center) {
                    timerView(state: context.state)
                        .font(.system(size: 36, weight: .bold, design: .monospaced))
                }

                DynamicIslandExpandedRegion(.leading) { EmptyView() }
                DynamicIslandExpandedRegion(.trailing) { EmptyView() }
                DynamicIslandExpandedRegion(.bottom) { EmptyView() }

            } compactLeading: {
                timerView(state: context.state)
                    .font(.system(.caption, design: .monospaced))
                    .fontWeight(.semibold)

            } compactTrailing: {
                EmptyView()

            } minimal: {
                timerView(state: context.state)
                    .font(.system(.caption2, design: .monospaced))
            }
        }
    }

    @ViewBuilder
    private func timerView(state: FocusActivityAttributes.ContentState) -> some View {
        if state.isPaused, let pauseDate = state.pauseDate {
            let remaining = Int(state.endDate.timeIntervalSince(pauseDate))
            let m = max(remaining, 0) / 60
            let s = max(remaining, 0) % 60
            Text(String(format: "%02d:%02d", m, s))
        } else if Date() < state.endDate {
            Text(timerInterval: Date()...state.endDate, countsDown: true, showsHours: false)
        } else {
            Text("00:00")
        }
    }
}
