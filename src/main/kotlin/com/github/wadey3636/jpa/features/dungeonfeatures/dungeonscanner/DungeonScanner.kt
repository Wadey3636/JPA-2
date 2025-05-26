package com.github.wadey3636.jpa.features.dungeonfeatures.dungeonscanner


import com.github.wadey3636.jpa.events.impl.SecondEvent
import com.github.wadey3636.jpa.utils.WorldUtils.isBlock
import com.github.wadey3636.jpa.utils.dungeon.DungeonUtils.Companion.inDungeons
import com.github.wadey3636.jpa.utils.dungeon.RoomInfo
import com.github.wadey3636.jpa.Core.mc
import com.github.wadey3636.jpa.utils.skyblock.devMessage
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.lang.System.currentTimeMillis

// Each 1x1 unit is 30 by 30
var iceFillPosition: RoomInfo? = null
var scanFinished = false
val uncheckedRooms = HashSet<BlockPos>()


class DungeonScanner {
    private var lastScan = currentTimeMillis()

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        uncheckedRooms.clear()
        iceFillPosition = null
        scanFinished = false

        for (i in 0..6) {
            for (z in 0..6) {
                uncheckedRooms.add(BlockPos((-25 - (32 * i)), 70, (-25 - (32 * z))))
            }
        }
    }


    @SubscribeEvent
    fun dungeonScanner(event: SecondEvent) {
        if (scanFinished || !inDungeons) return
        if (currentTimeMillis() - lastScan < 5000) return
        lastScan = currentTimeMillis()
        val iterator = uncheckedRooms.iterator()
        while (iterator.hasNext()) {
            val pos = iterator.next()
            if (mc.theWorld.getChunkFromBlockCoords(pos).isLoaded) {
                if (isIceFill(pos)) {
                    devMessage("Found Icefill")
                    val doors = findWitherDoors(pos)
                    if (doors.size == 1) {
                        iceFillPosition = RoomInfo("IceFill", intArrayOf(pos.x, pos.z), doors[0])
                    }
                    iterator.remove()
                } else {
                    iterator.remove()
                }
            }
        }
        if (uncheckedRooms.isEmpty()) scanFinished = true
    }


    private fun isIceFill(pos: BlockPos): Boolean {
        return isBlock(BlockPos(pos.x, pos.y, pos.z), Blocks.ice)

    }


    private fun findWitherDoors(pos: BlockPos): List<String> {
        return listOfNotNull(
            "South".takeIf { !isBlock(BlockPos(pos.x, 73, pos.z - 16), Blocks.air) },
            "North".takeIf { !isBlock(BlockPos(pos.x, 73, pos.z + 16), Blocks.air) },
            "East".takeIf { !isBlock(BlockPos(pos.x + 16, 73, pos.z), Blocks.air) },
            "West".takeIf { !isBlock(BlockPos(pos.x - 16, 73, pos.z), Blocks.air) }
        )
    }

}