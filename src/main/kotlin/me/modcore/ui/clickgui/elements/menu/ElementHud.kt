package me.modcore.ui.clickgui.elements.menu

import me.modcore.features.impl.render.ClickGUIModule
import me.modcore.features.settings.impl.HudSetting
import me.modcore.font.FontRenderer
import me.modcore.ui.clickgui.ClickGUI
import me.modcore.ui.clickgui.ClickGUI.TEXTOFFSET
import me.modcore.ui.clickgui.animations.impl.ColorAnimation
import me.modcore.ui.clickgui.animations.impl.LinearAnimation
import me.modcore.ui.clickgui.elements.*
import me.modcore.ui.clickgui.util.ColorUtil
import me.modcore.ui.clickgui.util.ColorUtil.brighter
import me.modcore.ui.clickgui.util.ColorUtil.buttonColor
import me.modcore.ui.clickgui.util.ColorUtil.clickGUIColor
import me.modcore.ui.clickgui.util.ColorUtil.darker
import me.modcore.ui.clickgui.util.ColorUtil.darkerIf
import me.modcore.ui.clickgui.util.ColorUtil.textColor
import me.modcore.ui.clickgui.util.HoverHandler
import me.modcore.ui.hud.EditHUDGui
import me.modcore.ui.util.MouseUtils.isAreaHovered
import me.modcore.utils.render.*
import me.modcore.utils.render.RenderUtils.loadBufferedImage
import net.minecraft.client.renderer.texture.DynamicTexture


/**
 * Renders all the modules.
 *
 * Backend made by Aton, with some changes
 * Design mostly made by Stivais
 *
 * @author Stivais, Aton
 * @see [Element]
 */
class ElementHud(parent: ModuleButton, setting: HudSetting) : Element<HudSetting>(
    parent, setting, ElementType.DUAL
) {
    override val isHovered: Boolean
        get() = setting.displayToggle && isAreaHovered(x + w - 30f, y + 5f, 21f, 20f)

    private val isShortcutHovered: Boolean
        get() {
            return if (setting.displayToggle) isAreaHovered(x + w - 60f, y + 5f, 21f, 20f)
            else isAreaHovered(x + w - 30f, y + 5f, 21f, 20f)
        }

    private val movementIcon = DynamicTexture(loadBufferedImage("/assets/odinmain/clickgui/MovementIcon.png"))
    private val colorAnim = ColorAnimation(250)
    private val hover = HoverHandler(0, 150)
    private val linearAnimation = LinearAnimation<Float>(200)

    override fun draw() {
        roundedRectangle(x, y, w, h, ColorUtil.elementBackground)
        text(name, x + TEXTOFFSET, y + 18f, textColor, 12f, FontRenderer.REGULAR)

        var offset = 30f
        if (setting.displayToggle) {
            hover.handle(x + w - 30f, y + 5f, 21f, 20f)
            val color = colorAnim.get(clickGUIColor, buttonColor, setting.enabled).brighter(1 + hover.percent() / 500f)
            if (!ClickGUIModule.switchType) {
                dropShadow(x + w - offset, y + 5f, 21f, 20f, 10f, 0.75f)
                roundedRectangle(x + w - offset, y + 5f, 21f, 20f, color, 5f)
                rectangleOutline(x + w - offset, y + 5f, 21f, 20f, clickGUIColor, 5f, 3f)
                offset = 60f
            } else {
                dropShadow(x + w - 43f, y + 4f, 34f, 20f, 10f, 0.75f)

                roundedRectangle(x + w - 43f, y + 4f, 34f, 20f, buttonColor, 9f)
                if (setting.enabled || linearAnimation.isAnimating()) roundedRectangle(x + w - 43f, y + 4f, linearAnimation.get(34f, 9f, setting.enabled), 20f, color, 9f)

                if (isHovered) rectangleOutline(x + w - 43f, y + 4f, 34f, 20f, color.darker(.85f), 9f, 3f)
                circle(x + w - linearAnimation.get(33f, 17f, !setting.enabled), y + 14f, 6f,
                    Color(220, 220, 220).darkerIf(isHovered, 0.9f)
                )
                offset = 70f
            }
        }
        drawDynamicTexture(
            movementIcon, x + w - offset, y + 5f, 20f, 20f
        )
    }

    override fun mouseClicked(mouseButton: Int): Boolean {
        if (mouseButton == 0) {
            when {
                isHovered -> if (colorAnim.start()) {
                    setting.enabled = !setting.enabled
                    setting.value.enabledSetting.value = setting.enabled
                }
                isShortcutHovered -> ClickGUI.swapScreens(EditHUDGui)
                else -> return false
            }
            return true
        }
        return false
    }
}