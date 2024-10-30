package ai.tech.core.misc.platform

import ai.tech.core.misc.platform.model.AndroidPlatform
import ai.tech.core.misc.platform.model.Platform
import android.os.Build

public actual fun getPlatform(): Platform = AndroidPlatform("Android ${Build.VERSION.SDK_INT}")