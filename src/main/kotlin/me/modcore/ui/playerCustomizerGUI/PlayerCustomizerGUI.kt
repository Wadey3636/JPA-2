package me.modcore.ui.playerCustomizerGUI

import PlayerEntryTypeAdapter
import com.github.wadey3636.jpa.features.render.PlayerEntry
import com.github.wadey3636.jpa.features.render.playerEntries
import com.github.wadey3636.jpa.utils.PlayerDataFetcher.getUUID
import com.google.common.collect.Multimap
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.mojang.authlib.GameProfile
import gg.essential.universal.UMinecraft
import kotlinx.coroutines.launch
import me.modcore.Core.logger
import me.modcore.Core.scope
import me.modcore.config.DataManager
import me.modcore.features.impl.render.ClickGUIModule
import me.modcore.ui.Screen
import me.modcore.ui.clickgui.util.ColorUtil
import me.modcore.ui.clickgui.util.ColorUtil.darkerIf
import me.modcore.ui.clickgui.util.ColorUtil.textColor
import me.modcore.ui.util.MouseUtils.isAreaHovered
import me.modcore.utils.render.*
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.world.GameRules.ValueType
import org.lwjgl.input.Mouse
import sun.security.rsa.RSAUtil.KeyType
import java.util.*
import kotlin.math.sign


var scrollOffset: Float = 0f

private var steveProfile: GameProfile? = null
private var stevePlayer: EntityOtherPlayerMP? = null
private var steveSkin: ResourceLocation? = null
object PlayerCustomizerGUI : Screen() {
    val gson = GsonBuilder()
        .registerTypeAdapter(PlayerEntry::class.java, PlayerEntryTypeAdapter())
        .setPrettyPrinting()
        .create()
    val customPlayerInstances: MutableList<CustomPlayerInstance> = mutableListOf()

    fun smoothScrollOffset() {
        if (customPlayerInstances.isEmpty()) {
            scrollOffset = 0f
            return
        }
        val height = 510 * customPlayerInstances.size
        if (height <= 972f) {
            scrollOffset = 0f
            return
        }
        val currentTop = 30f - scrollOffset
        val desiredBottom = 1002f - height  // so that bottom is at least 1002
        val targetTop = currentTop.coerceIn(desiredBottom, 30f)
        val targetScrollOffset = 30f - targetTop
        val smoothingFactor = 0.05f
        scrollOffset += (targetScrollOffset - scrollOffset) * smoothingFactor
    }


    override fun draw() {
        var drawY = 30f - scrollOffset
        GlStateManager.pushMatrix()
        scale(1f / scaleFactor, 1f / scaleFactor, 1f)
        roundedRectangle(1200, 30, 180, 90, ColorUtil.buttonColor, radius = 15)
        text("Add", 1290, 75, textColor.darkerIf(isHoveredOverAdd), 36f, align = TextAlign.Middle)

        roundedRectangle(1200, 140, 180, 90, ColorUtil.buttonColor, radius = 15)
        text("Open Textures", 1290, 185, textColor.darkerIf(isHoveredOverOpenTextures), 16f, align = TextAlign.Middle)


        var y = 30f - scrollOffset
        for (customPlayer in customPlayerInstances.toList()) {
            customPlayer.draw(drawY)
            drawY += 510
        }

        scale(scaleFactor, scaleFactor, 1f)
        GlStateManager.popMatrix()
    }

    override fun initGui() {

        loadEntries()
        if (OpenGlHelper.shadersSupported && mc.renderViewEntity is EntityPlayer && ClickGUIModule.blur) {
            mc.entityRenderer.stopUseShader()
            mc.entityRenderer.loadShader(ResourceLocation("shaders/post/blur.json"))
        }
        customPlayerInstances.clear()
        steveProfile = GameProfile(UUID.randomUUID(), "Steve")
        stevePlayer = EntityOtherPlayerMP(mc.theWorld, steveProfile)
        steveSkin = ResourceLocation("textures/entity/steve.png")
        for (entry in playerEntries.toList()) {
            customPlayerInstances.add(CustomPlayerInstance(entry))
            loadAccount(entry.name, entry)
        }


    }

    private fun loadEntries() {
        playerEntries.clear()
        val jsonArray = DataManager.loadDataFromFile("PlayerEntries")
        jsonArray.forEach {
            val playerEntry = gson.fromJson(it, PlayerEntry::class.java)
            playerEntries.add(playerEntry)
        }
    }




    private fun loadAccount(name: String, entry: PlayerEntry) {

        if (entry.profile == null) {
            entry.profile = steveProfile
        }



        scope.launch {
            val uuid = getUUID(name) ?: steveProfile?.id
            val profile = GameProfile(uuid, name)
            UMinecraft.getMinecraft().sessionService.fillProfileProperties(profile, true)
            entry.profile = profile
            refreshUI()
        }
    }
    fun refreshUI() {
        customPlayerInstances.clear()
        for (entry in playerEntries.toList()) {
            customPlayerInstances.add(0, CustomPlayerInstance(entry))
        }
    }


    override fun onScroll(amount: Int) {


        if (Mouse.getEventDWheel() != 0) {
            var y = 30f - scrollOffset
            val x = 60f
            for (entry in customPlayerInstances.toList()) {
                if (isAreaHovered(x, y, 1050f * 0.3f, 450f)) {
                    entry.mouseScrolled(amount)
                    return
                }

                y += 510
            }


            val actualAmount = amount.sign * 16
            scrollOffset += actualAmount
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        for (entry in customPlayerInstances.toList()) {
            entry.mouseClickedAnywhere(state = mouseButton)
        }
        if (mouseButton != 0) return
        if (isHoveredOverAdd) {
            playerEntries.add(PlayerEntry("Player", 1.0, 1.0, 1.0,
                false, null, false, false, false, false, false, true, null))
            val i = playerEntries.size - 1
            customPlayerInstances.add(CustomPlayerInstance(playerEntries[i]))
            loadAccount(playerEntries[i].name, playerEntries[i])
            return
        }
        if (isHoveredOverOpenTextures) {
            DataManager.openFolder("textures")
        }

        var y = 30f - scrollOffset
        val x = 60f
        for (entry in customPlayerInstances.toList()) {
            if (isAreaHovered(x, y, 1050f, 450f))
                entry.mouseClicked(y)
            y += 510
        }
    }


    override fun onGuiClosed() {
        mc.entityRenderer.stopUseShader()
        val array = JsonArray().apply {
            playerEntries.forEach { add(gson.toJsonTree(it)) }
        }
        DataManager.saveDataToFile("PlayerEntries", array)
    }
    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        for (instance in customPlayerInstances.toList()) {
            instance.mouseReleased(state)
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        for (instance in customPlayerInstances.toList()) {
            instance.keyTyped(typedChar, keyCode)
        }
        super.keyTyped(typedChar, keyCode)
    }


    private val isHoveredOverAdd
        get() = isAreaHovered(1200f, 30f, 180f, 90f)
    private val isHoveredOverOpenTextures
        get() = isAreaHovered(1200f, 140f, 180f, 90f)



}