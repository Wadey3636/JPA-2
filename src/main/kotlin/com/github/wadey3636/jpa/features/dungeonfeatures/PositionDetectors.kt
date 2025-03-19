package com.github.wadey3636.jpa.features.dungeonfeatures


import com.github.wadey3636.jpa.events.QuarterSecondEvent
import com.github.wadey3636.jpa.utils.PlayerPosInfo
import com.github.wadey3636.jpa.utils.RenderHelper.renderTitle
import com.github.wadey3636.jpa.utils.WorldUtils.isBlock
import com.github.wadey3636.jpa.utils.dungeon.DungeonUtils
import me.modcore.features.Category
import me.modcore.features.Module
import me.modcore.features.impl.render.ClickGUIModule.devMode
import me.modcore.features.settings.Setting.Companion.withDependency
import me.modcore.features.settings.impl.BooleanSetting
import me.modcore.features.settings.impl.ColorSetting
import me.modcore.features.settings.impl.NumberSetting
import me.modcore.features.settings.impl.StringSetting
import me.modcore.utils.noControlCodes
import me.modcore.utils.render.Color
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.concurrent.atomic.AtomicBoolean

var ee2Triggered = AtomicBoolean(false)
var ee3Triggered = AtomicBoolean(false)
var ee4Triggered = AtomicBoolean(false)
var midTriggered = AtomicBoolean(false)

var eess2Triggered = AtomicBoolean(false)
var eess3Triggered = AtomicBoolean(false)

/**
 * I made this before I knew about engineerclient
 */

