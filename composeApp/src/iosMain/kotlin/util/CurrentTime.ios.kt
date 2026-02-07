package util

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun currentTimeSeconds(): Double = NSDate().timeIntervalSince1970
