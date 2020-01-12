package vitorscoelho.tfxutils

import javafx.beans.binding.BooleanExpression
import javafx.beans.property.IntegerProperty
import javafx.beans.property.Property
import javafx.event.EventTarget
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.control.Tooltip
import javafx.util.Duration
import javafx.util.StringConverter
import tornadofx.*
import java.math.BigDecimal
import java.util.*


class Contexto(val recurso: Recurso) {
    val validationTornadoFX = ValidationContext()

    private val map = hashMapOf<String, DadosInput<*>>()

    fun <T> get(property: Property<T>): DadosInput<T>? = map[property.name] as DadosInput<T>

//    fun add(property: Property<*>, dados: DadosInput) {
//        map[property.name] = dados
//    }

//    fun <T> add(property: Property<T>, op: DadosInput.() -> Unit = {}): DadosInput {
//        val dados = DadosInput()
//        op(dados)
//        map[property.name] = dados
//        return dados
//    }

//    fun <T> add(property: Property<T>, dados: DadosInput<T>) {
//        map[property.name] = dados
//    }

    fun addInteiro(
        property: Property<Number>,
        op: DadosInputIntBuilder.() -> Unit = {}
    ): DadosInput<Int> {
        val dadosBuilder = DadosInputIntBuilder(property)
        op(dadosBuilder)
        setNomeDescricaoPeloRb(dadosBuilder)
        val dados = dadosBuilder.build()
        map[property.name] = dados
        return dados
    }

    fun addDouble(
        property: Property<Number>,
        op: DadosInputDoubleBuilder.() -> Unit = {}
    ): DadosInput<Number> {
        val dadosBuilder = DadosInputDoubleBuilder(property)
        op(dadosBuilder)
        setNomeDescricaoPeloRb(dadosBuilder)
        val dados = dadosBuilder.build()
        map[property.name] = dados
        return dados
    }
    /*
    companion object {
        fun addValidator(
            context: PropertiesContext,
            textField: TextField,
            predicate: () -> Boolean
        ): BooleanExpression {
            val validator = context.validationTornadoFX.addValidator(
                node = textField,
                property = textField.textProperty()
            ) {
                if (predicate.invoke()) null else error()
            }
            validator.validate(decorateErrors = true)
            return validator.valid
        }
    }
     */

