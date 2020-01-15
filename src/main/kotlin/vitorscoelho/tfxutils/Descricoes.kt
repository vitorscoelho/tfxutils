package vitorscoelho.tfxutils

import javafx.beans.property.Property
import javafx.util.Duration
import tornadofx.get
import java.util.*

class Descricoes(
    val rb: ResourceBundle,
    val sufixoNome: String,
    val sufixoDescricao: String,
    val tooltipShowDelay: Duration
) {
    fun nome(property: Property<*>): String = rb["${property.name}.${sufixoNome}"]
    fun descricao(property: Property<*>): String = rb["${property.name}.${sufixoDescricao}"]
}