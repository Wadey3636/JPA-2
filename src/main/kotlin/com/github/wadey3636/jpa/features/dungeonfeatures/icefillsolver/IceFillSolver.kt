package com.github.wadey3636.jpa.features.dungeonfeatures.icefillsolver


import com.github.wadey3636.jpa.events.QuarterSecondEvent
import com.github.wadey3636.jpa.features.dungeonfeatures.dungeonscanner.iceFillPosition
import com.github.wadey3636.jpa.utils.RenderHelper.drawBox
import com.github.wadey3636.jpa.utils.RenderHelper.drawLines3dAboveBlocks
import com.github.wadey3636.jpa.utils.RenderHelper.getViewerPos
import com.github.wadey3636.jpa.utils.WorldUtils.isBlock
import com.github.wadey3636.jpa.utils.dungeon.*
import me.modcore.features.Category
import me.modcore.features.Module
import me.modcore.features.settings.impl.BooleanSetting
import me.modcore.features.settings.impl.ColorSetting
import me.modcore.utils.render.Color
import me.modcore.utils.skyblock.devMessage
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object IceFillSolver : Module(
    name = "Ice Fill Solver",
    description = "A sweaty af icefill solver. If you are high ping use the automatic zpew toggle.",
    category = Category.DUNGEONS
) {
    private val icefillSolverPhase by BooleanSetting(
        name = "Phase",
        description = "Render the solutions through blocks"
    )

    private val icefillPathColor by ColorSetting(
        name = "Path Color",
        description = "The color of the solution path",
        default = Color(0, 255, 0)
    )
    private val icefillEtherwarpPointColor by ColorSetting(
        name = "Etherwarp Color",
        description = "The color of the solution's etherwarps",
        default = Color(0, 0, 255)
    )
    private val icefillTeleportPointColor by ColorSetting(
        name = "Teleports Color",
        description = "The color of the solution's teleports. (Like basic teleport, not etherwarp)",
        default = Color(255, 0, 0)
    )


    private lateinit var layer0: DeterminedVariant
    private lateinit var layer1: DeterminedVariant
    private lateinit var layer2: DeterminedVariant
    private var layer0plot: List<BlockPos>? = null
    private var layer1plot: List<BlockPos>? = null
    private var layer2plot: List<BlockPos>? = null
    private var layer0warp: List<BlockPos>? = null
    private var layer1warp: List<BlockPos>? = null
    private var layer2warp: List<BlockPos>? = null
    private var layer1tp: BlockPos? = null
    private var layer2tp: BlockPos? = null

    private var inIcefill = false
    private var determinedVariants = false

    private fun determineVariant(variants: List<VariantInfo>, room: RoomInfo): DeterminedVariant {
        for (points in variants) {
            if (points.detectionPoints.count {
                    isBlock(
                        convertToRealCoords(room, it),
                        Blocks.stone
                    )
                } == points.detectionPoints.size) {
                devMessage(points.name)
                return DeterminedVariant(points.name, points.plotPoints, points.warpPoints, points.tpPoint)
            }
        }

        devMessage("§c[Jpa] Error: Variant Undetermined")
        return DeterminedVariant("none", emptyList(), emptyList(), null)
    }


    private fun drawVariant(plot: List<BlockPos>?, warp: List<BlockPos>?, tpPoint: BlockPos?, partialTicks: Float) {
        if (plot == null || warp == null) return
        val viewerPos = getViewerPos(partialTicks)
        drawLines3dAboveBlocks(plot, icefillPathColor, 3f, icefillSolverPhase, viewerPos)
        warp.forEachIndexed { i, point ->
            if ((i <= 2 || isBlock(warp[i - 3], Blocks.packed_ice)) && !isBlock(point, Blocks.packed_ice)) {
                drawBox(point, icefillEtherwarpPointColor, 3f, icefillSolverPhase, viewerPos)
            }
        }
        tpPoint?.let {
            drawBox(it, icefillTeleportPointColor, 3f, icefillSolverPhase, viewerPos)
        }
    }


    private fun bulkConvertToRealCoords(list: List<BlockPos>, room: RoomInfo): List<BlockPos> {
        return list.map { convertToRealCoords(room, it) }
    }


    @SubscribeEvent
    fun reset(event: WorldEvent.Load) {
        determinedVariants = false
        inIcefill = false

    }

    @SubscribeEvent
    fun onQuarterSecond(event: QuarterSecondEvent) {
        iceFillPosition?.let { position ->
            if (!playerInRoomBounds(position, mc.thePlayer.position)) {
                inIcefill = false
                return
            }
            inIcefill = true
            if (determinedVariants) return
            layer0 = determineVariant(listOf(spongecokeVariant, epicVariant, crazyVariant, bfvarroeVariant), position)
            layer1 = determineVariant(
                listOf(
                    akuVariant,
                    jpVariant,
                    desticlesVariant,
                    krzVariant,
                    fanficVariant,
                    hiitsmeVariant
                ), position
            )
            layer2 = determineVariant(
                listOf(crossVariant, turtleVariant, americaVariant, pistolVariant, neutralVariant),
                position
            )
            layer0plot = bulkConvertToRealCoords(layer0.plotPoints, position)
            layer1plot = bulkConvertToRealCoords(layer1.plotPoints, position)
            layer2plot = bulkConvertToRealCoords(layer2.plotPoints, position)
            layer0warp = bulkConvertToRealCoords(layer0.warpPoints, position)
            layer1warp = bulkConvertToRealCoords(layer1.warpPoints, position)
            layer2warp = bulkConvertToRealCoords(layer2.warpPoints, position)
            layer1tp = layer1.tpPoint?.let { convertToRealCoords(position, it) }
            layer2tp = layer2.tpPoint?.let { convertToRealCoords(position, it) }
            determinedVariants = true
        }
    }


    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (inIcefill && determinedVariants) {
            drawVariant(layer0plot, layer0warp, null, event.partialTicks)
            drawVariant(layer1plot, layer1warp, layer1tp, event.partialTicks)
            drawVariant(layer2plot, layer2warp, layer2tp, event.partialTicks)
        }
    }
}
