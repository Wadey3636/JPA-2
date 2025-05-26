package com.github.wadey3636.jpa.features.dungeonfeatures


import com.github.wadey3636.jpa.events.impl.QuarterSecondEvent
import com.github.wadey3636.jpa.utils.WorldUtils.isBlock
import com.github.wadey3636.jpa.utils.dungeon.DungeonUtils
import com.github.wadey3636.jpa.features.Category
import com.github.wadey3636.jpa.features.Module
import com.github.wadey3636.jpa.features.settings.impl.BooleanSetting
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.concurrent.atomic.AtomicBoolean


object PositionalMessages : Module(
    name = "Pos Messages",
    description = "Sends Messages in chat when you stand in specific spots in F7",
    category = Category.FLOOR7
) {
    private var berzposactive = AtomicBoolean(false)
    private var simonsaysposactive = AtomicBoolean(false)
    private var ee2active = AtomicBoolean(false)
    private var ee3active = AtomicBoolean(false)
    private var ee4active = AtomicBoolean(false)
    private var goldorposactive = AtomicBoolean(false)
    private var dragonposactive = AtomicBoolean(false)
    private var midposactive = AtomicBoolean(false)
    private var ee2safespotactive = AtomicBoolean(false)
    private var ee3safespotactive = AtomicBoolean(false)
    private var stormposmsgactive = AtomicBoolean(false)

    private val berzmsg by BooleanSetting(
        name = "I4 Await Healer",
        description = "Sends as positional message when you stand on the spot awaiting healer leap",
        forceCheckBox = true
    )
    private val simonsayspos by BooleanSetting(
        name = "Simon Says",
        description = "When you stand at Simon Says",
        forceCheckBox = true
    )
    private val earlyentrypositions by BooleanSetting(
        name = "Early Entry Positions",
        description = "Sends as positional message when you stand on the spot awaiting healer leap",
        forceCheckBox = true
    )

    private val goldorpos by BooleanSetting(
        name = "Core",
        description = "When you are in Core",
        forceCheckBox = true
    )
    private val dragonpos by BooleanSetting(
        name = "P5",
        description = "When you fall into P5. Doesn't work with lavaclip",
        forceCheckBox = true
    )
    private val midposmsg by BooleanSetting(
        name = "Middle",
        description = "When you land at mid",
        forceCheckBox = true
    )
    private val stormposmsg by BooleanSetting(
        name = "Storm",
        description = "When you fall into storm",
        forceCheckBox = true
    )


    private fun sendPosMessage(
        coords: BlockPos,
        smallerpos: IntArray,
        largerpos: IntArray,
        checkBlockCoords: BlockPos,
        blocks: Block,
        msg: String,
        msgVariable: AtomicBoolean
    ) {
        if (
            (isBlock(checkBlockCoords, blocks)) &&
            (coords.x in smallerpos[0]..largerpos[0]) &&
            (coords.y in smallerpos[1]..largerpos[1]) &&
            (coords.z in smallerpos[2]..largerpos[2])
        ) {
            if (!msgVariable.get()) {
                msgVariable.set(true)

                mc.thePlayer?.sendChatMessage(msg)
            }
        } else {

            msgVariable.set(false)
        }


    }

    @SubscribeEvent
    fun positionalMessages(event: QuarterSecondEvent) {
        if (DungeonUtils.inDungeons) {
            val coords = mc.thePlayer.position
            if (berzmsg) sendPosMessage(
                coords,
                intArrayOf(92, 129, 43),
                intArrayOf(93, 134, 46),
                BlockPos(100, 167, 40),
                Blocks.barrier,
                "/pc Ready for Healer Leap!",
                berzposactive
            )
            if (simonsayspos) sendPosMessage(
                coords,
                intArrayOf(106, 119, 89),
                intArrayOf(111, 125, 97),
                BlockPos(100, 167, 40),
                Blocks.barrier,
                "/pc At Simon Says!",
                simonsaysposactive
            )
            if (earlyentrypositions) {
                sendPosMessage(
                    coords,
                    intArrayOf(44, 105, 127),
                    intArrayOf(61, 111, 135),
                    BlockPos(101, 118, 123),
                    Blocks.cobblestone_wall,
                    "/pc At ee2!",
                    ee2active
                )
                sendPosMessage(
                    coords,
                    intArrayOf(68, 108, 120),
                    intArrayOf(70, 111, 122),
                    BlockPos(101, 118, 123),
                    Blocks.cobblestone_wall,
                    "/pc At ee2 Safe Spot!",
                    ee2safespotactive
                )
                sendPosMessage(
                    coords,
                    intArrayOf(-1, 108, 97),
                    intArrayOf(3, 110, 108),
                    BlockPos(17, 118, 132),
                    Blocks.cobblestone_wall,
                    "/pc At ee3!",
                    ee3active
                )
                sendPosMessage(
                    coords,
                    intArrayOf(17, 121, 91),
                    intArrayOf(20, 126, 100),
                    BlockPos(17, 118, 132),
                    Blocks.cobblestone_wall,
                    "/pc At ee3 Safe Spot!",
                    ee3safespotactive
                )
                sendPosMessage(
                    coords,
                    intArrayOf(50, 114, 50),
                    intArrayOf(58, 119, 54),
                    BlockPos(17, 118, 132),
                    Blocks.cobblestone_wall,
                    "/pc At ee4!",
                    ee4active
                )
            }
            if (goldorpos) sendPosMessage(
                coords,
                intArrayOf(49, 113, 55),
                intArrayOf(58, 120, 116),
                BlockPos(54, 118, 54),
                Blocks.gold_block,
                "/pc In Core!",
                goldorposactive
            )
            if (dragonpos) sendPosMessage(
                coords,
                intArrayOf(51, 40, 64),
                intArrayOf(60, 55, 79),
                BlockPos(56, 63, 77),
                Blocks.sea_lantern,
                "/pc In P5!",
                dragonposactive
            )
            if (midposmsg) sendPosMessage(
                coords,
                intArrayOf(46, 64, 62),
                intArrayOf(68, 72, 84),
                BlockPos(56, 63, 77),
                Blocks.sea_lantern,
                "/pc At Mid!",
                midposactive
            )
            if (stormposmsg) sendPosMessage(
                coords,
                intArrayOf(63, 200, 29),
                intArrayOf(83, 210, 52),
                BlockPos(70, 220, 33),
                Blocks.stone_brick_stairs,
                "/pc In Storm!",
                stormposmsgactive
            )

        }
    }
}



