package vitorscoelho.tfxutils

import javafx.beans.property.Property
import javafx.util.StringConverter
import javax.measure.Quantity

val STRING_CONVERTER_STRING: StringConverter<String> by lazy {
    object : StringConverter<String>() {
        override fun toString(valor: String?): String? = valor
        override fun fromString(string: String?): String? = string
    }
}

val STRING_CONVERTER_DOUBLE: StringConverter<Number> by lazy {
    object : StringConverter<Number>() {
        override fun toString(valor: Number?): String? {
            if (valor == null) return null
            return valor.toString()
        }

        override fun fromString(valor: String?): Double {
            if (valor.isNullOrBlank() || valor == "-") return 0.0
            return valor.toDouble()
        }
    }
}

val STRING_CONVERTER_INT: StringConverter<Number> by lazy {
    object : StringConverter<Number>() {
        override fun toString(valor: Number?): String? {
            if (valor == null) return null
            return valor.toString()
        }

        override fun fromString(valor: String?): Int {
            if (valor.isNullOrBlank() || valor == "-") return 0
            return valor.toInt()
        }
    }
}

interface QuantityFactory {
    fun <T : Quantity<T>> getQuantity(value: Number, unit: javax.measure.Unit<T>): Quantity<T>
}

var quantityFactory: QuantityFactory = object : QuantityFactory {
    override fun <T : Quantity<T>> getQuantity(value: Number, unit: javax.measure.Unit<T>): Quantity<T> {
        throw NoSuchElementException("Variável |quantityFactory| não foi definida. Defina antes de utilizar qualquer recurso que utilize |stringConverterQuantity|.")
    }
}

fun <T : Quantity<T>> stringConverterQuantity(
    property: Property<Quantity<T>>,
    converterNumber: StringConverter<Number>
) = object : StringConverter<Quantity<T>>() {
    override fun toString(valor: Quantity<T>?) = valor?.value.toString() ?: ""

    override fun fromString(string: String?): Quantity<T> {
        val magnitude: Number = converterNumber.fromString(string)
        return quantityFactory.getQuantity(magnitude, property.value.unit)
    }
}