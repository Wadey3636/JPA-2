package me.modcore


import com.github.wadey3636.jpa.MODID
import kotlinx.coroutines.*

import me.modcore.config.Config
import me.modcore.font.FontRenderer
import me.modcore.ui.clickgui.ClickGUI
import me.modcore.ui.playerCustomizerGUI.PlayerCustomizerGUI
import me.modcore.ui.util.shader.RoundedRect
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import kotlin.coroutines.EmptyCoroutineContext

object Core {
    val mc: Minecraft = Minecraft.getMinecraft()


    const val VERSION = "@VER@"
    val scope = CoroutineScope(SupervisorJob() + EmptyCoroutineContext)
    val logger: Logger = LogManager.getLogger(MODID)

    var display: GuiScreen? = null

    fun init() {
        FontRenderer.init()
    }

    fun postInit() {
        File(mc.mcDataDir, "config/$MODID").takeIf { !it.exists() }?.mkdirs()
    }

    fun loadComplete() {
        runBlocking(Dispatchers.IO) {
            launch {
                Config.load()
            }.join()
        }
        ClickGUI.init()
        RoundedRect.initShaders()
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        PlayerCustomizerGUI.smoothScrollOffset()
        if (display == null) return
        mc.displayGuiScreen(display)
        display = null

    }
}
