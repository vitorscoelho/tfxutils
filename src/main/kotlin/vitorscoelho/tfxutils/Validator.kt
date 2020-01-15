package vitorscoelho.tfxutils

import tornadofx.*
import java.math.BigDecimal

val VALIDATE_ANYTHING: ValidationContext.(String?) -> ValidationMessage? by lazy {
    val retorno: ValidationContext.(String?) -> ValidationMessage? = { _: String? -> null }
    retorno
}

val ERROR_IF_NOT_INT: ValidationContext.(String?) -> ValidationMessage? by lazy {
    ifNotIntInInterval(severity = ValidationSeverity.Error)
}

val ERROR_IF_NOT_POSITIVE_INT: ValidationContext.(String?) -> ValidationMessage? by lazy {
    ifNotIntInInterval(min = 0, minInclusive = false, severity = ValidationSeverity.Error)
}

val ERROR_IF_NEGATIVE_INT: ValidationContext.(String?) -> ValidationMessage? by lazy {
    ifNotIntInInterval(min = 0, severity = ValidationSeverity.Error)
}

val ERROR_IF_NOT_DOUBLE: ValidationContext.(String?) -> ValidationMessage? by lazy {
    ifNotDoubleInInterval(severity = ValidationSeverity.Error)
}

val ERROR_IF_NOT_POSITIVE_DOUBLE: ValidationContext.(String?) -> ValidationMessage? by lazy {
    ifNotDoubleInInterval(min = 0.0, minInclusive = false, severity = ValidationSeverity.Error)
}

val ERROR_IF_NEGATIVE_DOUBLE: ValidationContext.(String?) -> ValidationMessage? by lazy {
    ifNotDoubleInInterval(min = 0.0, severity = ValidationSeverity.Error)
}

fun ifNotIntInInterval(
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
    minInclusive: Boolean = true,
    maxInclusive: Boolean = true,
    severity: ValidationSeverity
): ValidationContext.(String?) -> ValidationMessage? {
    val validacaoIntervalo = inteiroIntervalo(
        minimo = min, maximo = max,
        inclusiveMinimo = minInclusive, inclusiveMaximo = maxInclusive
    )
    return { text: String? ->
        val textoUtilizado: String = text ?: ""
        val msg = validacaoIntervalo(textoUtilizado)
        if (msg == "") null else ValidationMessage(message = msg, severity = severity)
    }
}

fun ifNotDoubleInInterval(
    min: Double = -Double.MAX_VALUE,
    max: Double = Double.MAX_VALUE,
    minInclusive: Boolean = true,
    maxInclusive: Boolean = true,
    severity: ValidationSeverity
): ValidationContext.(String?) -> ValidationMessage? {
    val validacaoIntervalo = doubleIntervalo(
        minimo = min, maximo = max,
        inclusiveMinimo = minInclusive, inclusiveMaximo = maxInclusive
    )
    return { text: String? ->
        val textoUtilizado: String = text ?: ""
        val msg = validacaoIntervalo(textoUtilizado)
        if (msg == "") null else ValidationMessage(message = msg, severity = severity)
    }
}

private fun Number.compare(outro: Number): Int = BigDecimal(this.toString()).compareTo(BigDecimal(outro.toString()))
private fun Number.maiorQue(outro: Number): Boolean = (this.compare(outro) > 0)
private fun Number.maiorOuIgualA(outro: Number): Boolean = (this.compare(outro) >= 0)
private fun Number.menorQue(outro: Number): Boolean = (this.compare(outro) < 0)
private fun Number.menorOuIgualA(outro: Number): Boolean = (this.compare(outro) <= 0)

private fun <T : Number> numberIntervalo(
    minimo: T,
    maximo: T,
    inclusiveMinimo: Boolean = true,
    inclusiveMaximo: Boolean = true,
    minimoPossivel: T,
    maximoPossivel: T,
    tipoNumero: String,
    isNumber: (text: String) -> Boolean,
    toNumber: (text: String) -> T
): (text: String) -> String {
    val msgErro = run {
        val inicio = "Deve ser um $tipoNumero"
        val msgMinimo: String = if (minimo == minimoPossivel && inclusiveMinimo) {
            ""
        } else if (inclusiveMinimo) {
            " maior ou igual a $minimo"
        } else {
            " maior que $minimo"
        }
        val msgMaximo: String = if (maximo == maximoPossivel && inclusiveMaximo) {
            ""
        } else if (inclusiveMaximo) {
            " menor ou igual a $maximo"
        } else {
            " menor que $maximo"
        }
        var msgFinal = inicio + msgMinimo
        if (msgMinimo != "" && msgMaximo != "") msgFinal += "e "
        msgFinal += msgMaximo
        return@run msgFinal
    }

    fun funValid(text: String): String {
        if (!isNumber(text)) return msgErro
        val valorNumber: T = toNumber(text)
        if (inclusiveMinimo) {
            if (valorNumber.menorQue(minimo)) return msgErro
        } else {
            if (valorNumber.menorOuIgualA(minimo)) return msgErro
        }
        if (inclusiveMaximo) {
            if (valorNumber.maiorQue(maximo)) return msgErro
        } else {
            if (valorNumber.maiorOuIgualA(maximo)) return msgErro
        }
        return ""
    }

    val valid: (String) -> String = { text -> funValid(text = text) }
    return valid
}

private fun inteiroIntervalo(
    minimo: Int = Int.MIN_VALUE,
    maximo: Int = Int.MAX_VALUE,
    inclusiveMinimo: Boolean = true,
    inclusiveMaximo: Boolean = true
): (text: String) -> String {
    return numberIntervalo(
        minimo = minimo, maximo = maximo,
        inclusiveMinimo = inclusiveMinimo, inclusiveMaximo = inclusiveMaximo,
        minimoPossivel = Int.MIN_VALUE, maximoPossivel = Int.MAX_VALUE,
        tipoNumero = "inteiro",
        isNumber = { text -> text.isInt() },
        toNumber = { text -> text.toInt() }
    )
}

private fun doubleIntervalo(
    minimo: Double = -Double.MAX_VALUE,
    maximo: Double = Double.MAX_VALUE,
    inclusiveMinimo: Boolean = true,
    inclusiveMaximo: Boolean = true
): (text: String) -> String {
    return numberIntervalo(
        minimo = minimo, maximo = maximo,
        inclusiveMinimo = inclusiveMinimo, inclusiveMaximo = inclusiveMaximo,
        minimoPossivel = -Double.MAX_VALUE, maximoPossivel = Double.MAX_VALUE,
        tipoNumero = "número real",
        isNumber = { text -> text.isDouble() },
        toNumber = { text -> text.toDouble() }
    )
}