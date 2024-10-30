package ai.tech.core.misc.type.single

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom

public fun uuidFromOrNull(s: String): Uuid? = s.runCatching { uuidFrom(this) }.getOrNull()
