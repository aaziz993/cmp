package ai.tech.core.data.database.exposed.column

import ai.tech.core.misc.type.serializer.decodeFromAny
import ai.tech.core.misc.type.serializer.encodeAnyToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.json.json

public inline fun <reified T : Any> Table.anyJson(name: String) =
    json<T>(name, Json.Default::encodeAnyToString, Json.Default::decodeFromAny)
