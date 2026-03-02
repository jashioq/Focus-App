package domain.model

data class TimerSession(
    val id: Long? = null,
    val taskId: Long,
    val startDate: String,
    val sequence: List<TimerBlock>,
    val secondsCompleted: Int = 0,
) {
    val totalFocusSeconds: Int
        get() = sequence.filter { it.mode == TimerMode.FOCUS }.sumOf { it.seconds }
    val isComplete: Boolean
        get() = secondsCompleted >= sequence.sumOf { it.seconds }
}
