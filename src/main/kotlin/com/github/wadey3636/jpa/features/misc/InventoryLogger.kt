package com.github.wadey3636.jpa.features.misc


import com.github.wadey3636.jpa.utils.ChestSize
import com.github.wadey3636.jpa.utils.ChestSize.Double
import com.github.wadey3636.jpa.utils.ChestSize.Single
import com.github.wadey3636.jpa.utils.GuiUtils.display
import com.github.wadey3636.jpa.utils.GuiUtils.getStacks
import com.github.wadey3636.jpa.utils.InventoryInfo
import com.github.wadey3636.jpa.utils.RenderHelper
import com.github.wadey3636.jpa.utils.WorldUtils
import com.github.wadey3636.jpa.utils.adapters.ChestEntryTypeAdapter
import com.github.wadey3636.jpa.utils.location.Island
import com.github.wadey3636.jpa.utils.location.LocationUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.github.wadey3636.jpa.Core.logger
import com.github.wadey3636.jpa.config.DataManager
import com.github.wadey3636.jpa.features.Category
import com.github.wadey3636.jpa.features.Module
import com.github.wadey3636.jpa.features.settings.impl.ColorSetting
import com.github.wadey3636.jpa.features.settings.impl.NumberSetting
import com.github.wadey3636.jpa.utils.render.Color
import com.github.wadey3636.jpa.utils.skyblock.devMessage
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


object InventoryLogger : Module(
    name = "Inventory Logger",
    description = "Logs all chests and gui's opened, allowing you to run /search to find an item in any logged chest. It is also dependent on what skyblock profile you are on",
    category = Category.MISC
) {

    private val overlayScale by NumberSetting("Overlay Scale", 3f, 1f, 10f, 1f, description = "The scale of the overlay when tracking a chest")
    private val overlayColor by ColorSetting("Color", Color.MAGENTA, description = "")
    private var tracking: Island? = null
    private var blockPosition: List<BlockPos>? = null

    val gson: Gson =
        GsonBuilder().registerTypeAdapter(InventoryInfo::class.java, ChestEntryTypeAdapter()).setPrettyPrinting()
            .create()


    var chestEntries: MutableList<InventoryInfo> = mutableListOf()
    private var lastClickedChest: List<BlockPos> = listOf()

    init {
        load()
    }

    private fun entryExists(location: Island, pos: List<BlockPos>): Int? {
        var i = 0
        while (i < chestEntries.size) {
            val entry = chestEntries[i]
            if (entry.location == location) {
                for (position in pos) {
                    if (entry.pos?.firstOrNull { it == position } != null) return i
                }
            }
            i++
        }
        return null
    }


    @SubscribeEvent
    fun logGui(event: TickEvent.ClientTickEvent) {
        val gui = mc.currentScreen
        if (
            event.phase != TickEvent.Phase.END
            || gui !is GuiChest
            || LocationUtils.currentArea.isArea(Island.Dungeon)
        ) return
        val location = LocationUtils.currentArea
        if (lastClickedChest.isNotEmpty() && (gui.display == "Large Chest" || gui.display == "Chest")) {
            val index = entryExists(location, lastClickedChest)
            if (index != null) {
                chestEntries[index] = InventoryInfo(
                    location,
                    lastClickedChest,
                    gui.getStacks,
                    determineSize(lastClickedChest.size)
                )
            } else {
                chestEntries.add(
                    InventoryInfo(
                        location,
                        lastClickedChest,
                        gui.getStacks,
                        determineSize(lastClickedChest.size)


                    )
                )
            }
            save()
        }
    }

    fun determineSize(size: Int): ChestSize {
        return if (size == 1) Single else Double
    }

    @SubscribeEvent
    fun worldLoadEvent(event: WorldEvent.Load) {
        lastClickedChest = listOf()
    }



    @SubscribeEvent
    fun findChest(event: PlayerInteractEvent) {
        if (
            event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK
            && WorldUtils.isChest(event.pos)
        ) {
            devMessage("Clicked Chest")
            lastClickedChest = WorldUtils.findDoubleChest(event.pos)
        } else {
            lastClickedChest = listOf()
        }
    }

    fun setTracking(blockPos: List<BlockPos>?, location: Island){
        tracking = location
        blockPosition = blockPos


    }

    @SubscribeEvent
    fun renderOverlay(event: RenderGameOverlayEvent.Post){
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || tracking != null) return
        tracking?.let { RenderHelper.drawCenteredText(it.displayName, overlayScale, overlayColor.rgba, mc.displayWidth / 2f, mc.displayHeight / 2f) }
    }

    @SubscribeEvent
    fun renderBlockAndTracer(event: RenderWorldLastEvent) {
        val trackedLocation = tracking
        val pos = blockPosition
        if (
            pos.isNullOrEmpty() || trackedLocation == null || !LocationUtils.currentArea.isArea(trackedLocation)
            ) return

        val viewerPos = RenderHelper.getViewerPos(event.partialTicks)
        RenderHelper.drawBox(
            pos[0],
            overlayColor,
            3f,
            true,
            viewerPos
        )
        RenderHelper.trace(pos[0], viewerPos, overlayColor, 3f, true)
    }



    private fun save() {
        val array = JsonArray()
        try {
            for (entry in chestEntries) {
                array.add(gson.toJsonTree(entry))
            }
            DataManager.saveDataToFile("ChestEntries", array)
        } catch (e: Exception) {
            logger.error("Error Saving Chests", e)
        }

    }

    private fun load() {
        chestEntries.clear()
        DataManager.loadDataFromFile("ChestEntries").forEach { jsonObject ->
            try {
                chestEntries.add(gson.fromJson(jsonObject, InventoryInfo::class.java))
            } catch (e: Exception) {
                devMessage("Error Loading Chests")
                logger.error("Error Loading Chests", e)
            }

        }
    }


}