package vitorscoelho.tfxutils

import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Control
import javafx.scene.control.Tooltip
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import tornadofx.*

class MessageDecorator(
    val message: String?,
    severity: ValidationSeverity,
    tooltipCssRule: (severity: ValidationSeverity) -> CssRule? = { null }
) :
    Decorator {
    constructor(message: String?, severity: ValidationSeverity, tooltipCssRule: CssRule? = null) :
            this(message = message, severity = severity, tooltipCssRule = { tooltipCssRule })

    val color: Color = when (severity) {
        ValidationSeverity.Error -> Color.RED
        ValidationSeverity.Warning -> Color.ORANGE
        ValidationSeverity.Success -> Color.GREEN
        else -> Color.BLUE
    }
    private val tooltipCssRule: CssRule? = tooltipCssRule(severity)
    var tag: Polygon? = null
    var tooltip: Tooltip? = null
    var attachedToNode: Node? = null

    var focusListener = ChangeListener<Boolean> { _, _, newValue ->
        if (newValue == true) showTooltip(attachedToNode!!) else tooltip?.hide()
    }

    override fun decorate(node: Node) {
        attachedToNode = node
        if (node is Parent) {
            tag = node.polygon(0.0, 0.0, 0.0, 10.0, 10.0, 0.0) {
                isManaged = false
                fill = color
            }
        }

        if (message?.isNotBlank() ?: false) {
            tooltip = Tooltip(message)
            if (tooltipCssRule != null) tooltip?.addClass(tooltipCssRule)
            if (node is Control) node.tooltip = tooltip else Tooltip.install(node, tooltip)
            if (node.isFocused) showTooltip(node)
            node.focusedProperty().addListener(focusListener)
        }
    }

    private fun showTooltip(node: Node) {
        tooltip?.apply {
            if (isShowing) return
            val b = node.localToScreen(node.boundsInLocal)
            if (b != null) show(node, b.minX + 5, b.maxY)
        }
    }

    override fun undecorate(node: Node) {
        tag?.removeFromParent()
        tooltip?.apply {
            hide()
            Tooltip.uninstall(node, this)
        }
        node.focusedProperty().removeListener(focusListener)
    }
}