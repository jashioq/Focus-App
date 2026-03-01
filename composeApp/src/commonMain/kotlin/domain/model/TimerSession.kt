package domain.model

data class TimerSession(
    val id: Long? = null,
    val taskId: Long,
    val startDate: String,
    val sequence: List<TimerBlock>,
)
