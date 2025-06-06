package com.github.wadey3636.jpa.features

import com.github.wadey3636.jpa.Core
import com.github.wadey3636.jpa.features.render.ClickGUIModule
import com.github.wadey3636.jpa.features.settings.AlwaysActive
import com.github.wadey3636.jpa.features.settings.Setting
import com.github.wadey3636.jpa.features.settings.impl.HudSetting
import com.github.wadey3636.jpa.features.settings.impl.Keybinding
import com.github.wadey3636.jpa.utils.clock.Executable
import com.github.wadey3636.jpa.utils.clock.Executor
import com.github.wadey3636.jpa.utils.clock.Executor.Companion.register
import com.github.wadey3636.jpa.utils.clock.Executor.LimitedExecutor
import com.github.wadey3636.jpa.utils.skyblock.modMessage
import net.minecraft.network.Packet
import net.minecraftforge.common.MinecraftForge
import org.lwjgl.input.Keyboard
import kotlin.reflect.full.hasAnnotation

/**
 * Class that represents a module. And handles all the settings.
 * @author Aton
 */
abstract class Module(
    val name: String,
    key: Int? = Keyboard.KEY_NONE,
    @Transient val category: Category = Category.RENDER,
    @Transient var description: String,
    @Transient val tag: TagType = TagType.NONE,
    toggled: Boolean = false,
) {

    var enabled: Boolean = toggled
        private set

    /**
     * Settings for the module
     */
    val settings: ArrayList<Setting<*>> = ArrayList()

    /**
     * Main keybinding of the module
     */
    val keybinding: Keybinding? = key?.let { Keybinding(it).apply { onPress = ::onKeybind } }

    protected inline val mc get() = Core.mc

    /**
     * Indicates if the module has the annotation [AlwaysActive],
     * which keeps the module registered to the eventbus, even if disabled
     */
    @Transient
    val alwaysActive = this::class.hasAnnotation<AlwaysActive>()

    init {
        if (alwaysActive) {
            MinecraftForge.EVENT_BUS.register(this)
        }
    }

    /**
     * Gets toggled when module is enabled
     */
    open fun onEnable() {
        if (!alwaysActive) {
            MinecraftForge.EVENT_BUS.register(this)
        }
    }

    /**
     * Gets toggled when module is disabled
     */
    open fun onDisable() {
        if (!alwaysActive) {
            MinecraftForge.EVENT_BUS.unregister(this)
        }
    }

    open fun onKeybind() {
        toggle()
        if (ClickGUIModule.enableNotification) {
            modMessage("$name ${if (enabled) "§aenabled" else "§cdisabled"}.")
        }
    }

    fun toggle() {
        enabled = !enabled
        if (enabled) onEnable()
        else onDisable()
    }

    fun <K : Setting<*>> register(setting: K): K {
        settings.add(setting)
        if (setting is HudSetting) {
            setting.value.init(this)
        }
        return setting
    }

    fun register(vararg setting: Setting<*>) {
        for (i in setting) {
            register(i)
        }
    }

    operator fun <K : Setting<*>> K.unaryPlus(): K = register(this)

    fun getSettingByName(name: String?): Setting<*>? {
        for (setting in settings) {
            if (setting.name.equals(name, ignoreCase = true)) {
                return setting
            }
        }
        return null
    }

    /**
     * Helper function to make cleaner code, and more performance, since we don't need multiple registers for packet received events.
     *
     * @param type The packet type to listen for.
     * @param shouldRun Get whether the function should run (Will in most cases be used with the "enabled" value)
     * @param func The function to run when the packet is received.
     */
    fun <T : Packet<*>> onPacket(
        type: Class<T>,
        shouldRun: () -> Boolean = { alwaysActive || enabled },
        func: (T) -> Unit
    ) {
        @Suppress("UNCHECKED_CAST")
        ModuleManager.packetFunctions.add(
            ModuleManager.PacketFunction(type, func, shouldRun) as ModuleManager.PacketFunction<Packet<*>>
        )
    }

    /**
     * Runs the given function when a Chat Packet is sent with a message that matches the given regex filter.
     *
     * @param filter The regex the message should match
     * @param shouldRun Boolean getter to decide if the function should run at any given time, could check if the option is enabled for instance.
     * @param func The function to run if the message matches the given regex and shouldRun returns true.
     *
     * @author Bonsai
     */
    fun onMessage(filter: Regex, shouldRun: () -> Boolean = { alwaysActive || enabled }, func: (String) -> Unit) {
        ModuleManager.messageFunctions.add(ModuleManager.MessageFunction(filter, shouldRun, func))
    }

    fun onWorldLoad(func: () -> Unit) {
        ModuleManager.worldLoadFunctions.add(func)
    }

    fun execute(
        delay: Long,
        repeats: Int,
        profileName: String = "${this.name} Executor",
        shouldRun: () -> Boolean = { this.enabled || this.alwaysActive },
        func: Executable
    ) {
        LimitedExecutor(delay, repeats, profileName, shouldRun, func).register()
    }

    fun execute(
        delay: () -> Long,
        profileName: String = "${this.name} Executor",
        shouldRun: () -> Boolean = { this.enabled || this.alwaysActive },
        func: Executable
    ) {
        Executor(delay, profileName, shouldRun, func).register()
    }

    fun execute(
        delay: Long,
        profileName: String = "${this.name} Executor",
        shouldRun: () -> Boolean = { this.enabled || this.alwaysActive },
        func: Executable
    ) {
        Executor(delay, profileName, shouldRun, func).register()
    }

    enum class TagType {
        NONE, NEW, RISKY, FPSTAX
    }


}