package ai.tech.core.presentation.component.dropdown.enum

import ai.tech.core.presentation.component.dropdown.list.ListDropdown
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@Composable
public fun <T : Enum<T>> EnumDropdown(
    values: () -> Array<T>,
    text: @Composable () -> Unit,
    onValueChange: (String) -> Unit,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    scrollState: ScrollState = rememberScrollState(),
    properties: PopupProperties = PopupProperties(focusable = true),
    shape: Shape = MenuDefaults.shape,
    containerColor: Color = MenuDefaults.containerColor,
    tonalElevation: Dp = MenuDefaults.TonalElevation,
    shadowElevation: Dp = MenuDefaults.ShadowElevation,
    border: BorderStroke? = null,
): Unit = ListDropdown(
    values().map(Any::toString),
    text,
    onValueChange,
    expanded,
    onDismissRequest,
    modifier,
    offset,
    scrollState,
    properties,
    shape,
    containerColor,
    tonalElevation,
    shadowElevation,
    border,
)

@Composable
public inline fun <reified T : Enum<T>> EnumDropdown(
    noinline text: @Composable () -> Unit,
    noinline onValueChange: (String) -> Unit,
    expanded: Boolean,
    noinline onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    scrollState: ScrollState = rememberScrollState(),
    properties: PopupProperties = PopupProperties(focusable = true),
    shape: Shape = MenuDefaults.shape,
    containerColor: Color = MenuDefaults.containerColor,
    tonalElevation: Dp = MenuDefaults.TonalElevation,
    shadowElevation: Dp = MenuDefaults.ShadowElevation,
    border: BorderStroke? = null,
): Unit = EnumDropdown(
    { enumValues<T>() },
    text,
    onValueChange,
    expanded,
    onDismissRequest,
    modifier,
    offset,
    scrollState,
    properties,
    shape,
    containerColor,
    tonalElevation,
    shadowElevation,
    border,
)
