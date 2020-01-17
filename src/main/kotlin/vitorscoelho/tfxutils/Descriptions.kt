package vitorscoelho.tfxutils

import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventTarget
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.Parent
import javafx.util.Duration
import tornadofx.Fieldset
import tornadofx.Form
import tornadofx.get
import tornadofx.opcr
import java.util.*

class Descriptions(
    val rb: ResourceBundle,
    val nameSuffix: String,
    val descriptionSuffix: String,
    val tooltipShowDelay: Duration
) {
    fun name(property: Property<*>): String = rb["${property.name}.${nameSuffix}"]
    fun description(property: Property<*>): String = rb["${property.name}.${descriptionSuffix}"]
}

interface WithDescriptions {
    val descriptions: Descriptions
}

class FormWithDescriptions(override val descriptions: Descriptions) : Form(), WithDescriptions

fun EventTarget.formWithDescriptions(descriptions: Descriptions, op: FormWithDescriptions.() -> Unit = {}) =
    opcr(this, FormWithDescriptions(descriptions = descriptions), op)

class FieldSetWithDescriptions(
    text: String? = null,
    labelPosition: Orientation = Orientation.HORIZONTAL,
    override var descriptions: Descriptions
) :
    Fieldset(text = text, labelPosition = labelPosition), WithDescriptions

fun EventTarget.fieldsetWithDescriptions(
    text: String? = null,
    descriptions: Descriptions,
    icon: Node? = null,
    labelPosition: Orientation? = null,
    wrapWidth: Double? = null,
    op: Fieldset.() -> Unit = {}
): FieldSetWithDescriptions {
    val fieldset = FieldSetWithDescriptions(text = text ?: "", descriptions = descriptions)
    if (wrapWidth != null) fieldset.wrapWidth = wrapWidth
    if (labelPosition != null) fieldset.labelPosition = labelPosition
    if (icon != null) fieldset.icon = icon
    opcr(this, fieldset, op)
    return fieldset
}

fun Node.getDescriptions(): Descriptions? {
    if (this is WithDescriptions && this.descriptions != null) return this.descriptions
    var nodeAtual: Parent? = this.parent
    while (nodeAtual != null) {
        if (nodeAtual is WithDescriptions && nodeAtual.descriptions != null) return nodeAtual.descriptions
        nodeAtual = nodeAtual.parent
    }
    return null
}