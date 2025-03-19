package com.github.wadey3636.jpa.utils.dungeon

import me.modcore.utils.render.Color
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation


data class DungeonPlayer(
    val name: String,
    val clazz: DungeonClass,
    val clazzLvl: Int,
    val locationSkin: ResourceLocation = ResourceLocation("textures/entity/steve.png"),
    var entity: EntityPlayer? = null,
    var isDead: Boolean = false,
    var deaths: Int = 0
)

/**
 * Enumeration representing player classes in a dungeon setting.
 *
 * Each class is associated with a specific code and color used for formatting in the game. The classes include Archer,
 * Mage, Berserk, Healer, and Tank.
 *
 * @property color The color associated with the class.
 * @property defaultQuadrant The default quadrant for the class.
 * @property priority The priority of the class.
 *
 */
enum class DungeonClass(
    val color: Color,
    val colorCode: Char,
    val defaultQuadrant: Int,
    var priority: Int,
) {
    Archer(Color.ORANGE, '6', 0, 2),
    Berserk(Color.DARK_RED, '4', 1, 0),
    Healer(Color.PINK, 'd', 2, 2),
    Mage(Color.BLUE, 'b', 3, 2),
    Tank(Color.DARK_GREEN, '2', 3, 1),
    Unknown(Color.WHITE, 'f', 0, 0)
}

enum class Blessing(
    var regex: Regex,
    val displayString: String,
    var current: Int = 0
) {
    POWER(Regex("Blessing of Power (X{0,3}(IX|IV|V?I{0,3}))"), "Power"),
    LIFE(Regex("Blessing of Life (X{0,3}(IX|IV|V?I{0,3}))"), "Life"),
    WISDOM(Regex("Blessing of Wisdom (X{0,3}(IX|IV|V?I{0,3}))"), "Wisdom"),
    STONE(Regex("Blessing of Stone (X{0,3}(IX|IV|V?I{0,3}))"), "Stone"),
    TIME(Regex("Blessing of Time (V)"), "Time");

    fun reset() {
        current = 0
    }
}

/**
 * Enumeration representing different floors in a dungeon.
 *
 * This enum class defines various floors, including both regular floors (F1 to F7) and master mode floors (M1 to M7).
 * Each floor has an associated floor number and an indicator of whether it is a master mode floor.
 *
 * @property floorNumber The numerical representation of the floor, where E represents the entrance floor.
 * @property isMM Indicates whether the floor is a master mode floor (M1 to M7).
 * @property personalBest The personal best time for the floor.
 * @property secretPercentage The percentage of secrets required.
 */
enum class Floor(val secretPercentage: Float = 1f) {
    E(0.3f),
    F1(0.3f),
    F2(0.4f),
    F3(0.5f),
    F4(0.6f),
    F5(0.7f),
    F6(0.85f),
    F7(10f),
    M1(6f),
    M2(6f),
    M3(8f),
    M4(5f),
    M5(5f),
    M6(7f),
    M7(10f),
    None(0f);


    /**
     * Gets the numerical representation of the floor.
     *
     * @return The floor number. E has a floor number of 0, F1 to F7 have floor numbers from 1 to 7, and M1 to M7 have floor numbers from 1 to 7.
     */
    val floorNumber: Int
        get() {
            return when (this) {
                E -> 0
                F1, M1 -> 1
                F2, M2 -> 2
                F3, M3 -> 3
                F4, M4 -> 4
                F5, M5 -> 5
                F6, M6 -> 6
                F7, M7 -> 7
                None -> -1
            }
        }

    /**
     * Indicates whether the floor is a master mode floor.
     *
     * @return `true` if the floor is a master mode floor (M1 to M7), otherwise `false`.
     */
    val isMM: Boolean
        get() {
            return when (this) {
                E, F1, F2, F3, F4, F5, F6, F7, None -> false
                M1, M2, M3, M4, M5, M6, M7 -> true
            }
        }
}