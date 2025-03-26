package com.github.wadey3636.jpa.utils.adapters

import com.github.wadey3636.jpa.features.misc.InventoryLogger.determineSize
import com.github.wadey3636.jpa.utils.InventoryInfo
import com.github.wadey3636.jpa.utils.Slot
import com.github.wadey3636.jpa.utils.location.Island
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import me.modcore.utils.skyblock.skullTexture
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockPos
import net.minecraft.util.ResourceLocation
import java.io.IOException
import java.util.*

//
//data class InventoryInfo(val location: Island, val pos: List<BlockPos>?, val page: Container)

class ChestEntryTypeAdapter : TypeAdapter<InventoryInfo>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, info: InventoryInfo) {
        out.beginObject()
        out.name("location").value(info.location.displayName)
        out.name("pos").beginArray()
        info.pos?.forEach {
            out.beginObject()
            out.name("posX").value(it.x)
            out.name("posY").value(it.y)
            out.name("posZ").value(it.z)
            out.endObject()
        }
        out.endArray()
        out.name("Container").beginArray()

        info.page.forEach { value ->
            out.beginObject()
            out.name("name").value(value.itemStack.displayName)
            out.name("slot").value(value.index)
            val resourceLocation: ResourceLocation? = Item.itemRegistry.getNameForObject(value.itemStack.item)
            val id = resourceLocation?.toString() ?: "minecraft:air"
            out.name("id").value(id)
            if (id == "minecraft:skull") {
                out.name("skullTexture").value(value.itemStack.skullTexture)
            }
            out.name("Count").value(value.itemStack.stackSize.toLong())
            out.endObject()
        }


        out.endArray()
        out.name("Size").value(info.size.int)

        out.endObject()


    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): InventoryInfo {
        var island: Island? = null
        val posList: MutableList<BlockPos> = mutableListOf()
        val slots: MutableList<Slot> = mutableListOf()
        var size: Int = 1
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "location" -> {
                    val displayName = reader.nextString()
                    island = Island.entries.firstOrNull { displayName.contains(it.displayName, true) } ?: Island.Unknown
                }

                "pos" -> {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        var x = 0
                        var y = 0
                        var z = 0
                        reader.beginObject()
                        while (reader.hasNext()) {
                            when (reader.nextName()) {
                                "posX" -> x = reader.nextInt()
                                "posY" -> y = reader.nextInt()
                                "posZ" -> z = reader.nextInt()
                                else -> reader.skipValue()
                            }
                        }
                        reader.endObject()
                        posList.add(BlockPos(x, y, z))
                    }
                    reader.endArray()
                }

                "Container" -> {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        var slotIndex = 0
                        var itemDisplayName = ""
                        var id: String? = null
                        var skullTexture: String? = null
                        var count = 0

                        reader.beginObject()
                        while (reader.hasNext()) {
                            when (reader.nextName()) {
                                "name" -> itemDisplayName = reader.nextString()
                                "slot" -> slotIndex = reader.nextInt()
                                "id" -> id = reader.nextString()
                                "skullTexture" -> skullTexture = reader.nextString()
                                "Count" -> count = reader.nextInt()
                                else -> reader.skipValue()
                            }
                        }
                        reader.endObject()
                        val resourceLocation = ResourceLocation(id)
                        val item = Item.itemRegistry.getObject(resourceLocation) ?: Item.getItemById(0)
                        val itemStack = ItemStack(item, count)
                        itemStack.setStackDisplayName(itemDisplayName)
                        if (id == "minecraft:skull" && skullTexture != null) {
                            itemStack.skullTexture = skullTexture
                        }

                        slots.add(Slot(slotIndex, itemStack))
                    }
                    reader.endArray()
                }
                "Size" -> {
                    size = reader.nextInt()
                }
                else -> {
                    reader.skipValue()
                }

            }
        }
        reader.endObject()
        if (island == null) {
            throw IOException("Missing location in InventoryInfo JSON")
        }
        return InventoryInfo(island, posList, slots, determineSize(size))
    }

}

