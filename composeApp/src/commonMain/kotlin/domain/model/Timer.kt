package domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Timer(
    val sequence: List<TimerBlock>,
    val secondsElapsed: Int,
    val isPaused: Boolean,
) {
    @Transient
    val totalTime: Int = sequence.sumOf { it.seconds }

    fun getCurrentBlock(): BlockPosition? {
        var accumulatedSeconds = 0
        for ((index, block) in sequence.withIndex()) {
            val blockStart = accumulatedSeconds
            accumulatedSeconds += block.seconds
            if (secondsElapsed < accumulatedSeconds) {
                return BlockPosition(
                    block = block,
                    index = index,
                    secondsInBlock = secondsElapsed - blockStart,
                    blockStartSeconds = blockStart,
                )
            }
        }
        return null
    }
}

data class BlockPosition(
    val block: TimerBlock,
    val index: Int,
    val secondsInBlock: Int,
    val blockStartSeconds: Int,
)

@Serializable
enum class TimerMode {
    FOCUS,
    BREAK
}

@Serializable
data class TimerBlock(
    val mode: TimerMode,
    val seconds: Int,
)
