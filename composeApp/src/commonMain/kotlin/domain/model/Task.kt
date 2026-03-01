package domain.model

data class Task(
    val id: Long? = null,
    val name: String,
    val description: String,
    val color: String,
    val startDate: String,
    val endDate: String,
    val timerSessions: List<TimerSession> = emptyList(),
)
