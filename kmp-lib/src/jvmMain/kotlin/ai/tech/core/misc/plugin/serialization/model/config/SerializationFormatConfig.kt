package ai.tech.core.misc.plugin.serialization.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.type.serialization.serializer.http.ContentTypeSerial

public interface SerializationFormatConfig : EnabledConfig {
    public val contentType: ContentTypeSerial
}
