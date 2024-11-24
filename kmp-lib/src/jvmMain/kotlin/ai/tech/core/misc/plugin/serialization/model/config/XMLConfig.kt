package ai.tech.core.misc.plugin.serialization.model.config

import ai.tech.core.misc.type.serializer.http.ContentTypeSerial
import ai.tech.core.misc.type.serializer.QNameSerial
import io.ktor.http.*
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion

@Serializable
public data class XMLConfig(
    val repairNamespaces: Boolean? = null,
    val xmlDeclMode: XmlDeclMode? = null,
    val indentString: String? = null,
    val nilAttribute: Pair<QNameSerial, String>? = null,
    val xmlVersion: XmlVersion? = null,
    val autoPolymorphic: Boolean? = null,
    override val contentType: ContentTypeSerial = ContentType.Application.Xml,
    override val enabled: Boolean = true,
) : SerializationFormatConfig
