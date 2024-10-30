package ai.tech.core.misc.type.serializer

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.QName
import nl.adaptivity.xmlutil.QNameSerializer

public typealias QNameSerial = @Serializable(with = QNameSerializer::class) QName