    private fun setNomeDescricaoPeloRb(dadosBuilder: DadosBuilder<*>) {
        val rb = recurso.rb
        try {
            dadosBuilder.nome = rb["${dadosBuilder.property.name}.${recurso.sufixoNome}"]
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            dadosBuilder.descricao = rb["${dadosBuilder.property.name}.${recurso.sufixoDescricao}"]
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
    fun EventTarget.fieldset(text: String? = null, icon: Node? = null, labelPosition: Orientation? = null, wrapWidth: Double? = null, op: Fieldset.() -> Unit = {}): Fieldset {
        val fieldset = Fieldset(text ?: "")
        if (wrapWidth != null) fieldset.wrapWidth = wrapWidth
        if (labelPosition != null) fieldset.labelPosition = labelPosition
        if (icon != null) fieldset.icon = icon
        opcr(this, fieldset, op)
        return fieldset
    }
    */
}

class Recurso(val rb: ResourceBundle, val sufixoNome: String, val sufixoDescricao: String)

interface DadosBuilder<T> {
    val property: Property<T>
    var nome: String
    var descricao: String
}

class DadosInputIntBuilder(override val property: Property<Number>) : DadosBuilder<Number> {
    var value: Number
        get() = property.value
        set(value) {
            property.value = value
        }

    override var nome = ""
    override var descricao = ""
    var validador: ValidacaoInput = ValidacaoInput.INTEIRO

    fun positivo() {
        validador = ValidacaoInput.INTEIRO_POSITIVO
    }

    fun intervalo(
        minimo: Int = Int.MIN_VALUE,
        maximo: Int = Int.MAX_VALUE,
        inclusiveMinimo: Boolean = true,
        inclusiveMaximo: Boolean = true
    ) {
        validador = ValidacaoInput.inteiroIntervalo(
            minimo = minimo, maximo = maximo,
            inclusiveMinimo = inclusiveMinimo, inclusiveMaximo = inclusiveMaximo
        )
    }

    fun build(): DadosInput<Int> {
        return object : DadosInput<Int>(
            nome = nome,
            descricao = descricao,
            converter = stringConverterInt,
            filterInput = validador.filterInput,
            valid = validador.valid
        ) {}
    }
}

class DadosInputDoubleBuilder(override val property: Property<Number>) : DadosBuilder<Number> {
    var value: Number
        get() = property.value
        set(value) {
            property.value = value
        }

    override var nome = ""
    override var descricao = ""
    var validador: ValidacaoInput = ValidacaoInput.REAL

    fun positivo() {
        validador = ValidacaoInput.REAL_POSITIVO
    }

    fun intervalo(
        minimo: Double = -Double.MAX_VALUE,
        maximo: Double = Double.MAX_VALUE,
        inclusiveMinimo: Boolean = true,
        inclusiveMaximo: Boolean = true
    ) {
        validador = ValidacaoInput.doubleIntervalo(
            minimo = minimo, maximo = maximo,
            inclusiveMinimo = inclusiveMinimo, inclusiveMaximo = inclusiveMaximo
        )
    }

    fun build(): DadosInput<Number> {
        return object : DadosInput<Number>(
            nome = nome,
            descricao = descricao,
            converter = stringConverterDouble,
            filterInput = validador.filterInput,
            valid = validador.valid
        ) {}
    }
}

abstract class DadosInput<T>(
    val nome: String,
    val descricao: String,
    val converter: StringConverter<T>,
    val filterInput: (formatter: TextFormatter.Change) -> Boolean,
    val valid: (text: String) -> String
)

class ValidacaoInput(
    val filterInput: (formatter: TextFormatter.Change) -> Boolean,
    val valid: (text: String) -> String
) {
    companion object {
        val QUALQUER = ValidacaoInput(filterInput = { true }, valid = { "" })
        val INTEIRO = ValidacaoInput(
            filterInput = { formatter -> formatter.controlNewText.isInt() || formatter.controlNewText == "-" },
            valid = { text -> if (text.isInt()) "" else "Deve ser um número inteiro" }
        )
        val INTEIRO_POSITIVO = ValidacaoInput(
            filterInput = { formatter -> formatter.controlNewText.isInt() && !formatter.controlNewText.contains("-") },
            valid = { text -> if ((text.isInt()) && (text.toInt() > 0)) "" else "Deve ser um inteiro maior que zero" }
        )
        val REAL = ValidacaoInput(
            filterInput = { formatter ->
                (formatter.controlNewText.isDouble() && !formatter.controlNewText.contains(
                    other = "d",
                    ignoreCase = true
                )) || formatter.controlNewText == "-"
            },
            valid = { text -> if (text.isDouble()) "" else "Deve ser um número real" }
        )
        val REAL_POSITIVO = ValidacaoInput(
            filterInput = { formatter ->
                formatter.controlNewText.isDouble() &&
                        !formatter.controlNewText.contains("-") &&
                        !formatter.controlNewText.contains(other = "d", ignoreCase = true)
            },
            valid = { text -> if ((text.isDouble()) && (text.toDouble() > 0.0)) "" else "Deve ser um número real maior que zero" }
        )

        fun inteiroIntervalo(
            minimo: Int = Int.MIN_VALUE,
            maximo: Int = Int.MAX_VALUE,
            inclusiveMinimo: Boolean = true,
            inclusiveMaximo: Boolean = true
        ): ValidacaoInput {
            val validacaoAuxiliar = if (minimo >= 0) INTEIRO_POSITIVO else INTEIRO
            val filterInput = validacaoAuxiliar.filterInput
            val valid = numberIntervalo(
                minimo = minimo, maximo = maximo,
                inclusiveMinimo = inclusiveMinimo, inclusiveMaximo = inclusiveMaximo,
                minimoPossivel = Int.MIN_VALUE, maximoPossivel = Int.MAX_VALUE,
                tipoNumero = "inteiro",
                isNumber = { text -> text.isInt() },
                toNumber = { text -> text.toInt() }
            )
            return ValidacaoInput(filterInput = filterInput, valid = valid)
        }

        fun doubleIntervalo(
            minimo: Double = -Double.MAX_VALUE,
            maximo: Double = Double.MAX_VALUE,
            inclusiveMinimo: Boolean = true,
            inclusiveMaximo: Boolean = true
        ): ValidacaoInput {
            val validacaoAuxiliar = if (minimo >= 0) REAL_POSITIVO else REAL
            val filterInput = validacaoAuxiliar.filterInput
            val valid = numberIntervalo(
                minimo = minimo, maximo = maximo,
                inclusiveMinimo = inclusiveMinimo, inclusiveMaximo = inclusiveMaximo,
                minimoPossivel = -Double.MAX_VALUE, maximoPossivel = Double.MAX_VALUE,
                tipoNumero = "número real",
                isNumber = { text -> text.isDouble() },
                toNumber = { text -> text.toDouble() }
            )
            return ValidacaoInput(filterInput = filterInput, valid = valid)
        }

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
                if (msgMinimo != "") msgFinal += "e "
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
    }
}

private fun Number.compare(outro: Number): Int = BigDecimal(this.toString()).compareTo(BigDecimal(outro.toString()))
private fun Number.maiorQue(outro: Number): Boolean = (this.compare(outro) > 0)
private fun Number.maiorOuIgualA(outro: Number): Boolean = (this.compare(outro) >= 0)
private fun Number.menorQue(outro: Number): Boolean = (this.compare(outro) < 0)
private fun Number.menorOuIgualA(outro: Number): Boolean = (this.compare(outro) <= 0)

fun EventTarget.fieldNumber(
    property: Property<Number>,
    contexto: Contexto,
    op: Field .(tf: TextField) -> kotlin.Unit = {}
): Field {
    return this.field {
        val dados: DadosInput<Number>? = contexto.get(property)
        if (dados != null) {
            text = dados.nome
            val tf = textfield {
                filterInput(dados.filterInput)
                textProperty().bindBidirectional(property, dados.converter)
//                adicionarContextoETooltip(
//
//                )
            }
            op(this, tf)
        }
    }
}

private fun Field.adicionarContextoETooltip(
    contexto: Contexto,
    dados: DadosInput<*>,
    textField: TextField,
    descricao: String
) {
    val validator = contexto.validationTornadoFX.addValidator(node = textField, property = textField.textProperty()) {
        if (dados.valid.invoke(textField.text) == "") null else error()
    }
    validator.validate(decorateErrors = true)
//    textField.adicionarTooltip(validExpression = validator.valid)
//    }
}

private fun TextField.adicionarTooltip(
    validExpression: BooleanExpression,
    descricao: String,
    msgErro: String,
    cssRuleDescricao: CssRule,
    cssRuleErro: CssRule,
    tooltipDelay: Double
) {
    //Configurando o Tooltip
    //Tive que criar um novo tooltip ao invés de só mudar o estilo (quando a msgErro deveria ser mostrada, porque a classe do css não estava alternando. BUG?
    fun adicionar(valido: Boolean) {
        adicionarTooltip(
            valido = valido,
            descricao = descricao,
            msgErro = msgErro,
            cssRuleDescricao = cssRuleDescricao,
            cssRuleErro = cssRuleErro,
            tooltipDelay = tooltipDelay
        )
    }
    adicionar(valido = validExpression.value)
    validExpression.onChange { valido -> adicionar(valido = valido) }
}

private fun TextField.adicionarTooltip(
    valido: Boolean,
    descricao: String,
    msgErro: String,
    cssRuleDescricao: CssRule,
    cssRuleErro: CssRule,
    tooltipDelay: Double
) {
    tooltip = null
    if (valido && descricao.isNotBlank()) adicionarTooltipDescricao(
        texto = descricao,
        cssRule = cssRuleDescricao,
        tooltipDelay = tooltipDelay
    )
    if (!valido && msgErro.isNotBlank()) adicionarTooltipErro(
        texto = msgErro,
        cssRule = cssRuleErro,
        tooltipDelay = tooltipDelay
    )
}

private fun TextField.adicionarTooltipDescricao(texto: String, cssRule: CssRule, tooltipDelay: Double) {
    adicionarTooltip(texto = texto, cssRule = cssRule, tooltipDelay = tooltipDelay)
}

private fun TextField.adicionarTooltipErro(texto: String, cssRule: CssRule, tooltipDelay: Double) {
    adicionarTooltip(texto = texto, cssRule = cssRule, tooltipDelay = tooltipDelay)
}

private fun TextField.adicionarTooltip(texto: String, cssRule: CssRule, tooltipDelay: Double) {
    val tooltip = Tooltip(texto)
    tooltip.showDelay = Duration(tooltipDelay)
    setTooltip(tooltip)
    tooltip.addClass(cssRule)
}

private val stringConverterString: StringConverter<String> = object : StringConverter<String>() {
    override fun toString(valor: String?): String? = valor

    override fun fromString(string: String?): String? = string
}

private val stringConverterDouble: StringConverter<Number> = object : StringConverter<Number>() {
    override fun toString(valor: Number?): String? {
        if (valor == null) return null
        return valor.toString()
    }

    override fun fromString(valor: String?): Double {
        if (valor.isNullOrBlank() || valor == "-") return 0.0
        return valor.toDouble()
    }
}

private val stringConverterInt: StringConverter<Int> = object : StringConverter<Int>() {
    override fun toString(valor: Int?): String? {
        if (valor == null) return null
        return valor.toString()
    }

    override fun fromString(valor: String?): Int {
        if (valor.isNullOrBlank() || valor == "-") return 0
        return valor.toInt()
    }
}