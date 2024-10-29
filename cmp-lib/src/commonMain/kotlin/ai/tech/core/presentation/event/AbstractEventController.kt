package ai.tech.core.presentation.event

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

public abstract class AbstractEventController<T> {
    private val _events = Channel<T>()

    public val events: Flow<T> = _events.receiveAsFlow()

    public suspend fun sendEvent(event: T): Unit = _events.send(event)
}