package util

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@OptIn(ExperimentalCoroutinesApi::class)
open class ViewModelTest {
    protected open val testDispatcher = UnconfinedTestDispatcher()
    protected open val logger = mock<Logger>()

    @BeforeTest
    fun beforeTest() {
        Dispatchers.setMain(testDispatcher)
        every {
            logger.d(any(), any())
        } returns Unit
        prepare()
    }

    @AfterTest
    fun afterTest() {
        Dispatchers.resetMain()
        dismantle()
    }

    protected open fun prepare() {}
    protected open fun dismantle() {}
}
