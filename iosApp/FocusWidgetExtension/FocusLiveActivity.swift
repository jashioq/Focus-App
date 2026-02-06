import ActivityKit
import SwiftUI
import WidgetKit

struct FocusLiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: FocusActivityAttributes.self) { context in
            // Lock Screen / Banner view
            HStack {
                Text(context.state.leftText)
                    .font(.headline)
                Spacer()
                Text(context.state.rightText)
                    .font(.headline)
            }
            .padding()
            .activityBackgroundTint(Color.black.opacity(0.8))
            .activitySystemActionForegroundColor(Color.white)

        } dynamicIsland: { context in
            DynamicIsland {
                // Expanded view
                DynamicIslandExpandedRegion(.leading) {
                    Text(context.state.leftText)
                        .font(.title2)
                        .fontWeight(.semibold)
                }

                DynamicIslandExpandedRegion(.trailing) {
                    Text(context.state.rightText)
                        .font(.title2)
                        .fontWeight(.semibold)
                }

                DynamicIslandExpandedRegion(.center) {
                    EmptyView()
                }

                DynamicIslandExpandedRegion(.bottom) {
                    EmptyView()
                }

            } compactLeading: {
                Text(context.state.leftText)
                    .font(.caption)
                    .fontWeight(.semibold)

            } compactTrailing: {
                Text(context.state.rightText)
                    .font(.caption)
                    .fontWeight(.semibold)

            } minimal: {
                Text(context.state.leftText)
                    .font(.caption2)
            }
        }
    }
}
