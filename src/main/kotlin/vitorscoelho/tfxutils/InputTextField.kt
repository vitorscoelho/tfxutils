package vitorscoelho.tfxutils

import javafx.beans.DefaultProperty
import javafx.beans.property.Property
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.control.Tooltip
import javafx.scene.layout.Region
import javafx.util.StringConverter
import tornadofx.*

@DefaultProperty("inputs")
class InputTextField(
    private val fieldTfx: Field,
    val textField: TextField,
    val property: Property<*>,
    val descricoes: Descricoes
) : AbstractField(fieldTfx.text, fieldTfx.forceLabelIndent) {
    override val inputContainer: Region
        get() = fieldTfx.inputContainer

    override val inputs: ObservableList<Node>
        get() = fieldTfx.inputs

    fun addValidator(
        validationContext: ValidationContext,
        validator: ValidationContext.(String?) -> ValidationMessage?
    ): ValidationContext.Validator<String> {
        val validator = validationContext.addValidator(node = this.textField, validator = validator)
        validator.valid.onChange { valido ->
            if (valido) {
                this.textField.tooltip = Tooltip(this.descricoes.descricao(this.property))
                this.textField.tooltip.showDelay = this.descricoes.tooltipShowDelay
            }
        }
        return validator
    }
}

fun <T> EventTarget.inputTextField(
    property: Property<T>,
    descricoes: Descricoes,
    filterInput: (formatter: TextFormatter.Change) -> Boolean,
    converter: StringConverter<T>,
    op: InputTextField.(tf: TextField) -> Unit = {}
): InputTextField {
    var tf: TextField? = null

    val fieldTfx = this.field {
        text = descricoes.nome(property)
        tf = textfield {
            filterInput(filterInput)
            textProperty().bindBidirectional(property, converter)
            tooltip = Tooltip(descricoes.descricao(property))
            tooltip.showDelay = descricoes.tooltipShowDelay
        }
    }
    val inputTextField = InputTextField(
        fieldTfx = fieldTfx, textField = tf!!, property = property, descricoes = descricoes
    )
    op(inputTextField, tf!!)
    return inputTextField
}

fun EventTarget.inputTextFieldInt(
    property: Property<Number>,
    descricoes: Descricoes,
    op: InputTextField.(tf: TextField) -> Unit = {}
): InputTextField {
    return this.inputTextField(
        property = property,
        descricoes = descricoes,
        filterInput = FILTER_INPUT_INT,
        converter = STRING_CONVERTER_INT,
        op = op
    )
}

fun EventTarget.inputTextFieldDouble(
    property: Property<Number>,
    descricoes: Descricoes,
    op: InputTextField.(tf: TextField) -> Unit = {}
): InputTextField {
    return this.inputTextField(
        property = property,
        descricoes = descricoes,
        filterInput = FILTER_INPUT_REAL,
        converter = STRING_CONVERTER_DOUBLE,
        op = op
    )
}