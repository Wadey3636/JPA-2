package me.modcore.ui.playerCustomizerGUI

import com.github.wadey3636.jpa.features.render.PlayerEntry
import me.modcore.features.impl.render.ClickGUIModule
import me.modcore.font.FontRenderer
import me.modcore.ui.clickgui.animations.impl.ColorAnimation
import me.modcore.ui.clickgui.animations.impl.LinearAnimation
import me.modcore.ui.clickgui.util.ColorUtil
import me.modcore.ui.clickgui.util.ColorUtil.brighter
import me.modcore.ui.clickgui.util.ColorUtil.brighterIf
import me.modcore.ui.clickgui.util.ColorUtil.buttonColor
import me.modcore.ui.clickgui.util.ColorUtil.clickGUIColor
import me.modcore.ui.clickgui.util.ColorUtil.darker
import me.modcore.ui.clickgui.util.ColorUtil.darkerIf
import me.modcore.ui.clickgui.util.ColorUtil.elementBackground
import me.modcore.ui.clickgui.util.ColorUtil.textColor
import me.modcore.ui.clickgui.util.HoverHandler
import me.modcore.ui.playerCustomizerGUI.PlayerCustomizerGUI.mc
import me.modcore.ui.util.MouseUtils.isAreaHovered
import me.modcore.ui.util.MouseUtils.mouseX
import me.modcore.utils.render.*
import me.modcore.utils.skyblock.devMessage
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.GlStateManager
import kotlin.math.roundToInt




//I cooked this up at 4 AM dont judge. IK this is fucked, but I can't be asked to improve it :)
//If someone for some ungodly reason is taking example from this mod. Ignore this entire GUI.
//Look at ClickGUI instead because this is an ungodly horror that I shat out after
//consuming an entire container of advil and snorting 2 lines of coke.
//Still better than whiteshdow's coding >:)


class CustomPlayerInstance(private val playerEntry: PlayerEntry) {
    private val sliders: List<ElementSlider> = listOf(
        ElementSlider("Scale X", 0.1, 10.0, "", playerEntry, 0.1, Option.X, 612.0f, 10f, 500f, 104f),
        ElementSlider("Scale Y", 0.1, 10.0, "", playerEntry, 0.1, Option.Y, 612.0f, 10f, 500f, 104f),
        ElementSlider("Scale Z", 0.1, 10.0, "", playerEntry, 0.1, Option.Z, 612.0f, 10f, 500f, 104f)
    )


    private val boxVerticalOffset: Float = 10f
    private val switchVerticalOffset: Float = 8f


    private val colorAnimations: List<ColorAnimation> = listOf(ColorAnimation(250), ColorAnimation(250), ColorAnimation(250), ColorAnimation(250), ColorAnimation(250))
    private val animations: List<LinearAnimation<Float>> = listOf(LinearAnimation<Float>(200), LinearAnimation<Float>(200), LinearAnimation<Float>(200), LinearAnimation<Float>(200), LinearAnimation<Float>(200))
    val x = 60f
    val width = 1050f
    val height = 450f
    val toggleSwitchX = 350
    val switchOffset = -25f
    val switchHeight = 78f
    private val nameElement: ElementTextBox = ElementTextBox("Name", playerEntry, x + toggleSwitchX, 24f, 10f, 20f)

    fun mouseClickedAnywhere(state: Int){
        for (slider in sliders) {
            slider.mouseClickedAnywhere(state)
        }
    }


    fun mouseClicked(y: Float){
        for (slider in sliders) {
            slider.mouseClicked()
        }

        if(
            if (!ClickGUIModule.switchType) isAreaHovered(x + toggleSwitchX+ switchOffset, y + 29f, 42f, 40f)
            else isAreaHovered(x + switchOffset + toggleSwitchX, y + 48f, 68f, 40f)
            ) {
            if (colorAnimations[0].start()) {
                animations[0].start()
                playerEntry.toggle = !playerEntry.toggle
                return
            }

        }
        if(
            if (!ClickGUIModule.switchType) isAreaHovered(x + toggleSwitchX+ switchOffset, y + 29f + switchHeight, 42f, 40f)
            else isAreaHovered(x + switchOffset + toggleSwitchX, y + 48f + switchHeight, 68f, 40f)
        ) {
            if (colorAnimations[1].start()) {
                animations[1].start()
                playerEntry.hideHelmet = !playerEntry.hideHelmet
                return
            }

        }
        if(
            if (!ClickGUIModule.switchType) isAreaHovered(x + toggleSwitchX+ switchOffset, y + 29f + switchHeight * 2, 42f, 40f)
            else isAreaHovered(x + switchOffset + toggleSwitchX, y + 48f + switchHeight * 2, 68f, 40f)
        ) {
            if (colorAnimations[2].start()) {
                animations[2].start()
                playerEntry.hideChestplate = !playerEntry.hideChestplate
                return
            }

        }
        if(
            if (!ClickGUIModule.switchType) isAreaHovered(x + toggleSwitchX+ switchOffset, y + 29f + switchHeight * 3, 42f, 40f)
            else isAreaHovered(x + switchOffset + toggleSwitchX, y + 48f + switchHeight * 3, 68f, 40f)
        ) {
            if (colorAnimations[3].start()) {
                animations[3].start()
                playerEntry.hideLeggings = !playerEntry.hideLeggings
                return
            }

        }
        if(
            if (!ClickGUIModule.switchType) isAreaHovered(x + toggleSwitchX+ switchOffset, y + 29f + switchHeight, 42f, 40f)
            else isAreaHovered(x + switchOffset + toggleSwitchX, y + 48f + switchHeight * 4, 68f, 40f)
        ) {
            if (colorAnimations[4].start()) {
                animations[4].start()
                playerEntry.hideBoots = !playerEntry.hideBoots
                return
            }

        }


    }


