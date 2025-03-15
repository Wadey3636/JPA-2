package me.modcore.ui.playerCustomizerGUI

import com.github.wadey3636.jpa.features.render.PlayerEntry
import com.github.wadey3636.jpa.features.render.playerEntries
import com.github.wadey3636.jpa.utils.PlayerDataFetcher.getUUID
import com.mojang.authlib.GameProfile
import gg.essential.universal.UMinecraft
import me.modcore.Core.logger
import me.modcore.ui.Screen
import me.modcore.ui.clickgui.util.ColorUtil
import kotlinx.coroutines.launch
import me.modcore.Core.scope
import me.modcore.features.impl.render.ClickGUIModule
import me.modcore.font.FontRenderer
import me.modcore.ui.clickgui.animations.impl.ColorAnimation
import me.modcore.ui.clickgui.animations.impl.LinearAnimation
import me.modcore.ui.clickgui.util.ColorUtil.brighter
import me.modcore.ui.clickgui.util.ColorUtil.brighterIf
import me.modcore.ui.clickgui.util.ColorUtil.buttonColor
import me.modcore.ui.clickgui.util.ColorUtil.clickGUIColor
import me.modcore.ui.clickgui.util.ColorUtil.darker
import me.modcore.ui.clickgui.util.ColorUtil.darkerIf
import me.modcore.ui.clickgui.util.ColorUtil.textColor
import me.modcore.ui.util.MouseUtils.isAreaHovered
import me.modcore.utils.render.*
import net.minecraft.util.ResourceLocation
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.input.Mouse
import java.util.UUID
import kotlin.math.roundToInt
import kotlin.math.sign
var scrollOffset: Float = 0f

private var steveProfile: GameProfile? = null
private var stevePlayer: EntityOtherPlayerMP? = null
private var steveSkin: ResourceLocation? = null
object PlayerCustomizerGUI : Screen() {

    private val customPlayerInstances: MutableList<CustomPlayerInstance> = mutableListOf()

    override fun draw() {
        var drawY = 30f - scrollOffset
        GlStateManager.pushMatrix()
        scale(1f / scaleFactor, 1f / scaleFactor, 1f)
        roundedRectangle(1200, 60, 180, 90, ColorUtil.buttonColor, radius = 15)
        text("Add", 1290, 105, textColor.darkerIf(isHoveredOverAdd), 36f, align = TextAlign.Middle)
        var y = 30f - scrollOffset
        for (customPlayer in customPlayerInstances) {
            customPlayer.draw(drawY)
            drawY += 510
        }

        scale(scaleFactor, scaleFactor, 1f)
        GlStateManager.popMatrix()
    }

    override fun initGui() {
        if (OpenGlHelper.shadersSupported && mc.renderViewEntity is EntityPlayer && ClickGUIModule.blur) {
            mc.entityRenderer.stopUseShader()
            mc.entityRenderer.loadShader(ResourceLocation("shaders/post/blur.json"))
        }
        customPlayerInstances.clear()
        steveProfile = GameProfile(UUID.randomUUID(), "Steve")
        stevePlayer = EntityOtherPlayerMP(mc.theWorld, steveProfile)
        steveSkin = ResourceLocation("textures/entity/steve.png")
        for (entry in playerEntries) {
            customPlayerInstances.add(CustomPlayerInstance(entry))
            loadAccount(entry.name, entry)
        }


    }
    private fun loadAccount(name: String, entry: PlayerEntry){
        if (entry.profile == null) {
            entry.profile = steveProfile
            return
        }

        var profile: GameProfile? = null
       scope.launch {
           val uuid = getUUID(name) ?: steveProfile?.id
           profile = GameProfile(uuid, name)
           UMinecraft.getMinecraft().sessionService.fillProfileProperties(profile, true)
       }.invokeOnCompletion {
           entry.profile = profile
       }
    }

    override fun onScroll(amount: Int) {
        if (Mouse.getEventDWheel() != 0) {
            val actualAmount = amount.sign * 16
            scrollOffset += actualAmount
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        for (entry in customPlayerInstances) {
            entry.mouseClickedAnywhere(state = mouseButton)
        }
        if (mouseButton != 0) return
        if (isHoveredOverAdd) {
            playerEntries.add(PlayerEntry("Player", 1.0, 1.0, 1.0,
                null, false, false, false, false, true, null))
            val i = playerEntries.size - 1
            customPlayerInstances.add(CustomPlayerInstance(playerEntries[i]))
            loadAccount(playerEntries[i].name, playerEntries[i])
            return
        }
        var y = 30f - scrollOffset
        val x = 60f
        for (entry in customPlayerInstances) {
            if (isAreaHovered(x, y, 1050f, 450f))
                entry.mouseClicked(y)
            y += 510
        }
    }


    override fun onGuiClosed() {
        mc.entityRenderer.stopUseShader()
    }
    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        for (instance in customPlayerInstances) {
            instance.mouseReleased(state)
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        for (instance in customPlayerInstances) {
            instance.keyTyped(typedChar, keyCode)
        }
        super.keyTyped(typedChar, keyCode)
    }


    private val isHoveredOverAdd
        get() = isAreaHovered(1200f, 60f, 180f, 90f)



}