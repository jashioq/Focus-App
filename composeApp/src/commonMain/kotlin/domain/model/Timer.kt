package domain.model

class Timer(
    val sequence: List<TimerBlock>,
    val secondsElapsed: Int,
    val isPaused: Boolean,
) {
    val totalTime =
        this.sequence.sumOf { it.seconds }

    fun getCurrentBlock(): Pair<TimerBlock, Int>? {
        var accumulatedSeconds = 0
        for (block in sequence) {
            val blockStart = accumulatedSeconds
            accumulatedSeconds += block.seconds
            if (secondsElapsed < accumulatedSeconds) {
                return Pair(block, secondsElapsed - blockStart)
            }
        }
        return null
    }
}

enum class TimerMode {
    FOCUS,
    BREAK
}

data class TimerBlock(
    val mode: TimerMode,
    val seconds: Int,
)
