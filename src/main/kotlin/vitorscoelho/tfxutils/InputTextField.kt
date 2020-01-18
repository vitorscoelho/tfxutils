package vitorscoelho.tfxutils

import javafx.beans.DefaultProperty
import javafx.beans.property.Property
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.control.Tooltip
import javafx.geometry.Orientation.HORIZONTAL
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.util.StringConverter
import tornadofx.*
import javax.measure.Quantity
import javax.measure.quantity.Length

@DefaultProperty("inputs")
class InputTextField<T> internal constructor(
    text: String? = null,
    orientation: Orientation,
    forceLabelIndent: Boolean,
    val property: Property<T>,
    val descriptions: Descriptions?,
    filterInput: (formatter: TextFormatter.Change) -> Boolean,
    converter: StringConverter<T>
) : AbstractField(text, forceLabelIndent) {
    override val inputContainer = if (orientation == HORIZONTAL) HBox() else VBox()
    override val inputs: ObservableList<Node> = inputContainer.children
    val textField = TextField()
    var value by property

    init {
        inputContainer.addClass(Stylesheet.inputContainer)
        inputContainer.addPseudoClass(orientation.name.toLowerCase())
        children.add(inputContainer)
        /*
        // Register/deregister with parent Fieldset
        parentProperty().addListener { _, oldParent, newParent ->
            ((oldParent as? Fieldset) ?: oldParent?.findParent<Fieldset>())?.fields?.remove(this)
            ((newParent as? Fieldset) ?: newParent?.findParent<Fieldset>())?.fields?.add(this)
        }
         */
    }

    init {
        if (property.value == null) throw NullPointerException("|property.value| não pode ser nulo em um InputTextField")
        colocarTitulo()
        inputContainer.add(textField)
        with(textField) {
            filterInput(filterInput)
            textProperty().bindBidirectional(property, converter)
            adicionarTooltipDescricao()
        }
    }

    fun addValidator(
        validationContext: ValidationContext,
        validator: ValidationContext.(String?) -> ValidationMessage?
    ): ValidationContext.Validator<String> {
        val validator = validationContext.addValidator(node = this.textField, validator = validator)
        validator.valid.onChange { valido -> if (valido) adicionarTooltipDescricao() }
        return validator
    }

    private fun adicionarTooltipDescricao() {
        if (descriptions != null) {
            textField.tooltip = Tooltip(descriptions.description(this.property)).apply {
                showDelay = descriptions.tooltipShowDelay
            }
        }
    }

    private fun colocarTitulo() {
        if (descriptions != null) {
            val nome = descriptions.name(property)
            if (property.value is Quantity<*>) {
                this.text = tituloQuantity(nome = nome, qtd = property.value as Quantity<*>)
                property.addListener { _, oldValue, newValue ->
                    val oldValueQtd = oldValue as Quantity<*>
                    val newValueQtd = newValue as Quantity<*>
                    if (oldValueQtd.getUnit() != newValueQtd.getUnit()) {
                        this.text = tituloQuantity(nome = nome, qtd = newValueQtd)
                    }
                }
            } else {
                this.text = nome
            }
        }
    }

    private fun tituloQuantity(nome: String, qtd: Quantity<*>): String {
        var unidade = qtd.getUnit().toString()
        unidade = if (!unidade.isBlank() && unidade != "one") " ($unidade)" else ""
        return nome + unidade
    }
}

fun <T> EventTarget.inputTextField(
    property: Property<T>,
    descriptions: Descriptions?,
    orientation: Orientation,
    forceLabelIndent: Boolean,
    filterInput: (formatter: TextFormatter.Change) -> Boolean,
    converter: StringConverter<T>,
    op: InputTextField<T>.() -> Unit = {}
): InputTextField<T> {
    val descriptionsAdotado = descriptions ?: if (this is Node) this.getDescriptions() else null
    val inputTextField = InputTextField<T>(
        text = null,
        orientation = orientation,
        forceLabelIndent = forceLabelIndent,
        property = property,
        descriptions = descriptionsAdotado,
        filterInput = filterInput,
        converter = converter
    )
    opcr(this, inputTextField) {}
    op(inputTextField)
    return inputTextField
}

fun EventTarget.inputTextFieldInt(
    property: Property<Number>,
    descriptions: Descriptions? = null,
    orientation: Orientation = HORIZONTAL,
    forceLabelIndent: Boolean = false,
    op: InputTextField<Number>.() -> Unit = {}
): InputTextField<Number> {
    return this.inputTextField(
        property = property,
        descriptions = descriptions,
        orientation = orientation,
        forceLabelIndent = forceLabelIndent,
        filterInput = FILTER_INPUT_INT,
        converter = STRING_CONVERTER_INT,
        op = op
    )
}

fun EventTarget.inputTextFieldPositiveInt(
    property: Property<Number>,
    descriptions: Descriptions? = null,
    orientation: Orientation = HORIZONTAL,
    forceLabelIndent: Boolean = false,
    op: InputTextField<Number>.() -> Unit = {}
): InputTextField<Number> {
    return this.inputTextField(
        property = property,
        descriptions = descriptions,
        orientation = orientation,
        forceLabelIndent = forceLabelIndent,
        filterInput = FILTER_INPUT_POSITIVE_INT,
        converter = STRING_CONVERTER_INT,
        op = op
    )
}

