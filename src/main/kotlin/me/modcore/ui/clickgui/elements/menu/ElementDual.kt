package me.modcore.ui.clickgui.elements.menu

import me.modcore.features.settings.impl.DualSetting
import me.modcore.font.FontRenderer
import me.modcore.ui.clickgui.animations.impl.EaseInOut
import me.modcore.ui.clickgui.elements.Element
import me.modcore.ui.clickgui.elements.ElementType
import me.modcore.ui.clickgui.elements.ModuleButton
import me.modcore.ui.clickgui.util.ColorUtil.buttonColor
import me.modcore.ui.clickgui.util.ColorUtil.clickGUIColor
import me.modcore.ui.clickgui.util.ColorUtil.darker
import me.modcore.ui.clickgui.util.ColorUtil.darkerIf
import me.modcore.ui.clickgui.util.ColorUtil.elementBackground
import me.modcore.ui.util.MouseUtils
import me.modcore.utils.render.*

/**
 * Renders all the modules.
 *
 * Backend made by Aton, with some changes
 * Design mostly made by Stivais
 *
 * @author Stivais, Aton
 * @see [Element]
 */
class ElementDual(parent: ModuleButton, setting: DualSetting) : Element<DualSetting>(
    parent, setting, ElementType.DUAL
) {
    private val posAnim = EaseInOut(250)

    private val isRightHovered: Boolean
        get() = MouseUtils.isAreaHovered(x + w / 2 + 5f, y + 2f, w / 2 - 10f, 30f)

    private val isLeftHovered: Boolean
        get() = MouseUtils.isAreaHovered(x + 5f, y + 2f, w / 2 - 10f, 30f)

    override fun draw() {
        roundedRectangle(x, y, w, h, elementBackground)
        dropShadow(x + 7f, y + 3f, w - 14f, 28f, 10f, 3.75f)
        roundedRectangle(x + 7f, y + 3f, w - 14f, 28f, buttonColor, 5f)

        val pos = posAnim.get(8f, w / 2, !setting.enabled)
        roundedRectangle(x + pos, y + 3f, w / 2 - 6f, 28f, clickGUIColor.darker(0.8f), 5f)

        text(
            setting.left,
            x + w / 4 + 6f,
            y + 1f + h / 2,
            Color.WHITE.darkerIf(isLeftHovered),
            12f,
            FontRenderer.REGULAR,
            TextAlign.Middle
        )
        text(
            setting.right,
            x + w * 3 / 4 - 3f,
            y + 1f + h / 2,
            Color.WHITE.darkerIf(isRightHovered),
            12f,
            FontRenderer.REGULAR,
            TextAlign.Middle
        )
    }

    override fun mouseClicked(mouseButton: Int): Boolean {
        if (mouseButton != 0) return false
        if (isLeftHovered && setting.enabled) {
            if (posAnim.start()) setting.enabled = false
            return true
        } else if (isRightHovered && !setting.enabled) {
            if (posAnim.start()) setting.enabled = true
            return true
        }
        return false
    }
}