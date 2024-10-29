package ai.tech

import ai.tech.di.koinConfiguration
import ai.tech.navigation.presentation.NavScreen
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Preview
@Composable
public fun App(): Unit = KoinApplication({ koinConfiguration() }) { NavScreen() }