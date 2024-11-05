package customs.cms.model

import core.presentation.component.datatable.model.config.CRUDTableConfig
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Serializable
public data class ScanConfig(
    val crudTable: CRUDTableConfig = CRUDTableConfig(),
    val mapInterval: Duration = 5.toDuration(DurationUnit.MINUTES),
    val imageDir: String = "",
    val initialSaveDir: String? = null,
)