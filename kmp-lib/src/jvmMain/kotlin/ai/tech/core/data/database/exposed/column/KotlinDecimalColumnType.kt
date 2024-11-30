package ai.tech.core.data.database.exposed.column

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import java.math.MathContext
import java.sql.ResultSet
import java.sql.SQLException
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table

public class KotlinDecimalColumnType(
    /** Total count of significant digits in the whole number. */
    public val precision: Int,
    /** Count of decimal digits in the fractional part. */
    public val scale: Int
) : ColumnType<BigDecimal>() {

    override fun sqlType(): String = "DECIMAL($precision, $scale)"

    override fun readObject(rs: ResultSet, index: Int): Any? {
        return rs.getObject(index)
    }

    override fun valueFromDB(value: Any): BigDecimal = when (value) {
        is BigDecimal -> value
        is Double -> {
            if (value.isNaN()) {
                throw SQLException("Unexpected value of type Double: NaN of ${value::class.qualifiedName}")
            }
            else {
                value.toBigDecimal(decimalMode = DecimalMode(roundingMode = RoundingMode.ROUND_HALF_TO_EVEN, scale = scale.toLong()))
            }
        }

        is Float -> {
            if (value.isNaN()) {
                error("Unexpected value of type Float: NaN of ${value::class.qualifiedName}")
            }
            else {
                value.toBigDecimal(decimalMode = DecimalMode(roundingMode = RoundingMode.ROUND_HALF_TO_EVEN, scale = scale.toLong()))
            }
        }

        is Long -> value.toBigDecimal(decimalMode = DecimalMode(roundingMode = RoundingMode.ROUND_HALF_TO_EVEN, scale = scale.toLong()))
        is Int -> value.toBigDecimal(decimalMode = DecimalMode(roundingMode = RoundingMode.ROUND_HALF_TO_EVEN, scale = scale.toLong()))
        is Short -> value.toLong().toBigDecimal(decimalMode = DecimalMode(roundingMode = RoundingMode.ROUND_HALF_TO_EVEN, scale = scale.toLong()))
        else -> error("Unexpected value of type Decimal: $value of ${value::class.qualifiedName}")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as KotlinDecimalColumnType

        if (precision != other.precision) return false
        if (scale != other.scale) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + precision
        result = 31 * result + scale
        return result
    }

    public companion object {

        internal val INSTANCE = KotlinDecimalColumnType(MathContext.DECIMAL64.precision, 20)
    }
}

public fun Table.kotlinDecimal(name: String, precision: Int, scale: Int): Column<BigDecimal> = registerColumn(name, KotlinDecimalColumnType(precision, scale))
