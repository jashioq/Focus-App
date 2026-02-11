package domain.repository

import domain.model.Timer
import kotlinx.coroutines.flow.Flow

interface TimerRepository {
    val timerFlow: Flow<Timer?>
    fun start(timer: Timer)
    fun stop()
    fun pause()
    fun resume()
    fun skipBlock()
    fun extendBlock(seconds: Int)
}
