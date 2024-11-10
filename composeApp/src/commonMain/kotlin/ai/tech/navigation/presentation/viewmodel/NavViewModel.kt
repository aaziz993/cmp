package ai.tech.navigation.presentation.viewmodel

import ai.tech.core.presentation.event.navigator.NavigationAction
import ai.tech.core.presentation.event.navigator.Navigator
import ai.tech.core.presentation.navigation.Destination
import ai.tech.core.presentation.viewmodel.AbstractViewModel
import ai.tech.core.presentation.viewmodel.ViewModelState
import ai.tech.core.presentation.viewmodel.success
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
public class NavViewModel(
    public val navigator: Navigator<Destination>,
    override val savedStateHandle: SavedStateHandle
) : AbstractViewModel<NavigationAction>() {

    override fun action(action: NavigationAction): Boolean = navigator.navigate(action)
}
