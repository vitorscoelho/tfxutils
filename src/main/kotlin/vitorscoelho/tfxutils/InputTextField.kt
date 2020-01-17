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
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.util.StringConverter
import tornadofx.*

@DefaultProperty("inputs")
class InputTextField<T>(
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
        this.text = descriptions?.name(property)
//        with(this.label){
//            prefWidth = Region.USE_COMPUTED_SIZE
//            minWidth = Region.USE_PREF_SIZE
//            maxWidth = Region.USE_PREF_SIZE
//        }
//        with(this.labelContainer){
//            prefWidth = Region.USE_COMPUTED_SIZE
//            minWidth = Region.USE_PREF_SIZE
//            maxWidth = Region.USE_PREF_SIZE
//        }
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
        validator.valid.onChange { valido ->
            if (valido) {
                adicionarTooltipDescricao()
            }
        }
        return validator
    }

    private fun adicionarTooltipDescricao() {
        if (descriptions != null) {
            textField.tooltip = Tooltip(descriptions.description(this.property)).apply {
                showDelay = descriptions.tooltipShowDelay
            }
        }
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