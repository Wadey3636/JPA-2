package com.github.wadey3636.jpa.features.misc


import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import com.github.wadey3636.jpa.utils.WorldUtils
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.util.BlockPos
import com.github.wadey3636.jpa.utils.InventoryInfo
import com.github.wadey3636.jpa.utils.Location
import me.modcore.features.Category
import me.modcore.features.Module
import com.github.wadey3636.jpa.utils.LocationUtils
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import me.modcore.Core.logger
import me.modcore.config.DataManager
import me.modcore.utils.skyblock.devMessage


object InventoryLogger : Module(
    name = "Inventory Logger",
    description = "Logs all chests and gui's opened, allowing you to run /search to find an item in any logged chest. It is also dependent on what skyblock profile you are on",
    category = Category.MISC
) {

    private var chestEntries: MutableList<InventoryInfo> = mutableListOf()
    private var lastClickedChest: List<BlockPos> = listOf()

    init {
        load()
    }

    private fun entryExists(location: Location, pos: List<BlockPos>): Int? {
        var i = 0
        while (i < chestEntries.size) {
            val entry = chestEntries[i]
            if (entry.location == location && entry.pos == pos) return i
            i++
        }
        return null
    }



    @SubscribeEvent
    fun openGui(event: GuiOpenEvent){
        val location = LocationUtils.getLocation() ?: return
        if (lastClickedChest.isNotEmpty() && event.gui is GuiChest) {
            val index = entryExists(location, lastClickedChest)
            val container = (event.gui as GuiChest).inventorySlots
            if (index != null) {
                chestEntries[index] = InventoryInfo(location, lastClickedChest, container)
            }
            else chestEntries.add(InventoryInfo(location, lastClickedChest, container))
        }
        save()
        devMessage(chestEntries.size)
    }
    @SubscribeEvent
    fun findChest(event: PlayerInteractEvent) {
        if (
            event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK
            && WorldUtils.isChest(event.pos)
            ) {
            lastClickedChest = WorldUtils.findDoubleChest(event.pos)
        }
    }
    private fun save() {
        val array = JsonArray()
        val gson =  GsonBuilder().setPrettyPrinting().create()
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


        }
    }


}