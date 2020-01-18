package vitorscoelho.tfxutils

import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import tornadofx.onChange
import javax.measure.Quantity
import javax.measure.Unit

fun <T : Quantity<T>> Property<Quantity<T>>.conectar(unitProperty: ObjectProperty<Unit<T>>) {
    val mudar = { value = value.to(unitProperty.value) }
    mudar()
    unitProperty.onChange { mudar() }
//    onChange { unitProperty.value = value.unit }
}