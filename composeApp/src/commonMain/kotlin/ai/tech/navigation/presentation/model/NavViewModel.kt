package ai.tech.navigation.presentation.model

import ai.tech.core.presentation.event.navigator.NavigationAction
import ai.tech.core.presentation.viewmodel.AbstractViewModel
import androidx.lifecycle.SavedStateHandle
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
public class NavViewModel(override val savedStateHandle: SavedStateHandle) : AbstractViewModel<Unit, NavigationAction>() {

}
