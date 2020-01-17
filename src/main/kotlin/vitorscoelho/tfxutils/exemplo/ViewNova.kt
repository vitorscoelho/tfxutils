package vitorscoelho.tfxutils.exemplo

import javafx.application.Application
import javafx.scene.Node
import javafx.util.Duration
import tornadofx.*
import vitorscoelho.tfxutils.*
import java.util.*

internal fun main(args: Array<String>) {
    Locale.setDefault(Locale.US)
    FX.locale = Locale("en", "US")
    Application.launch(Aplicacao::class.java, *args)
}

internal class Aplicacao : App(ViewNova::class, EstiloPrincipal::class) {
    init {
        reloadStylesheetsOnFocus()
        val versaoJava = System.getProperty("java.version")
        val versaoJavaFX = System.getProperty("javafx.version")
        println("Versão Java: $versaoJava // Versão JavaFX: $versaoJavaFX")
    }
}

/*
class ScopeInicialVisualizador(val enderecoBancoDeDados: String) : Scope() {
    val dao = DAO(enderecoBD = enderecoBancoDeDados)
}

internal class ControllerInicialVisualizador : Controller() {
    override val scope = super.scope as ScopeInicialVisualizador
 */

internal class ViewNova : View() {
    val validationContext = ValidationContext().apply {
        decorationProvider = {
            MessageDecorator(
                message = it.message,
                severity = it.severity,
                tooltipCssRule = EstiloPrincipal.tooltipErro
            )
        }
    }
    val elemento = BeanElementoModel(
        BeanElemento().apply {
            texto = "Texto"
            inteiro = 1
            real = 2.0
        }
    )
    val descricoes = Descriptions(
        rb = ResourceBundle.getBundle("vitorscoelho.tfxutils.Textos"),
        nameSuffix = "nome",
        descriptionSuffix = "descricao",
        tooltipShowDelay = Duration(100.0)
    )
    override val root = formWithDescriptions(descriptions = descricoes) {
        fieldset("Dados de elemento") {
            //            inputTextFieldString(property = elemento.texto, mapDadosInput = mapDadosInput) {
//
//            }
            inputTextFieldInt(property = elemento.inteiro) {
                addValidator(validationContext, ERROR_IF_NEGATIVE_INT)
            }
            inputTextFieldDouble(property = elemento.real) {
                addValidator(validationContext = validationContext, validator = ERROR_IF_NOT_POSITIVE_DOUBLE)
            }
            field("OIsss")
        }
    }
}