fun EventTarget.inputTextFieldDouble(
    property: Property<Number>,
    descriptions: Descriptions? = null,
    orientation: Orientation = HORIZONTAL,
    forceLabelIndent: Boolean = false,
    op: InputTextField<Number>.() -> Unit = {}
): InputTextField<Number> {
    return this.inputTextField(
        property = property,
        descriptions = descriptions,
        orientation = orientation,
        forceLabelIndent = forceLabelIndent,
        filterInput = FILTER_INPUT_REAL,
        converter = STRING_CONVERTER_DOUBLE,
        op = op
    )
}

fun EventTarget.inputTextFieldPositiveDouble(
    property: Property<Number>,
    descriptions: Descriptions? = null,
    orientation: Orientation = HORIZONTAL,
    forceLabelIndent: Boolean = false,
    op: InputTextField<Number>.() -> Unit = {}
): InputTextField<Number> {
    return this.inputTextField(
        property = property,
        descriptions = descriptions,
        orientation = orientation,
        forceLabelIndent = forceLabelIndent,
        filterInput = FILTER_INPUT_POSITIVE_REAL,
        converter = STRING_CONVERTER_DOUBLE,
        op = op
    )
}

fun EventTarget.inputTextFieldString(
    property: Property<String>,
    descriptions: Descriptions? = null,
    orientation: Orientation = HORIZONTAL,
    forceLabelIndent: Boolean = false,
    op: InputTextField<String>.() -> Unit = {}
): InputTextField<String> {
    return this.inputTextField(
        property = property,
        descriptions = descriptions,
        orientation = orientation,
        forceLabelIndent = forceLabelIndent,
        filterInput = FILTER_INPUT_ANY,
        converter = STRING_CONVERTER_STRING,
        op = op
    )
}

@JvmName("inputTextFieldIntQuantity")
fun <T : Quantity<T>> EventTarget.inputTextFieldInt(
    property: Property<Quantity<T>>,
    descriptions: Descriptions? = null,
    orientation: Orientation = HORIZONTAL,
    forceLabelIndent: Boolean = false,
    op: InputTextField<Quantity<T>>.() -> Unit = {}
): InputTextField<Quantity<T>> {
    return this.inputTextField<Quantity<T>>(
        property = property,
        descriptions = descriptions,
        orientation = orientation,
        forceLabelIndent = forceLabelIndent,
        filterInput = FILTER_INPUT_INT,
        converter = stringConverterQuantity(property = property, converterNumber = STRING_CONVERTER_INT),
        op = op
    )
}

@JvmName("inputTextFieldPositiveIntQuantity")
fun <T : Quantity<T>> EventTarget.inputTextFieldPositiveInt(
    property: Property<Quantity<T>>,
    descriptions: Descriptions? = null,
    orientation: Orientation = HORIZONTAL,
    forceLabelIndent: Boolean = false,
    op: InputTextField<Quantity<T>>.() -> Unit = {}
): InputTextField<Quantity<T>> {
    return this.inputTextField(
        property = property,
        descriptions = descriptions,
        orientation = orientation,
        forceLabelIndent = forceLabelIndent,
        filterInput = FILTER_INPUT_POSITIVE_INT,
        converter = stringConverterQuantity(property = property, converterNumber = STRING_CONVERTER_INT),
        op = op
    )
}

@JvmName("inputTextFieldDoubleQuantity")
fun <T : Quantity<T>> EventTarget.inputTextFieldDouble(
    property: Property<Quantity<T>>,
    descriptions: Descriptions? = null,
    orientation: Orientation = HORIZONTAL,
    forceLabelIndent: Boolean = false,
    op: InputTextField<Quantity<T>>.() -> Unit = {}
): InputTextField<Quantity<T>> {
    return this.inputTextField(
        property = property,
        descriptions = descriptions,
        orientation = orientation,
        forceLabelIndent = forceLabelIndent,
        filterInput = FILTER_INPUT_REAL,
        converter = stringConverterQuantity(property = property, converterNumber = STRING_CONVERTER_DOUBLE),
        op = op
    )
}

@JvmName("inputTextFieldPositiveDoubleQuantity")
fun <T : Quantity<T>> EventTarget.inputTextFieldPositiveDouble(
    property: Property<Quantity<T>>,
    descriptions: Descriptions? = null,
    orientation: Orientation = HORIZONTAL,
    forceLabelIndent: Boolean = false,
    op: InputTextField<Quantity<T>>.() -> Unit = {}
): InputTextField<Quantity<T>> {
    return this.inputTextField(
        property = property,
        descriptions = descriptions,
        orientation = orientation,
        forceLabelIndent = forceLabelIndent,
        filterInput = FILTER_INPUT_POSITIVE_REAL,
        converter = stringConverterQuantity(property = property, converterNumber = STRING_CONVERTER_DOUBLE),
        op = op
    )
}