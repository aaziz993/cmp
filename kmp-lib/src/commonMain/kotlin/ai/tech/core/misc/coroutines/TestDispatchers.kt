package ai.tech.core.misc.coroutines

import kotlinx.coroutines.CoroutineDispatcher

public class TestDispatchers(
    public val testDispatcher: CoroutineDispatcher
) : DispatcherProvider {
    override val main: CoroutineDispatcher
        get() = testDispatcher
    override val io: CoroutineDispatcher
        get() = testDispatcher
    override val unconfined: CoroutineDispatcher
        get() = testDispatcher
}