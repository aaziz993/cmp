package ai.tech.core.data.database.exposed.transform

import ai.tech.core.data.database.exposed.transform.model.Meal
import kotlinx.datetime.LocalTime
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnTransformer
import org.jetbrains.exposed.sql.Table

public object MealTimeTransformer : ColumnTransformer<LocalTime, Meal> {

    override fun wrap(value: LocalTime): Meal = when {
        value.hour < 10 -> Meal.BREAKFAST
        value.hour < 15 -> Meal.LUNCH
        else -> Meal.DINNER
    }

    override fun unwrap(value: Meal): LocalTime = when (value) {
        Meal.BREAKFAST -> LocalTime(8, 0)
        Meal.LUNCH -> LocalTime(12, 0)
        Meal.DINNER -> LocalTime(18, 0)
    }
}

public fun Table.meal(column: Column<LocalTime>): Column<Meal> = column.transform(MealTimeTransformer)

