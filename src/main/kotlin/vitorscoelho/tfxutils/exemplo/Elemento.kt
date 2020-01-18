package vitorscoelho.tfxutils.exemplo

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue
import javax.measure.Quantity
import javax.measure.quantity.Area
import javax.measure.quantity.Length

internal class BeanElemento {
    val textoProperty = SimpleStringProperty(null, "elemento.texto")
    var texto by textoProperty
    val inteiroProperty = SimpleIntegerProperty(null, "elemento.inteiro")
    var inteiro by inteiroProperty
    val realProperty = SimpleDoubleProperty(null, "elemento.real")
    var real by realProperty
    val quantityInteiroProperty = SimpleObjectProperty<Quantity<Length>>(null, "elemento.inteiro_quantity")
    var quantityInteiro by quantityInteiroProperty
    val quantityRealProperty = SimpleObjectProperty<Quantity<Area>>(null, "elemento.real_quantity")
    var quantityReal by quantityRealProperty
}

internal class BeanElementoModel(item: BeanElemento) : ItemViewModel<BeanElemento>(initialValue = item) {
    val texto = bind(BeanElemento::textoProperty)
    val inteiro = bind(BeanElemento::inteiroProperty)
    val real = bind(BeanElemento::realProperty)
    val quantityInteiro = bind(BeanElemento::quantityInteiroProperty)
    val quantityReal = bind(BeanElemento::quantityRealProperty)
}