object PositionDetectors : Module(
    name = "Pos Detectors",
    description = "Displays a player's name when they arrive at specified positions",
    category = Category.FLOOR7
) {
    private var textColor = 0xFF0000
    private val detectorColor by ColorSetting(
        name = "Alert Color",
        default = Color.PURPLE,
        description = "The color of the Alert"
    )
    private val includePosition by BooleanSetting(
        name = "Include Position",
        description = "Includes the location of the displayed player"
    )
    private val safespots by BooleanSetting(
        name = "Safe Spots",
        description = "Allows safe spots to trigger the detector"
    )
    private val detectorTextSize by NumberSetting(
        name = "Scale",
        description = "The Size of the Alert",
        min = 1,
        max = 5,
        default = 3
    )


    private val midDetector by BooleanSetting(
        name = "Mid Detector",
        description = "When someone is at mid",
        forceCheckBox = true
    )
    private val midText by StringSetting(
        "Mid Detector Text",
        "is at Mid!",
        description = "Player + Input Text"
    ).withDependency { includePosition && midDetector }

    private val ee2Detector by BooleanSetting(
        name = "ee2 Detector",
        description = "When someone is at ee2",
        forceCheckBox = true
    )
    private val ee2Text by StringSetting(
        "ee2 Text",
        "is at ee2!",
        description = "Player + Input Text"
    ).withDependency { includePosition && ee2Detector }
    private val ee2TextSS by StringSetting(
        "ee2SS Text",
        "is at ee2 Safe Spot!",
        description = "Player + Input Text"
    ).withDependency { ee2Detector && safespots && includePosition }

    private val ee3Detector by BooleanSetting(
        name = "ee3 Detector",
        description = "When someone is at ee3",
        forceCheckBox = true
    )
    private val ee3Text by StringSetting(
        "ee3 Text",
        "is at ee3!",
        description = "Player + Input Text"
    ).withDependency { includePosition && ee3Detector }
    private val ee3TextSS by StringSetting(
        "ee3SS Text",
        "is at ee3 Safe Spot!",
        description = "Player + Input Text"
    ).withDependency { includePosition && ee3Detector && safespots }

    private val ee4Detector by BooleanSetting(
        name = "ee4 Detector",
        description = "When someone is at ee4",
        forceCheckBox = true
    )
    private val ee4Text by StringSetting(
        "ee4 Text",
        "is at ee4!",
        description = "Player + Input Text"
    ).withDependency { includePosition && ee4Detector }


    private var player: String = ""

    @SubscribeEvent
    fun midReset(event: WorldEvent.Load) {
        midTriggered.set(false)
        ee2Triggered.set(false)
        ee3Triggered.set(false)
        ee4Triggered.set(false)
        eess2Triggered.set(false)
        eess3Triggered.set(false)
    }


    /**
     * @param detectconfig: Boolean
     * @param players: ArrayListOf<PlayerInfo>()
     * @param lowCoords: intArrayOf() Use the lowest coordinate numbers
     * @param highCoords: intArrayOf() Use the highest coordinate numbers
     * @param blockPos: BlockPos() Condition Block coordinates
     * @param block: Condition Block type
     * @param detectorActive: AtomicBoolean()
     */

    fun detectPlayers(
        detectconfig: Boolean,
        players: ArrayList<PlayerPosInfo>,
        lowCoords: IntArray,
        highCoords: IntArray,
        blockPos: BlockPos,
        block: Block,
        detectorActive: AtomicBoolean, text: String
    ) {
        if (!detectconfig || !isBlock(BlockPos(blockPos), block)) return
        val detected = players.firstOrNull {
            it.position.xCoord.toInt() in lowCoords[0]..highCoords[0] &&
                    it.position.yCoord.toInt() in lowCoords[1]..highCoords[1] &&
                    it.position.zCoord.toInt() in lowCoords[2]..highCoords[2]
        }
        if (detected == null) {
            detectorActive.set(false); return
        }

        if (!detectorActive.get()) {
            detected.let { player = it.name }
            textColor = detectorColor.rgba
            mc.theWorld.playSound(
                mc.thePlayer.posX,
                mc.thePlayer.posY,
                mc.thePlayer.posZ,
                "minecraft:random.orb",
                1.0f,
                1.0f,
                false
            )
            detectorActive.set(true)
            if (includePosition) {
                renderTitle("${player.noControlCodes} $text", detectorTextSize.toFloat(), textColor, 3000L)
            } else renderTitle(player, detectorTextSize.toFloat(), textColor, 3000L)
        }

    }


    @SubscribeEvent
    fun detector(event: QuarterSecondEvent) {
        if (DungeonUtils.inDungeons) {
            val players = arrayListOf<PlayerPosInfo>()
            if (devMode) {
                mc.theWorld?.playerEntities?.forEach {
                    players.add(PlayerPosInfo(it.displayNameString, it.positionVector))
                }
            } else {
                mc.theWorld?.playerEntities?.forEach {
                    if (it != mc.thePlayer) players.add(PlayerPosInfo(it.displayNameString, it.positionVector))
                }
            }

            detectPlayers(
                midDetector,
                players,
                intArrayOf(46, 64, 62),
                intArrayOf(68, 72, 84),
                BlockPos(53, 63, 110),
                Blocks.air,
                midTriggered,
                midText
            )

            detectPlayers(
                ee2Detector,
                players,
                intArrayOf(44, 105, 127),
                intArrayOf(61, 111, 135),
                BlockPos(101, 118, 123),
                Blocks.cobblestone_wall,
                ee2Triggered,
                ee2Text
            )

            detectPlayers(
                ee3Detector,
                players,
                intArrayOf(-1, 108, 97),
                intArrayOf(3, 110, 108),
                BlockPos(17, 118, 132),
                Blocks.cobblestone_wall,
                ee3Triggered,
                ee3Text
            )
            detectPlayers(
                ee4Detector,
                players,
                intArrayOf(50, 114, 50),
                intArrayOf(58, 119, 54),
                BlockPos(17, 118, 132),
                Blocks.cobblestone_wall,
                ee4Triggered,
                ee4Text
            )





            if (safespots) {

                detectPlayers(
                    ee2Detector,
                    players,
                    intArrayOf(68, 108, 120),
                    intArrayOf(70, 111, 122),
                    BlockPos(101, 118, 123),
                    Blocks.cobblestone_wall,
                    eess2Triggered,
                    ee2TextSS
                )

                detectPlayers(
                    ee3Detector,
                    players,
                    intArrayOf(17, 121, 91),
                    intArrayOf(20, 126, 100),
                    BlockPos(17, 118, 132),
                    Blocks.cobblestone_wall,
                    eess3Triggered,
                    ee3TextSS
                )
            }
        }

    }

}





