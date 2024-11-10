package ai.tech.core.presentation.viewmodel.model;

import ai.tech.core.misc.type.multiple.model.RestartableStateFlow
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
public interface ViewModelMutableStateFlow<T> : RestartableStateFlow<T>, MutableStateFlow<T>
