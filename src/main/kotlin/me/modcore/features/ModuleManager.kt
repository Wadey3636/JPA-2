package me.modcore.features

import com.github.wadey3636.jpa.features.dungeonfeatures.*
import com.github.wadey3636.jpa.features.dungeonfeatures.icefillsolver.IceFillSolver
import com.github.wadey3636.jpa.features.misc.Blink
import com.github.wadey3636.jpa.features.misc.InventoryLogger
import com.github.wadey3636.jpa.features.misc.PestFarmingKeybind
import com.github.wadey3636.jpa.features.render.PlayerRenderer
import me.modcore.Core.mc
import me.modcore.events.impl.ChatPacketEvent
import me.modcore.events.impl.InputEvent
import me.modcore.events.impl.PacketEvent
import me.modcore.events.impl.ServerTickEvent
import me.modcore.features.impl.render.ClickGUIModule
import me.modcore.features.settings.impl.KeybindSetting
import me.modcore.ui.hud.EditHUDGui
import me.modcore.ui.hud.HudElement
import me.modcore.utils.capitalizeFirst
import me.modcore.utils.profile
import me.modcore.utils.render.getTextWidth
import net.minecraft.network.Packet
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

/**
 * Class that contains all Modules and huds
 * @author Aton, Bonsai
 */
object ModuleManager {
    data class PacketFunction<T : Packet<*>>(
        val type: Class<T>,
        val function: (T) -> Unit,
        val shouldRun: () -> Boolean,
    )

    data class MessageFunction(val filter: Regex, val shouldRun: () -> Boolean, val function: (String) -> Unit)

    data class TickTask(var ticksLeft: Int, val server: Boolean, val function: () -> Unit)

    val packetFunctions = mutableListOf<PacketFunction<Packet<*>>>()
    val messageFunctions = mutableListOf<MessageFunction>()
    val worldLoadFunctions = mutableListOf<() -> Unit>()
    val tickTasks = mutableListOf<TickTask>()
    val huds = arrayListOf<HudElement>()

    val modules: ArrayList<Module> = arrayListOf(
        ClickGUIModule,
        GfsKeybinds,
        //P3StartTimer,
        //PadTimer,
        PositionalMessages,
        PositionDetectors,
        ProfitTracker,
        TerminalWaypoints,
        WishNotification,
        IceFillSolver,
        PestFarmingKeybind,
        Blink,
        InventoryLogger,
        PlayerRenderer
    )

    init {
        for (module in modules) {
            module.keybinding?.let {
                module.register(KeybindSetting("Keybind", it, "Toggles the module"))
            }
        }
    }

    fun addModules(vararg module: Module) {
        for (i in module) {
            modules.add(i)
            i.keybinding?.let { i.register(KeybindSetting("Keybind", it, "Toggles the module")) }
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        tickTaskTick()
    }

    @SubscribeEvent(receiveCanceled = true)
    fun onServerTick(event: ServerTickEvent) {
        tickTaskTick(true)
    }

    private fun tickTaskTick(server: Boolean = false) {
        tickTasks.removeAll {
            if (it.server != server) return@removeAll false
            if (it.ticksLeft <= 0) {
                it.function()
                return@removeAll true
            }
            it.ticksLeft--
            false
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    fun onReceivePacket(event: PacketEvent.Receive) {
        packetFunctions.forEach {
            if (it.type.isInstance(event.packet) && it.shouldRun.invoke()) it.function(event.packet)
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    fun onSendPacket(event: PacketEvent.Send) {
        packetFunctions.forEach {
            if (it.type.isInstance(event.packet) && it.shouldRun.invoke()) it.function(event.packet)
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    fun onChatPacket(event: ChatPacketEvent) {
        messageFunctions.forEach {
            if (event.message matches it.filter && it.shouldRun()) it.function(event.message)
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        worldLoadFunctions
            .forEach { it.invoke() }
    }

    @SubscribeEvent
    fun activateModuleKeyBinds(event: InputEvent.Keyboard) {
        for (module in modules) {
            for (setting in module.settings) {
                if (setting is KeybindSetting && setting.value.key == event.keycode) {
                    setting.value.onPress?.invoke()
                }
            }
        }
    }

    @SubscribeEvent
    fun activateModuleMouseBinds(event: InputEvent.Mouse) {
        for (module in modules) {
            for (setting in module.settings) {
                if (setting is KeybindSetting && setting.value.key + 100 == event.keycode) {
                    setting.value.onPress?.invoke()
                }
            }
        }
    }

    @SubscribeEvent
    fun onRenderOverlay(event: RenderGameOverlayEvent.Post) {
        if ((mc.currentScreen != null) || event.type != RenderGameOverlayEvent.ElementType.ALL || mc.currentScreen == EditHUDGui) return

        profile("Odin Hud") {
            for (i in 0 until huds.size) {
                huds[i].draw(false)
            }
        }
    }

    fun getModuleByName(name: String?): Module? = modules.firstOrNull { it.name.equals(name, true) }

    fun generateFeatureList(): String {
        val sortedCategories = modules.sortedByDescending { getTextWidth(it.name, 18f) }.groupBy { it.category }.entries
            .sortedBy { Category.entries.associateWith { it.ordinal }[it.key] }

        val featureList = StringBuilder()

        for ((category, modulesInCategory) in sortedCategories) {
            val displayName = category.name.lowercase().capitalizeFirst()
            featureList.appendLine("Category: ${if (displayName == "Floor7") "Floor 7" else displayName}")
            for (module in modulesInCategory) {
                featureList.appendLine("- ${module.name}: ${module.description}")
            }
            featureList.appendLine()
        }
        return featureList.toString()
    }
}