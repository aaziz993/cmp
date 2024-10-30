package ai.tech.core.misc.platform

import ai.tech.core.misc.platform.model.IosPlatform
import ai.tech.core.misc.platform.model.Platform
import platform.UIKit.UIDevice

public actual fun getPlatform(): Platform = IosPlatform(UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion)