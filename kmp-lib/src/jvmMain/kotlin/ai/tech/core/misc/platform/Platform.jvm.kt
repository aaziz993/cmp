package ai.tech.core.misc.platform

import ai.tech.core.misc.platform.model.JvmPlatform
import ai.tech.core.misc.platform.model.Platform


public actual fun getPlatform(): Platform = JvmPlatform("Java ${System.getProperty("java.version")}")
