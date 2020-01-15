package vitorscoelho.tfxutils

import javafx.util.StringConverter

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