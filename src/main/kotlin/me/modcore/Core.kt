package me.modcore


import com.github.wadey3636.jpa.MODID
import kotlinx.coroutines.*
import me.modcore.config.Config
import me.modcore.font.FontRenderer
import me.modcore.ui.clickgui.ClickGUI
import me.modcore.ui.playerCustomizerGUI.PlayerCustomizerGUI
import me.modcore.ui.searchui.SearchGui
import me.modcore.ui.util.shader.RoundedRect
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.client.event.RenderGameOverlayEvent
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
        PlayerCustomizerGUI.loadEntries()
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        PlayerCustomizerGUI.smoothScrollOffset()
        if (display == null) return
        mc.displayGuiScreen(display)
        display = null

    }

    @SubscribeEvent
    fun renderCanceller(event: RenderGameOverlayEvent.Pre) {
        if (mc.currentScreen !is SearchGui && mc.currentScreen !is PlayerCustomizerGUI && mc.currentScreen !is ClickGUI) return
        if (
            event.type == RenderGameOverlayEvent.ElementType.HOTBAR ||
            event.type == RenderGameOverlayEvent.ElementType.HEALTH ||
            event.type == RenderGameOverlayEvent.ElementType.ARMOR ||
            event.type == RenderGameOverlayEvent.ElementType.FOOD ||
            event.type == RenderGameOverlayEvent.ElementType.EXPERIENCE ||
            event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS ||
            event.type == RenderGameOverlayEvent.ElementType.CHAT ||
            event.type == RenderGameOverlayEvent.ElementType.BOSSHEALTH ||
            event.type == RenderGameOverlayEvent.ElementType.AIR ||
            event.type == RenderGameOverlayEvent.ElementType.DEBUG ||
            event.type == RenderGameOverlayEvent.ElementType.HEALTHMOUNT ||
            event.type == RenderGameOverlayEvent.ElementType.JUMPBAR ||
            event.type == RenderGameOverlayEvent.ElementType.PORTAL ||
            event.type == RenderGameOverlayEvent.ElementType.HELMET ||
            event.type == RenderGameOverlayEvent.ElementType.PLAYER_LIST
        ) {
            event.isCanceled = true
        }
    }
}
