package ai.tech.core.misc.platform.model

public sealed class Platform(public val name: String)

public class JvmPlatform(name: String) : Platform(name)

public class AndroidPlatform(name: String) : Platform(name)

public open class IosPlatform(name: String) : Platform(name)

public class IosArm64Platform(name: String) : IosPlatform(name)

public class IosX64Platform(name: String) : IosPlatform(name)

public class IosSimulatorArm64Platform(name: String) : IosPlatform(name)

public sealed class JsPlatform(name: String) : Platform(name)

public class JsBrowserPlatform(name: String) : JsPlatform(name)

public class JsNodePlatform(name: String) : JsPlatform(name)

public class WasmJsBrowserPlatform(name: String) : JsPlatform(name)

public class WasmJsNodePlatform(name: String) : JsPlatform(name)
