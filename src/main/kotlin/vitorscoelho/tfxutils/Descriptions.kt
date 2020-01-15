package vitorscoelho.tfxutils

import javafx.beans.property.Property
import javafx.util.Duration
import tornadofx.get
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