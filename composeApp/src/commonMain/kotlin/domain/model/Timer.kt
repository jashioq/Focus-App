package domain.model

class Timer(
    val sequence: List<TimerBlock>,
    val secondsElapsed: Int,
    val isPaused: Boolean,
) {
    val totalTime =
        this.sequence.sumOf { it.seconds }

    fun getCurrentBlock(): Pair<TimerBlock, Int>? {
        var totalSeconds = sequence.first().seconds
        var previousTotalSeconds = 0

        this.sequence.forEachIndexed { index, block ->
            if (this.secondsElapsed < totalSeconds) {
                val secondsInBlock = this.secondsElapsed - previousTotalSeconds
                return Pair(block, secondsInBlock)
            }

            previousTotalSeconds = totalSeconds
            totalSeconds += block.seconds
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
