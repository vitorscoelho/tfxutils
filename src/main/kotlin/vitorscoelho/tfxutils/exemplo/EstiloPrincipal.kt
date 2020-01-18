package vitorscoelho.tfxutils.exemplo

import javafx.geometry.Pos
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import tornadofx.*

internal class EstiloPrincipal : Stylesheet() {
    init {
        val fontSizePadrao = 13.px
        val fontSizeTooltip = fontSizePadrao
        root {
            fontSize = fontSizePadrao
        }
        tooltipDescricao {
            backgroundColor = multi(Color.GRAY)
            fontSize = fontSizeTooltip
        }
        tooltipErro {
            backgroundColor = multi(Color.RED)
            fontSize = fontSizeTooltip
        }
        val larguraFieldset = 180.px
        val larguraBotaoETextArea = larguraFieldset * 2.0
        val spacingVBox = 20.px
        form {
            fontSize = fontSizePadrao
            legend {
                fontSize = fontSizePadrao * 1.2
            }
            hbox {
                spacing = 20.px
            }
            field {
//                setMinMaxPrefWidth(larguraFieldset)
                label{
                    prefWidth = 300.px
                    maxWidth = 300.px
                }
                textField {
                    prefWidth = 300.px
                    maxWidth = 300.px
                    alignment = Pos.CENTER_RIGHT
                }
            }
        }
        vboxDados {
            fontSize = fontSizePadrao
            alignment = Pos.TOP_CENTER
            spacing = spacingVBox
            setMinMaxPrefWidth(Region.USE_COMPUTED_SIZE.px)
            setMinMaxPrefHeight(Region.USE_COMPUTED_SIZE.px)
            form {
                fontSize = fontSizePadrao
                legend {
                    fontSize = fontSizePadrao * 1.2
                }
                hbox {
                    spacing = 20.px
                }
                field {
                    setMinMaxPrefWidth(larguraFieldset)
                    textField {
                        prefWidth = larguraFieldset
                        maxWidth = larguraFieldset
                        alignment = Pos.CENTER_RIGHT
                    }
                }
            }
            button {
                setMinMaxPrefWidth(larguraBotaoETextArea)
                fontSize = fontSizePadrao * 1.5
            }
            textArea {
                setMinMaxPrefWidth(larguraBotaoETextArea)
            }
        }
        vboxCanvas {
            fontSize = fontSizePadrao * 1.2
        }
    }

    private fun PropertyHolder.setMinMaxPrefWidth(valor: Dimension<Dimension.LinearUnits>) {
        prefWidth = valor; minWidth = valor; maxWidth = valor
    }

    private fun PropertyHolder.setMinMaxPrefHeight(valor: Dimension<Dimension.LinearUnits>) {
        prefHeight = valor; minHeight = valor; maxHeight = valor
    }

    companion object {
        private val gridpane by csselement("GridPane")
        private val hbox by csselement("HBox")
        val tooltipDescricao by cssclass()
        val tooltipErro by cssclass()
        val vboxDados by cssclass()
        val vboxCanvas by cssclass()
    }
}