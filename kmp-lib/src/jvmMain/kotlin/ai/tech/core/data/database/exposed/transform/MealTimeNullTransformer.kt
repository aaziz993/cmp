package ai.tech.core.data.database.exposed.transform

import ai.tech.core.data.database.exposed.transform.model.Meal
import kotlinx.datetime.LocalTime
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnTransformer
import org.jetbrains.exposed.sql.Table

public object MealTimeNullTransformer : ColumnTransformer<LocalTime?, Meal?> {

    override fun wrap(value: LocalTime?): Meal? = value?.let {
        when {
            it.hour < 10 -> Meal.BREAKFAST
            it.hour < 15 -> Meal.LUNCH
            else -> Meal.DINNER
        }
    }

    override fun unwrap(value: Meal?): LocalTime? = value?.let {
        when (it) {
            Meal.BREAKFAST -> LocalTime(8, 0)
            Meal.LUNCH -> LocalTime(12, 0)
            Meal.DINNER -> LocalTime(18, 0)
            else -> LocalTime(0, 0)
        }
    }
}

public fun Table.nullableMeal(column: Column<LocalTime?>): Column<Meal?> =
    column.transform(MealTimeNullTransformer)
