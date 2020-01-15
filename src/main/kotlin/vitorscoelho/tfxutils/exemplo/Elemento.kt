package vitorscoelho.tfxutils.exemplo

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue
import javax.measure.Quantity
import javax.measure.quantity.Length

internal class BeanElemento {
    val textoProperty = SimpleStringProperty(null, "elemento.texto")
    var texto by textoProperty
    val inteiroProperty = SimpleIntegerProperty(null, "elemento.inteiro")
    var inteiro by inteiroProperty
    val realProperty = SimpleDoubleProperty(null, "elemento.real")
    var real by realProperty
//    val quantityProperty = SimpleObjectProperty<Quantity<Length>>(null, "elemento.quantity")
//    var quantity by quantityProperty
}

internal class BeanElementoModel(item: BeanElemento) : ItemViewModel<BeanElemento>(initialValue = item) {
    val texto = bind(BeanElemento::textoProperty)
    val inteiro = bind(BeanElemento::inteiroProperty)
    val real = bind(BeanElemento::realProperty)
//    val quantity = bind(BeanElemento::quantityProperty)
}


//class BeanElementoModel(item: BeanElemento, val contexto: Contexto) :
//    ItemViewModel<BeanElemento>(initialValue = item) {
//    val nome = bind(BeanElemento::nomeProperty)
//    val lx: SimpleIntegerProperty = bind(BeanElemento::lxProperty)
//    val ly = bind(BeanElemento::lyProperty)
//    /*
//    TIPOS PARA TESTAR:
//    - STRING
//    - INT
//    - DOUBLE
//    - QUANTITY
//     */
//
//    init {
//        nome.value = "Um retângulo"
//        lx.value = 0
//        ly.value = 0.0
//        contexto.add(lx) {
//            value = 2.0
//            inteiroPositivo()
//        }
////        contexto.add(nome) {
////            nome = "ls"
////            descricao = "Descrição do nome do retângulo"
////            filterInput = { formatter -> formatter.controlNewText.isInt() || formatter.controlNewText == "-" }
////        }
////        contexto.add(
////            property = nome,
////            dados = object : DadosInput<String>(
////                nome = "Lx",
////                descricao = "Descrição do nome do retângulo",
////                converter = DefaultStringConverter(),
////                filterInput = { formatter -> formatter.controlNewText.isInt() || formatter.controlNewText == "-" },
////                valid = { true },
////                errorMessage = "Não pode ser este valor"
////            ) {}
////        )
//    }

/*
Ideia para não ter que fazer muitas gambiarras.
Fazer com que a property (ou o bind) sejam "cadastrados" em um map e que o field busque a property neste map (que pode estar em um PropertiesContext) para pegar as informações de nome, descrição, etc...
Buscar pelo 'name' da property provavelmente será mais fácil do que buscar pela property em si. Ou seja talvez seja melhor um método 'add(propertyName:String)'

Exemplo:

class PropertiesContext{
    private val map = hashmap<Property<*>, DadosAdicionaisInput>
    fun add(prop:Property<T>):DadosAdicionaisInput {
        val dados = DadosAdicionaisInput()
        map[prop] = dados
        return dados
    }
}

class BeanRetangulo {
    init {
        propertiesContext.add(lx).apply {
            nome = "Lx"
            descricao = "Largura do retângulo"
            .......
        }
    }
}
*/
//}
