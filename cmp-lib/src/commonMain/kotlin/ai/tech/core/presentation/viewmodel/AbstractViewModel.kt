@file:OptIn(ExperimentalForInheritanceCoroutinesApi::class)

package ai.tech.core.presentation.viewmodel

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.client.mutablePager
import ai.tech.core.data.crud.client.pager
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import ai.tech.core.misc.type.multiple.map
import ai.tech.core.misc.type.multiple.model.OnetimeWhileSubscribed
import ai.tech.core.misc.type.multiple.model.RestartableMutableStateFlow
import ai.tech.core.misc.type.multiple.model.RestartableStateFlow
import ai.tech.core.misc.type.multiple.restartableStateIn
import ai.tech.core.presentation.viewmodel.ViewModelState.Success
import ai.tech.core.presentation.viewmodel.model.exception.ViewModelStateException
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.RemoteMediator
import app.cash.paging.cachedIn
import app.cash.paging.createPagingConfig
import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent

public abstract class AbstractViewModel<T : Any>(protected val savedStateHandle: SavedStateHandle) : ViewModel(), KoinComponent {

    public open fun exceptionTransform(exception: Throwable): ViewModelStateException = ViewModelStateException(exception)

    public suspend fun <T : Any> ViewModelState<T>.map(block: suspend () -> T): ViewModelState<T> = try {
        Success(block())
    }
    catch (e: Throwable) {
        toFailure(exceptionTransform(e))
    }

    public suspend fun <T : Any> ViewModelState<T>.mapResult(block: suspend () -> Result<T>): ViewModelState<T> =
        block().fold(
            onSuccess = { Success(it) },
            onFailure = { toFailure(exceptionTransform(it)) },
        )

    public suspend fun <T : Any> ViewModelState<T>.mapEither(block: suspend () -> Either<Throwable, T>): ViewModelState<T> =
        block().fold(
            ifLeft = { toFailure(exceptionTransform(it)) },
            ifRight = { Success(it) },
        )

    public suspend fun <T : Any> ViewModelState<T>.mapRaise(block: suspend Raise<Throwable>.() -> T): ViewModelState<T> =
        mapEither { either<Throwable, T> { block() } }

    public abstract fun action(action: T)

    protected fun <T, R> StateFlow<T>.map(transform: (data: T) -> R): StateFlow<R> =
        map(scope = viewModelScope, transform)

    @OptIn(ExperimentalCoroutinesApi::class)
    protected fun <T, R> StateFlow<T>.map(initialValue: R, transform: suspend (data: T) -> R): StateFlow<R> =
        map(viewModelScope, initialValue, transform)

    protected val <T : Any> Flow<PagingData<T>>.cached: Flow<PagingData<T>>
        get() = cachedIn(viewModelScope)

    protected val <T> Flow<T>.launch: Job
        get() = launchIn(viewModelScope)

    protected fun <T> Flow<T>.viewModelScopeFlow(
        initialValue: T,
        started: SharingStarted = SharingStarted.OnetimeWhileSubscribed(STATE_STARTED_STOP_TIMEOUT_MILLIS),
    ): RestartableStateFlow<T> = restartableStateIn(
        started,
        viewModelScope,
        initialValue,
    )

    protected fun <T : Any> viewModelStateFlow(
        initialValue: ViewModelState<T> = idle(),
        started: SharingStarted = SharingStarted.OnetimeWhileSubscribed(STATE_STARTED_STOP_TIMEOUT_MILLIS),
        block: suspend FlowCollector<ViewModelState<T>>.(ViewModelState<T>) -> Unit
    ): RestartableStateFlow<ViewModelState<T>> = flow { block(initialValue) }.viewModelScopeFlow(initialValue, started)

    protected fun <T : Any> viewModelMutableStateFlow(
        initialValue: ViewModelState<T> = idle(),
        started: SharingStarted = SharingStarted.OnetimeWhileSubscribed(STATE_STARTED_STOP_TIMEOUT_MILLIS),
        block: (suspend MutableStateFlow<ViewModelState<T>>.(ViewModelState<T>) -> ViewModelState<T>)? = null,
    ): RestartableMutableStateFlow<ViewModelState<T>> {
        val mutableStateFlow = MutableStateFlow(initialValue)

        val restartableStateFlow = if (block == null) {
            mutableStateFlow
        }
        else {
            mutableStateFlow.onStart { mutableStateFlow.update { mutableStateFlow.block(it) } }
        }.viewModelScopeFlow(initialValue, started)

        return object : RestartableMutableStateFlow<ViewModelState<T>> by restartableStateFlow {
            override fun restart() = restartableStateFlow.restart()
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    protected fun <T : Any> CRUDRepository<T>.viewModelPagingFlow(
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
        config: PagingConfig = createPagingConfig(10),
        initialKey: Int? = null,
        remoteMediator: RemoteMediator<Int, T>? = null,
        firstItemOffset: Int = 0,
        disablePrepend: Boolean = false,
    ) = pager(sort, predicate, config, initialKey, remoteMediator, viewModelScope, firstItemOffset, disablePrepend)

    @OptIn(ExperimentalPagingApi::class)
    protected fun <Value : Any> CRUDRepository<Value>.viewModelMutablePager(
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
        properties: List<EntityProperty>,
        getEntityValues: (Value) -> List<String>,
        createEntity: (Map<String, String>) -> Value,
        config: PagingConfig = createPagingConfig(10),
        initialKey: Int? = null,
        remoteMediator: RemoteMediator<Int, Value>? = null,
        firstItemOffset: Int = 0,
        disablePrepend: Boolean = false,
    ) = mutablePager(
        sort,
        predicate,
        properties,
        getEntityValues,
        createEntity,
        config,
        initialKey,
        remoteMediator,
        viewModelScope,
        firstItemOffset,
        disablePrepend,
    )

    @OptIn(ExperimentalPagingApi::class)
    protected fun CRUDRepository<*>.viewModelPagingFlow(
        projections: List<Variable>,
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
        config: PagingConfig = createPagingConfig(10),
        initialKey: Int? = null,
        remoteMediator: RemoteMediator<Int, List<Any?>>? = null,
        firstItemOffset: Int = 0,
    ) = pager(projections, sort, predicate, config, initialKey, remoteMediator, viewModelScope, firstItemOffset)

    public companion object {

        // The reasoning 5_000 was chosen for the stopTimeoutMillis can be found in the official Android documentation, which discusses the ANR (Application Not Responding) timeout threshold.
        public const val STATE_STARTED_STOP_TIMEOUT_MILLIS: Long = 5_000
    }
}
