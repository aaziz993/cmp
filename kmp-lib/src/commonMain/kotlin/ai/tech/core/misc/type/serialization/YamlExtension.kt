package ai.tech.core.misc.type.serialization

import net.mamoe.yamlkt.Yaml

public val String.yamlAny: Any?
    get() = Yaml.Default.decodeAnyFromString(this)

public val String.yamlList: List<Any?>
    get() = Yaml.Default.decodeListFromString(this)

public val String.yamlMap: Map<String?, Any?>
    get() = Yaml.Default.decodeMapFromString(this)
