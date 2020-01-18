package vitorscoelho.tfxutils.exemplo

import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.util.Duration
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units.METRE
import tech.units.indriya.unit.Units.SQUARE_METRE
import tornadofx.*
import vitorscoelho.tfxutils.*
import java.util.*
import javax.measure.MetricPrefix
import javax.measure.Quantity
import javax.measure.Unit

internal fun main(args: Array<String>) {
    Locale.setDefault(Locale.US)
    FX.locale = Locale("en", "US")
    quantityFactory = object : QuantityFactory {
        override fun <T : Quantity<T>> getQuantity(value: Number, unit: Unit<T>): Quantity<T> {
            return Quantities.getQuantity(value, unit)
        }
    }
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
            quantityInteiro = Quantities.getQuantity(23, METRE)
            quantityReal = Quantities.getQuantity(12.0, SQUARE_METRE)
        }
    )
    val descricoes = Descriptions(
        rb = ResourceBundle.getBundle("vitorscoelho.tfxutils.Textos"),
        nameSuffix = "nome",
        descriptionSuffix = "descricao",
        tooltipShowDelay = Duration(100.0)
    )

    val unitTeste = SimpleObjectProperty(METRE)
    override val root = formWithDescriptions(descriptions = descricoes) {
        fieldset("Dados de elemento") {
            inputTextFieldString(property = elemento.texto) {
                addValidator(validationContext) { texto ->
                    if (texto == "bleu") null else error("Deveria ser bleu")
                }
            }
            inputTextFieldInt(property = elemento.inteiro) {
                addValidator(validationContext, ERROR_IF_NEGATIVE_INT)
            }
            inputTextFieldDouble(property = elemento.real) {
                addValidator(validationContext = validationContext, validator = ERROR_IF_NOT_POSITIVE_DOUBLE)
            }
            inputTextFieldInt(property = elemento.quantityInteiro) {
                //                addValidator(validationContext=validationContext,validator = )
                addValidator(validationContext, ERROR_IF_NOT_POSITIVE_INT)
            }
            inputTextFieldDouble(property = elemento.quantityReal) {

            }
            field("OIsss")
            elemento.quantityInteiro.conectar(unitTeste)
        }
        button("OI") {
            action {
                //                elemento.quantityInteiro.value = Quantities.getQuantity(333, MetricPrefix.CENTI(METRE))
                unitTeste.value = MetricPrefix.CENTI(METRE)
            }
        }
        validationContext.validate()
    }
}