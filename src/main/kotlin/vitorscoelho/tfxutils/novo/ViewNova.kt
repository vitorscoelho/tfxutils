package vitorscoelho.tfxutils.novo

import javafx.application.Application
import javafx.beans.property.IntegerProperty
import javafx.beans.property.Property
import javafx.event.EventTarget
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.control.Tooltip
import javafx.util.Duration
import javafx.util.StringConverter
import tornadofx.*
import vitorscoelho.tfxutils.ERROR_IF_NEGATIVE_INT
import vitorscoelho.tfxutils.ERROR_IF_NOT_INT
import vitorscoelho.tfxutils.ERROR_IF_NOT_POSITIVE_INT
import vitorscoelho.tfxutils.ValidacaoInput
import vitorscoelho.tfxutils.exemplo.BeanElemento
import vitorscoelho.tfxutils.exemplo.BeanElementoModel
import java.util.*

fun main(args: Array<String>) {
    Locale.setDefault(Locale.US)
    FX.locale = Locale("en", "US")
    Application.launch(Aplicacao::class.java, *args)
}

class Aplicacao : App(ViewNova::class) {
    init {
        reloadStylesheetsOnFocus()
        val versaoJava = System.getProperty("java.version")
        val versaoJavaFX = System.getProperty("javafx.version")
        println("Versão Java: $versaoJava // Versão JavaFX: $versaoJavaFX")
    }
}

class ViewNova : View() {
    val validationContext = ValidationContext()
    val elemento = BeanElementoModel(
        BeanElemento().apply {
            texto = "Texto"
            inteiro = 1
            real = 2.0
        }
    )
    val descricoes = Descricoes(
        rb = ResourceBundle.getBundle("vitorscoelho.tfxutils.Textos"),
        sufixoNome = "nome",
        sufixoDescricao = "descricao"
    )
    override val root = form {
        fieldset("Dados de elemento") {
            //            inputTextFieldString(property = elemento.texto, mapDadosInput = mapDadosInput) {
//
//            }
            inputTextFieldInt(property = elemento.inteiro, descricoes = descricoes) {
                textfield {
                    tooltip = Tooltip("Descrição")
                    tooltipProperty().onChange {
                        println()
                    }
                    val v = validationContext.addValidator(node = this, validator = ERROR_IF_NOT_INT)
                    v.validate(decorateErrors = true)
                }
                textfield {
                    tooltip = Tooltip("Descrição")
                    tooltipProperty().onChange {
                        println()
                    }
                    validationContext.addValidator(node = this, validator = ERROR_IF_NOT_INT)
                }
//                addValidator(validationContext, ValidacaoInput.INTEIRO_POSITIVO.valid)
            }
            inputTextFieldDouble(property = elemento.real, descricoes = descricoes) {

            }
        }
    }
}

class Descricoes(
    val rb: ResourceBundle,
    val sufixoNome: String,
    val sufixoDescricao: String,
    val cssRuleTooltipDescricao: CssRule? = null,
    val cssRuleTooltipErro: CssRule? = null,
    val tooltipDelay: Double = 1000.0
) {
    fun nome(property: Property<*>): String = rb["${property.name}.${sufixoNome}"]
    fun descricao(property: Property<*>): String = rb["${property.name}.${sufixoDescricao}"]
}
/*
class Recurso(val rb: ResourceBundle, val sufixoNome: String, val sufixoDescricao: String)
 */

class DadosInput(
    val filterInput: (formatter: TextFormatter.Change) -> Boolean,
    val valid: (text: String) -> Pair<Boolean, String>
) {
    /*
    val filterInput: (formatter: TextFormatter.Change) -> Boolean,
    val valid: (text: String) -> String
     */
}

fun EventTarget.inputTextFieldInt(
    property: Property<Number>,
    descricoes: Descricoes,
    op: Field.(tf: TextField) -> Unit = {}
): Field {
    return this.inputTextField(
        property = property,
        descricoes = descricoes,
        filterInput = ValidacaoInput.INTEIRO.filterInput,
        converter = stringConverterInt,
        op = op
    )
}

fun EventTarget.inputTextFieldDouble(
    property: Property<Number>,
    descricoes: Descricoes,
    op: Field.(tf: TextField) -> Unit = {}
): Field {
    return this.inputTextField(
        property = property,
        descricoes = descricoes,
        filterInput = ValidacaoInput.REAL.filterInput,
        converter = stringConverterDouble,
        op = op
    )
}

fun <T> EventTarget.inputTextField(
    property: Property<T>,
    descricoes: Descricoes,
    filterInput: (formatter: TextFormatter.Change) -> Boolean,
    converter: StringConverter<T>,
    op: Field.(tf: TextField) -> Unit = {}
): Field {
    return this.field {
        text = descricoes.nome(property)
        val tf = textfield {
            filterInput(filterInput)
            textProperty().bindBidirectional(property, converter)
            adicionarTooltipDescricao(
                texto = descricoes.descricao(property),
                cssRule = descricoes.cssRuleTooltipDescricao,
                tooltipDelay = descricoes.tooltipDelay
            )
        }
        op(this, tf)
    }
}

private fun TextField.adicionarTooltipDescricao(texto: String, cssRule: CssRule?, tooltipDelay: Double) {
    adicionarTooltip(texto = texto, cssRule = cssRule, tooltipDelay = tooltipDelay)
}

private fun TextField.adicionarTooltipErro(texto: String, cssRule: CssRule?, tooltipDelay: Double) {
    adicionarTooltip(texto = texto, cssRule = cssRule, tooltipDelay = tooltipDelay)
}

private fun TextField.adicionarTooltip(texto: String, cssRule: CssRule?, tooltipDelay: Double) {
    val tooltip = Tooltip(texto)
    tooltip.showDelay = Duration(tooltipDelay)
    setTooltip(tooltip)
    if (cssRule != null) tooltip.addClass(cssRule)
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

private val stringConverterInt: StringConverter<Number> = object : StringConverter<Number>() {
    override fun toString(valor: Number?): String? {
        if (valor == null) return null
        return valor.toString()
    }

    override fun fromString(valor: String?): Number {
        if (valor.isNullOrBlank() || valor == "-") return 0
        return valor.toInt()
    }
}