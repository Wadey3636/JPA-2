package com.github.wadey3636.jpa.features.dungeonfeatures


import me.modcore.events.impl.P3StartEvent
import me.modcore.events.impl.QuarterSecondEvent
import com.github.wadey3636.jpa.utils.GuiUtils.containsOneOf
import com.github.wadey3636.jpa.utils.GuiUtils.deformat
import com.github.wadey3636.jpa.utils.RenderHelper
import com.github.wadey3636.jpa.utils.RenderHelper.getViewerPos
import com.github.wadey3636.jpa.utils.WorldUtils
import me.modcore.events.impl.ChatPacketEvent
import me.modcore.features.Category
import me.modcore.features.Module
import me.modcore.features.settings.Setting.Companion.withDependency
import me.modcore.features.settings.impl.BooleanSetting
import me.modcore.features.settings.impl.ColorSetting
import me.modcore.features.settings.impl.SelectorSetting
import me.modcore.features.settings.impl.StringSetting
import me.modcore.utils.render.Color
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


object TerminalWaypoints : Module(
    name = "Terminal Waypoints",
    category = Category.FLOOR7,
    description = "Highlights selected terminals"
) {


    private var activeWaypoints: MutableList<BlockPos> = mutableListOf()
    private val terminalWaypointsTracer by BooleanSetting(
        name = "Tracer",
        description = "Draws a line from your cursor to the next terminal waypoint"
    )
    private val terminalWaypointsPhase by BooleanSetting(
        name = "Phase",
        description = "Draws waypoints through walls",
        default = true
    )
    private val terminalWaypointsColor by ColorSetting(
        "Color",
        description = "The color of the Terminal Waypoints",
        default = Color.BLUE
    )
    private var terminalSection: Int = 0
    private val terminalPreset by SelectorSetting(
        name = "Terminal Presets",
        description = "Presets for the terminal waypoints",
        defaultSelected = "Archer",
        options = arrayListOf("Archer", "Berserker", "Tank", "Mage", "Custom")
    )
    private val i4 by BooleanSetting(
        name = "I4",
        description = "Enable if Berserker is doing I4",
        forceCheckBox = true
    ).withDependency { terminalPreset != 4 }
    private val ee2 by BooleanSetting(
        name = "EE2",
        description = "Enable if Archer is doing I4",
        forceCheckBox = true
    ).withDependency { terminalPreset == 0 || terminalPreset == 2 }
    private val mageCoring by BooleanSetting(
        name = "Core",
        description = "Enable if Mage is coring",
        forceCheckBox = true
    ).withDependency { terminalPreset == 0 || terminalPreset == 2 }

    private val terminalWaypointsTextS1 by StringSetting(
        name = "S1",
        description = "Enter the terminals separated by slashes in the order you plan to complete them. LL and RL stand for the left and right levers. DEV stands for device. Example: 5/3/RL"

    ).withDependency { terminalPreset == 4 }
    private val terminalWaypointsTextS2 by StringSetting(
        name = "S2",
        description = "Enter the terminals separated by slashes in the order you plan to complete them. LL and RL stand for the left and right levers. DEV stands for device. Example: 5/3/RL"
    ).withDependency { terminalPreset == 4 }
    private val terminalWaypointsTextS3 by StringSetting(
        name = "S3",
        description = "Enter the terminals separated by slashes in the order you plan to complete them. LL and RL stand for the left and right levers. DEV stands for device. Example: 5/3/RL"
    ).withDependency { terminalPreset == 4 }
    private val terminalWaypointsTextS4 by StringSetting(
        name = "S4",
        description = "Enter the terminals separated by slashes in the order you plan to complete them. LL and RL stand for the left and right levers. DEV stands for device. Example: 5/3/RL"
    ).withDependency { terminalPreset == 4 }


    private fun addS1() {
        when (terminalPreset) {
            0 -> if (i4 && ee2) "4/3" else if (i4) "3" else "2"
            1 -> if (i4) "2/1" else "1"
            2 -> if (ee2) "LL/RL" else "4/RL/LL"
            3 -> if (i4) "" else "3"
            4 -> terminalWaypointsTextS1
            else -> ""
        }.split("/").forEach {
            when (it.lowercase()) {
                "1" -> activeWaypoints.add(BlockPos(111, 113, 73))
                "2" -> activeWaypoints.add(BlockPos(111, 119, 79))
                "3" -> activeWaypoints.add(BlockPos(89, 112, 92))
                "4" -> activeWaypoints.add(BlockPos(89, 122, 101))
                "ll" -> activeWaypoints.add(BlockPos(106, 124, 113))
                "rl" -> activeWaypoints.add(BlockPos(94, 124, 113))
                else -> return@forEach
            }
        }
    }

    private fun addS2() {
        when (terminalPreset) {
            0 -> if (mageCoring) "2" else "2/3"
            1 -> "1/3"
            2 -> "4/RL"
            3 -> "5/3"
            4 -> terminalWaypointsTextS2
            else -> ""
        }.split("/").forEach {
            when (it.lowercase()) {
                "1" -> activeWaypoints.add(BlockPos(68, 109, 121))
                "2" -> activeWaypoints.add(BlockPos(59, 120, 122))
                "3" -> activeWaypoints.add(BlockPos(39, 108, 143))
                "4" -> activeWaypoints.add(BlockPos(40, 124, 125))
                "5" -> activeWaypoints.add(BlockPos(23, 132, 138))
                "ll" -> activeWaypoints.add(BlockPos(23, 132, 138))
                "rl" -> activeWaypoints.add(BlockPos(27, 124, 127))
                else -> return@forEach
            }
        }

    }

    private fun addS3() {

        when (terminalPreset) {
            0 -> if (mageCoring) "" else "2"
            1 -> "1"
            2 -> "4/LL/RL"
            3 -> "3"
            4 -> terminalWaypointsTextS3
            else -> ""
        }.split("/").forEach {
            when (it.lowercase()) {
                "1" -> activeWaypoints.add(BlockPos(-3, 109, 112))
                "2" -> activeWaypoints.add(BlockPos(-3, 119, 93))
                "3" -> activeWaypoints.add(BlockPos(19, 123, 93))
                "4" -> activeWaypoints.add(BlockPos(-3, 109, 77))
                "ll" -> activeWaypoints.add(BlockPos(2, 122, 55))
                "rl" -> activeWaypoints.add(BlockPos(14, 122, 55))
                else -> return@forEach
            }
        }

    }

    private fun addS4() {
        when (terminalPreset) {
            0 -> if (mageCoring) "" else "2"
            1 -> "1"
            2 -> if (mageCoring) "2" else "4"
            3 -> "3"
            4 -> terminalWaypointsTextS4
            else -> ""
        }.split("/").forEach {
            when (it.lowercase()) {
                "1" -> activeWaypoints.add(BlockPos(41, 109, 29))
                "2" -> activeWaypoints.add(BlockPos(44, 121, 29))
                "3" -> activeWaypoints.add(BlockPos(67, 109, 29))
                "4" -> activeWaypoints.add(BlockPos(72, 115, 48))
                "ll" -> activeWaypoints.add(BlockPos(84, 121, 34))
                "rl" -> activeWaypoints.add(BlockPos(86, 128, 46))
                else -> return@forEach
            }
        }

    }

    @SubscribeEvent
    fun onQuarterSec(event: QuarterSecondEvent) {
        activeWaypoints.removeIf { waypoint ->
            val armorStands = mc.theWorld.getEntitiesWithinAABB(
                EntityArmorStand::class.java,
                AxisAlignedBB(
                    waypoint.x - 3.0, waypoint.y - 3.0, waypoint.z - 3.0,
                    waypoint.x + 3.0, waypoint.y + 3.0, waypoint.z + 3.0
                )
            )
            armorStands.isNotEmpty() && armorStands.any {
                it.name.deformat.containsOneOf(
                    "active",
                    "terminal active",
                    "activated",
                    "device active",
                    ignoreCase = true
                )
            }
        }
    }

    @SubscribeEvent
    fun worldLoad(event: WorldEvent.Load) {
        activeWaypoints.clear()
    }

    @SubscribeEvent
    fun chatReceived(event: ChatPacketEvent) {
        if (event.message.containsOneOf("! (7/7)", "! (8/8)")) {
            terminalSection += 1
            when (terminalSection) {
                1 -> addS2()
                2 -> addS3()
                3 -> addS4()
            }
        }

    }

    @SubscribeEvent
    fun onP3Start(event: P3StartEvent) {
        terminalSection = 0
        addS1()
    }


    private fun isDev(pos: BlockPos): Boolean {
        return true
    }

    override fun onDisable() {
        super.onDisable()
        activeWaypoints.clear()
    }


    @SubscribeEvent
    fun onRenderLast(event: RenderWorldLastEvent) {
        if (activeWaypoints.isEmpty()) return
        val viewerPos = getViewerPos(event.partialTicks)
        val firstWaypoint = activeWaypoints.firstOrNull()
        var devColor = terminalWaypointsColor
        var renderWaypoints = activeWaypoints
        if (firstWaypoint != null && isDev(firstWaypoint)) {
            val distance = WorldUtils.findDistance3D(
                viewerPos.first, viewerPos.second, viewerPos.third,
                firstWaypoint.x.toDouble(), firstWaypoint.y.toDouble(), firstWaypoint.z.toDouble()
            )
            val clampedAlpha = (distance.coerceIn(2.0, 10.0) / 10).let { if (it == 0.2) 0.0 else it }
            devColor = Color(
                terminalWaypointsColor.r,
                terminalWaypointsColor.g,
                terminalWaypointsColor.b,
                clampedAlpha.toFloat()
            )
            renderWaypoints = activeWaypoints.drop(1).toMutableList()
        }
        renderWaypoints.forEach {
            RenderHelper.drawBox(it, terminalWaypointsColor, 3f, !terminalWaypointsPhase, viewerPos)
        }
        firstWaypoint?.let {
            if (terminalWaypointsTracer) {
                RenderHelper.trace(it, viewerPos, devColor, 3f, true)
            }
        }
    }

}

