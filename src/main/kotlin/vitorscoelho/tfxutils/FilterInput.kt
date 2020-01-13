package vitorscoelho.tfxutils

import javafx.scene.control.TextFormatter
import tornadofx.isDouble
import tornadofx.isInt

val FILTER_INPUT_ANY: (formatter: TextFormatter.Change) -> Boolean by lazy {
    { _: TextFormatter.Change -> true }
}

val FILTER_INPUT_INT: (formatter: TextFormatter.Change) -> Boolean by lazy {
    { formatter: TextFormatter.Change -> formatter.controlNewText.isInt() || formatter.controlNewText == "-" }
}

val FILTER_INPUT_POSITIVE_INT: (formatter: TextFormatter.Change) -> Boolean by lazy {
    { formatter: TextFormatter.Change -> formatter.controlNewText.isInt() && !formatter.controlNewText.contains("-") }
}

val FILTER_INPUT_REAL: (formatter: TextFormatter.Change) -> Boolean by lazy {
    { formatter: TextFormatter.Change ->
        (formatter.controlNewText.isDouble() && !formatter.controlNewText.contains(
            other = "d",
            ignoreCase = true
        )) || formatter.controlNewText == "-"
    }
}

val FILTER_INPUT_POSITIVE_REAL: (formatter: TextFormatter.Change) -> Boolean by lazy {
    { formatter: TextFormatter.Change ->
        formatter.controlNewText.isDouble() &&
                !formatter.controlNewText.contains("-") &&
                !formatter.controlNewText.contains(other = "d", ignoreCase = true)
    }
}