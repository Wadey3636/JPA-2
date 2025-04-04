package com.github.wadey3636.jpa.features.misc


import com.github.wadey3636.jpa.utils.ChestSize
import com.github.wadey3636.jpa.utils.ChestSize.Double
import com.github.wadey3636.jpa.utils.ChestSize.Single
import com.github.wadey3636.jpa.utils.GuiUtils.display
import com.github.wadey3636.jpa.utils.GuiUtils.getStacks
import com.github.wadey3636.jpa.utils.InventoryInfo
import com.github.wadey3636.jpa.utils.WorldUtils
import com.github.wadey3636.jpa.utils.adapters.ChestEntryTypeAdapter
import com.github.wadey3636.jpa.utils.location.Island
import com.github.wadey3636.jpa.utils.location.LocationUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import me.modcore.Core.logger
import me.modcore.config.DataManager
import me.modcore.features.Category
import me.modcore.features.Module
import me.modcore.utils.skyblock.devMessage
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.util.BlockPos
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


object InventoryLogger : Module(
    name = "Inventory Logger",
    description = "Logs all chests and gui's opened, allowing you to run /search to find an item in any logged chest. It is also dependent on what skyblock profile you are on",
    category = Category.MISC
) {


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

    fun setLocation(){

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