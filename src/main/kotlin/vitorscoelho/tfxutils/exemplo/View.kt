package vitorscoelho.tfxutils.exemplo

import javafx.application.Application
import javafx.scene.control.Tooltip
import tornadofx.*
import vitorscoelho.tfxutils.Contexto
import vitorscoelho.tfxutils.Recurso
import java.util.*

class ViewInicial : View() {
    val contexto = Contexto(
        Recurso(
            rb = ResourceBundle.getBundle("vitorscoelho.tfxutils.Textos"),
            sufixoNome = "nome",
            sufixoDescricao = "descricao"
        )
    )
    val elemento = BeanElementoModel(
        BeanElemento().apply {
            texto = "Texto"
            inteiro = 1
            real = 2.0
            /*
            val texto = bind(BeanElemento::textoProperty)
    val inteiro = bind(BeanElemento::inteiroProperty)
    val real = bind(BeanElemento::realProperty)
    val quantity = bind(BeanElemento::quantityProperty)
             */
        },
        contexto
    )

//    val context: PropertiesContext
//    override val root = formWithContext(context = context) {
//        fieldset("Retângulo") {
//            fieldDescritivo(property = retangulo.lx)
//            fieldDescritivo(property = retangulo.ly)
//        }
//    }

    override val root = form {
        fieldset("Elemento") {
            val propertyInteiro = elemento.inteiro
            val dadosInteiro = contexto.get(propertyInteiro)!!
            field(dadosInteiro.nome) {
                textfield(property = propertyInteiro, converter = dadosInteiro.converter) {
                    tooltip = Tooltip(dadosInteiro.descricao)
                    filterInput(dadosInteiro.filterInput)
                }
            }
            val propertyReal = elemento.real
            val dadosReal = contexto.get(propertyReal)!!
            field(dadosReal.nome) {
                textfield(property = propertyReal, converter = dadosReal.converter) {
                    tooltip = Tooltip(dadosReal.descricao)
                    filterInput(dadosReal.filterInput)
                }
            }
//            field()
            //            field(dados.nome) {
//                textfield(property = property) {
//                    tooltip = Tooltip(dados.descricao)
//                    filterInput(dados.filterInput)
//                }
//            }
//            val property = retangulo.lx
//            val dados = contexto.get(property)!!
//            field(dados.nome) {
//                textfield(property = retangulo.lx, converter = dados.converter) {
//                    tooltip = Tooltip(dados.descricao)
//                    filterInput(dados.filterInput)
//                }
//            }
//            field("Ly (m)") {
//                textfield(property = retangulo.ly) { }
//            }
        }
    }
}

fun main(args: Array<String>) {
    Locale.setDefault(Locale.US)
    FX.locale = Locale("en", "US")
    Application.launch(Aplicacao::class.java, *args)
}

class Aplicacao : App(ViewInicial::class) {
    init {
        reloadStylesheetsOnFocus()
        val versaoJava = System.getProperty("java.version")
        val versaoJavaFX = System.getProperty("javafx.version")
        println("Versão Java: $versaoJava // Versão JavaFX: $versaoJavaFX")
    }
}