    fun draw(y: Float) {
        val playerEntity = EntityOtherPlayerMP(mc.theWorld, playerEntry.profile)
        playerEntity.alwaysRenderNameTag = false
        GuiInventory.drawEntityOnScreen(
            (x + width * 0.15).roundToInt(),
            (y + height * 0.9).roundToInt(),
            120,
            0f,
            0f,
            playerEntity
        )
        roundedRectangle(x, y, width, height, ColorUtil.buttonColor, radius = 5)
        roundedRectangle(x,  y, width * 0.3, height, ColorUtil.elementBackground, ColorUtil.elementBackground, ColorUtil.elementBackground, 0, 5, 0, 5, 0, 0.5)
        roundedRectangle(x + width * 0.3, y, 2, height, ColorUtil.clickGUIColor.brighter(1.3f), radius = 0)


        drawSwitch("Toggle", x + toggleSwitchX, y + 34 + switchHeight * 0.5f, 28f, enabled = playerEntry.toggle, 0)
        drawSwitch("Hide Helmet", x + toggleSwitchX, y + 34 + switchHeight * 1.5f, 28f, enabled = playerEntry.hideHelmet, 1)
        drawSwitch("Hide Chestplate", x + toggleSwitchX, y + 34 + switchHeight * 2.5f, 28f, enabled = playerEntry.hideChestplate, 2)
        drawSwitch("Hide Leggings", x + toggleSwitchX, y + 34 + switchHeight * 3.5f, 28f, enabled = playerEntry.hideLeggings, 3)
        drawSwitch("Hide Boots", x + toggleSwitchX, y + 34 + switchHeight * 4.5f, 28f, enabled = playerEntry.hideBoots, 4)
        var y2 = 10
        for (slider in sliders) {
            slider.y = y + y2
            slider.draw()
            y2 += 72
        }
        nameElement.y = y
        nameElement.draw()



    }
    val scale = 1.8f
    private fun drawSwitch(name: String, x: Float, y: Float, h: Float, enabled: Boolean, index: Int){
        text(name, x - 25, y + h / 2f - 24, textColor, 20f, FontRenderer.REGULAR)
        GlStateManager.pushMatrix()
        scale(scale, scale, 1f)
        val isHovered =
        if (!ClickGUIModule.switchType) isAreaHovered((x + switchOffset) / scale, (y + boxVerticalOffset) / scale, 21f, 20f)
        else isAreaHovered((x + switchOffset) / scale, (y + switchVerticalOffset) / scale, 34f, 20f)


        val color =
            colorAnimations[index].get(
                clickGUIColor.darkerIf(isHovered, 0.7f),
                buttonColor.brighter(1.3f).brighterIf(isHovered, 1.3f),
                enabled
            )

        if (!ClickGUIModule.switchType) {
            //render check box
            dropShadow((x + switchOffset) / scale, (y + boxVerticalOffset) / scale, 21f, 20f, 10f, 0.75f)
            roundedRectangle((x + switchOffset) / scale, (y + boxVerticalOffset) / scale, 21f, 20f, color, 5f)
            rectangleOutline((x + switchOffset) / scale, (y + boxVerticalOffset) / scale, 21f, 20f, clickGUIColor, 5f, 3f)
        } else {
            //render switch
            dropShadow((x + switchOffset) / scale, (y + switchVerticalOffset) / scale, 34f, 20f, 10f, 0.75f)
            roundedRectangle((x + switchOffset) / scale, (y + switchVerticalOffset) / scale, 34f, 20f, buttonColor, 9f)

            if (enabled || animations[index].isAnimating()) roundedRectangle((x + switchOffset) / scale, (y + switchVerticalOffset) / scale, animations[index].get(34f, 9f, enabled), 20f, color, 9f)

            if (isHovered) rectangleOutline((x + switchOffset) / scale, (y + switchVerticalOffset) / scale, 34f, 20f, color.darker(.85f), 9f, 3f)
            circle((x + switchOffset + (42f * scale)) / scale - animations[index].get(33f, 17f, !enabled), (y + (14f * scale)) / scale, 6f,
                Color(220, 220, 220).darkerIf(isHovered, 0.9f)
            )
        }

        scale(1/2f, 1/2f, 1f)
        GlStateManager.popMatrix()
    }

    fun keyTyped(typedChar: Char, keyCode: Int){
        for (slider in sliders) {
            slider.keyTyped(typedChar, keyCode)
        }

    }


    fun mouseReleased(state: Int){
        for (slider in sliders) {
            slider.mouseReleased(state)
        }
    }




}

enum class Option {
    X,
    Y,
    Z
}