package com.github.wadey3636.jpa.utils

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import com.github.wadey3636.jpa.utils.GuiUtils.deformat
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.scoreboard.Score
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.tileentity.TileEntityChest
import net.minecraft.util.BlockPos
import java.util.stream.Collectors
import kotlin.math.pow
import kotlin.math.sqrt
import me.modcore.Core.mc

/**
 * Retrieves the block object at the specified positon
 *
 * @param pos The position to query.
 * @return The block as a `Block`.
 */
object WorldUtils {
    fun getBlockAt(pos: IntArray): Block {
        val x = pos[0]
        val y = pos[1]
        val z = pos[2]
        val newPos = BlockPos(x, y, z)
        return mc.theWorld?.getBlockState(newPos)?.block ?: Blocks.air
    }

    fun getBlockAt(pos: BlockPos): Block {
        return mc.theWorld?.getBlockState(pos)?.block ?: Blocks.air
    }


    /**
     * Checks if a block at a specified position is a specified Block.
     * Returns as a Boolean
     *
     * @param pos: BlockPos
     * @param blocks: Block
     */
    //The Blocks need to be converted into strings otherwise it doesn't work.
    // I assume it has to do with Hypixel Supporting different versions
    fun isBlock(pos: BlockPos, blocks: Block): Boolean {
        return blocks.toString() == (mc.theWorld?.getBlockState(pos)?.block ?: Blocks.air).toString()
    }

    fun isChest(pos: BlockPos): Boolean{
        return mc.theWorld?.getTileEntity(pos) is TileEntityChest
    }
    fun findDoubleChest(pos: BlockPos): List<BlockPos> {
        val possibleChests = arrayOf(
            BlockPos(pos.x - 1, pos.y, pos.z),
            BlockPos(pos.x + 1, pos.y, pos.z),
            BlockPos(pos.x, pos.y, pos.z -1),
            BlockPos(pos.x, pos.y, pos.z + 1)
            )
        for (location in possibleChests) {
            if (isChest(location)) return listOf(pos, location)
        }
        return listOf(pos)
    }
    fun findDistance3D(x: Double, y: Double, z: Double, x1: Double, y1: Double, z1: Double): Double {
        return sqrt((x1 - x).pow(2) + (y1 - y).pow(2) + (z1 - z).pow(2))
    }
    fun findDistance2D(x: Double, z: Double, x1: Double, z1: Double): Double {
        return sqrt((x1-x).pow(2) + (z1-z).pow(2))
    }


    /**
     * Checks if a given position is within the bounds defined by min and max BlockPos.
     */
    fun isWithinBounds(pos: BlockPos, min: BlockPos, max: BlockPos): Boolean {
        return pos.x in min.x..max.x && pos.y in min.y..max.y && pos.z in min.z..max.z
    }

//Thank you to Wyan because I just yoinked this code from him when he sent me how he got the scoreboard

    fun getSidebarLines(): List<String> {
        val lines: MutableList<String> = ArrayList()
        if (mc.theWorld == null) return lines
        val scoreboard = mc.theWorld.scoreboard ?: return lines

        val objective = scoreboard.getObjectiveInDisplaySlot(1) ?: return lines

        var scores = scoreboard.getSortedScores(objective)
        val list = scores.stream()
            .filter { input: Score? -> input != null && input.playerName != null && !input.playerName.startsWith("#") }
            .collect(
                Collectors.toList()
            )

        scores = if (list.size > 15) {
            Lists.newArrayList(Iterables.skip(list, scores.size - 15))
        } else {
            list
        }
        for (score in scores) {
            val team = scoreboard.getPlayersTeam(score.playerName)
            lines.add(ScorePlayerTeam.formatPlayerName(team, score.playerName).deformat)
        }

        return lines
    }
}