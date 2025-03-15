package com.github.wadey3636.jpa

import com.github.wadey3636.jpa.commands.ModCommand
import com.github.wadey3636.jpa.utils.TitleRenderer
import me.modcore.Core
import me.modcore.Core.mc
import me.modcore.features.ModuleManager
import me.modcore.font.FontRenderer
import me.modcore.ui.clickgui.ClickGUI
import me.modcore.utils.clock.Executor
import me.modcore.utils.render.RenderUtils
import me.modcore.utils.render.RenderUtils2D
import me.modcore.utils.render.Renderer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.Blocks
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import java.io.File


const val MODID = "jpa"

@Mod(modid = MODID, useMetadata = true)
class NoobRoutes {
    companion object {
        @Mod.Instance(MODID)
        lateinit var instance: NoobRoutes
            private set
    }




    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {

        try {
            val resource: net.minecraft.client.resources.IResource = Minecraft.getMinecraft().resourceManager
                .getResource(net.minecraft.util.ResourceLocation("test:test.txt"))
            org.apache.commons.io.IOUtils.copy(resource.inputStream, java.lang.System.out)
        } catch (e: java.io.IOException) {
            throw java.lang.RuntimeException(e)
        }
        ClientCommandHandler.instance.registerCommand(ModCommand())
        println("Dirt: ${Blocks.dirt.unlocalizedName}")
	    // Below is a demonstration of an access-transformed class access.
	    println("Color State: " + GlStateManager.Color());
        val modules = listOf(
            Core,
            ModuleManager,
            Executor,
            Renderer,
            RenderUtils2D,
            RenderUtils,
            ClickGUI,
            TitleRenderer
        )
        modules.forEach {
            MinecraftForge.EVENT_BUS.register(it)
        }
        FontRenderer.init()
    }
    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        Core.postInit()
    }

    @Mod.EventHandler
    fun loadComplete(event: FMLLoadCompleteEvent) {
        File(mc.mcDataDir, "config/jpa").takeIf { !it.exists() }?.mkdirs()
        Core.loadComplete()
        ModuleManager.addModules()

    }

}
