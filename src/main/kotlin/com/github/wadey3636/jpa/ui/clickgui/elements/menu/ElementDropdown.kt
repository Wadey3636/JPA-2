package com.github.wadey3636.jpa.ui.clickgui.elements.menu

import com.github.wadey3636.jpa.features.settings.impl.DropdownSetting
import com.github.wadey3636.jpa.font.FontRenderer
import com.github.wadey3636.jpa.ui.clickgui.ClickGUI.TEXTOFFSET
import com.github.wadey3636.jpa.ui.clickgui.animations.impl.LinearAnimation
import com.github.wadey3636.jpa.ui.clickgui.elements.Element
import com.github.wadey3636.jpa.ui.clickgui.elements.ElementType
import com.github.wadey3636.jpa.ui.clickgui.elements.ModuleButton
import com.github.wadey3636.jpa.ui.clickgui.util.ColorUtil.elementBackground
import com.github.wadey3636.jpa.ui.clickgui.util.ColorUtil.textColor
import com.github.wadey3636.jpa.ui.util.MouseUtils.isAreaHovered
import com.github.wadey3636.jpa.utils.render.drawArrow
import com.github.wadey3636.jpa.utils.render.roundedRectangle
import com.github.wadey3636.jpa.utils.render.text

/**
 * Renders all the modules.
 *
 * Backend made by Aton, with some changes
 * Design mostly made by Stivais
 *
 * @author Stivais, Aton
 * @see [Element]
 */
class ElementDropdown(parent: ModuleButton, setting: DropdownSetting) : Element<DropdownSetting>(
    parent, setting, ElementType.DROPDOWN
) {
    private val linearAnimation = LinearAnimation<Float>(200)

    override val isHovered: Boolean
        get() =
            isAreaHovered(x, y, w, h)

    override fun draw() {
        roundedRectangle(x, y, w, h, elementBackground)
        text(name, x + TEXTOFFSET, y + h / 2f, textColor, 12f, FontRenderer.REGULAR)

        val rotation = linearAnimation.get(90f, 0f, !setting.value)
        drawArrow(x + w - 12f, y + 16, rotation, scale = 0.8f)
    }

    override fun mouseClicked(mouseButton: Int): Boolean {
        if (isHovered) {
            if (linearAnimation.start()) {
                setting.enabled = !setting.enabled
                parent.updateElements()
                return true
            }
        }
        return false
    }

}