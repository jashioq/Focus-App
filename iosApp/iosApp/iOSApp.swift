import SwiftUI

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
        .backgroundTask(.appRefresh("com.jan.focus.blockTransition")) {
            // No-op: waking the app is enough for the pending delay() to fire
        }
    }
}