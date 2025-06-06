package com.github.wadey3636.jpa.utils.skyblock

import com.github.wadey3636.jpa.Core.mc
import com.github.wadey3636.jpa.utils.equalsOneOf
import com.github.wadey3636.jpa.utils.noControlCodes
import com.github.wadey3636.jpa.utils.render.Color
import com.github.wadey3636.jpa.utils.render.RenderUtils.bind
import com.github.wadey3636.jpa.utils.render.scale
import com.github.wadey3636.jpa.utils.render.translate
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.Entity
import net.minecraft.inventory.ContainerChest
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraftforge.common.util.Constants

/**
 * Returns the ExtraAttribute Compound
 */
val ItemStack?.extraAttributes: NBTTagCompound?
    get() = this?.getSubCompound("ExtraAttributes", false)

fun ItemStack.displayName(): String =
    this.tagCompound?.getCompoundTag("display")?.takeIf { it.hasKey("Name", 8) }?.getString("Name")
        ?: this.item.getItemStackDisplayName(this)

/**
 * Returns displayName without control codes.
 */
val ItemStack?.unformattedName: String
    get() = this?.displayName()?.noControlCodes ?: ""

/**
 * Returns the lore for an Item
 */
val ItemStack?.lore: List<String>
    get() = this?.tagCompound?.getCompoundTag("display")?.getTagList("Lore", 8)?.let {
        List(it.tagCount()) { i -> it.getStringTagAt(i) }
    }.orEmpty()

/**
 * Returns Item ID for an Item
 */
val ItemStack?.skyblockID: String
    get() = this?.extraAttributes?.getString("id") ?: ""

/**
 * Returns uuid for an Item
 */
val ItemStack?.uuid: String
    get() = this?.extraAttributes?.getString("uuid") ?: ""

/**
 * Returns if an item has an ability
 */
val ItemStack?.hasAbility: Boolean
    get() = this?.lore?.any { it.contains("Ability:") && it.endsWith("RIGHT CLICK") } == true

/**
 * Returns if an item is a shortbow
 */
val ItemStack?.isShortbow: Boolean
    get() = this?.lore?.any { it.contains("Shortbow: Instantly shoots!") } == true

/**
 * Returns if an item is a fishing rod
 */
val ItemStack?.isFishingRod: Boolean
    get() = this?.lore?.any { it.contains("FISHING ROD") } == true

/**
 * Returns if an item is Spirit leaps or an Infinileap
 */
val ItemStack?.isLeap: Boolean
    get() = this?.skyblockID?.equalsOneOf("INFINITE_SPIRIT_LEAP", "SPIRIT_LEAP") == true

val EntityPlayerSP.usingEtherWarp: Boolean
    get() {
        val item = heldItem ?: return false
        if (item.skyblockID == "ETHERWARP_CONDUIT") return true
        return isSneaking && item.extraAttributes?.getBoolean("ethermerge") == true
    }

/**
 * Returns the ID of held item
 */
fun isHolding(vararg id: String): Boolean =
    mc.thePlayer?.heldItem?.skyblockID in id

/**
 * Returns first slot of an Item
 */
fun getItemSlot(item: String, ignoreCase: Boolean = true): Int? =
    mc.thePlayer?.inventory?.mainInventory?.indexOfFirst { it?.unformattedName?.contains(item, ignoreCase) == true }
        .takeIf { it != -1 }

/**
 * Gets index of an item in a chest.
 * @return null if not found.
 */
fun getItemIndexInContainerChest(
    container: ContainerChest,
    item: String,
    subList: IntRange = 0..container.inventory.size - 36
): Int? {
    return container.inventorySlots.subList(subList.first, subList.last + 1).firstOrNull {
        it.stack?.unformattedName?.noControlCodes?.lowercase() == item.noControlCodes.lowercase()
    }?.slotIndex
}

fun getItemIndexInContainerChest(
    container: ContainerChest,
    item: String,
    subList: IntRange = 0..container.inventory.size - 36,
    ignoreCase: Boolean = false
): Int? {
    return container.inventorySlots.subList(subList.first, subList.last + 1).firstOrNull {
        it.stack?.unformattedName?.contains(item, ignoreCase) == true
    }?.slotIndex
}

/**
 * Gets index of an item in a chest using its uuid.
 * @return null if not found.
 */
fun getItemIndexInContainerChestByUUID(
    container: ContainerChest,
    uuid: String,
    subList: IntRange = 0..container.inventory.size - 36,
    ignoreCase: Boolean = false
): Int? {
    return container.inventorySlots.subList(subList.first, subList.last + 1).firstOrNull {
        it.stack?.uuid?.contains(uuid) == true
    }?.slotIndex
}

/**
 * Gets index of an item in a chest using its lore.
 * @return null if not found.
 */
fun getItemIndexInContainerChestByLore(
    container: ContainerChest,
    lore: String,
    subList: IntRange = 0..container.inventory.size - 36,
    ignoreCase: Boolean = false
): Int? {
    return container.inventorySlots.subList(subList.first, subList.last + 1).firstOrNull {
        it.stack?.lore?.contains(lore) == true
    }?.slotIndex
}

enum class ItemRarity(
    val loreName: String,
    val colorCode: String,
    val color: Color
) {
    COMMON("COMMON", "§f", Color.WHITE),
    UNCOMMON("UNCOMMON", "§2", Color.GREEN),
    RARE("RARE", "§9", Color.BLUE),
    EPIC("EPIC", "§5", Color.PURPLE),
    LEGENDARY("LEGENDARY", "§6", Color.ORANGE),
    MYTHIC("MYTHIC", "§d", Color.MAGENTA),
    DIVINE("DIVINE", "§b", Color.CYAN),
    SPECIAL("SPECIAL", "§c", Color.RED),
    VERY_SPECIAL("VERY SPECIAL", "§c", Color.RED);
}

private val rarityRegex =
    Regex("§l(?<rarity>${ItemRarity.entries.joinToString("|") { it.loreName }}) ?(?<type>[A-Z ]+)?(?:§[0-9a-f]§l§ka)?$")

/**
 * Gets the rarity of an item
 * @param lore Lore of an item
 * @return ItemRarity or null if not found
 */
fun getRarity(lore: List<String>): ItemRarity? {
    // Start from the end since the rarity is usually the last line or one of the last.
    for (i in lore.indices.reversed()) {
        val rarity = rarityRegex.find(lore[i])?.groups?.get("rarity")?.value ?: continue
        return ItemRarity.entries.find { it.loreName == rarity }
    }
    return null
}

fun getSkullValue(entity: Entity?): String? = entity?.inventory?.get(4)?.skullTexture

fun ItemStack.setLore(lines: List<String>): ItemStack {
    setTagInfo("display", getSubCompound("display", true).apply {
        setTag("Lore", NBTTagList().apply {
            for (line in lines) appendTag(NBTTagString(line))
        })
    })
    return this
}

val strengthRegex = Regex("Strength: \\+(\\d+)")

/**
 * Returns the primary Strength value for an Item
 */
val ItemStack?.getSBStrength: Int
    get() {
        return this?.lore?.firstOrNull { it.noControlCodes.startsWith("Strength:") }
            ?.let { loreLine -> strengthRegex.find(loreLine.noControlCodes)?.groups?.get(1)?.value?.toIntOrNull() } ?: 0
    }

fun ItemStack.setLoreWidth(lines: List<String>, width: Int): ItemStack {
    setTagInfo("display", getSubCompound("display", true).apply {
        setTag("Lore", NBTTagList().apply {
            for (line in lines) {
                val words = line.split(" ")
                var currentLine = ""
                for (word in words) {
                    if ((currentLine + word).length <= width) {
                        currentLine += if (currentLine.isNotEmpty()) " $word" else word
                    } else {
                        appendTag(NBTTagString(currentLine))
                        currentLine = word
                    }
                }
                if (currentLine.isNotEmpty()) {
                    appendTag(NBTTagString(currentLine))
                }
            }
        })
    })
    return this
}

fun ItemStack.drawItem(x: Float = 0f, y: Float = 0f, scale: Float = 1f, z: Float = 200f) {
    GlStateManager.pushMatrix()
    scale(scale, scale, 1f)
    translate(x / scale, y / scale, 0f)
    Color.WHITE.bind()

    RenderHelper.enableStandardItemLighting()
    RenderHelper.enableGUIStandardItemLighting()

    mc.renderItem.zLevel = z
    mc.renderItem.renderItemIntoGUI(this, 0, 0)
    RenderHelper.disableStandardItemLighting()
    GlStateManager.popMatrix()
}


var ItemStack.skullTexture: String?
    get() = this.tagCompound
        ?.getCompoundTag("SkullOwner")
        ?.getCompoundTag("Properties")
        ?.getTagList("textures", Constants.NBT.TAG_COMPOUND)
        ?.getCompoundTagAt(0)
        ?.getString("Value")
    set(value) {
        if (value == null) {
            this.tagCompound?.removeTag("SkullOwner")
        } else {
            if (this.tagCompound == null) {
                this.tagCompound = NBTTagCompound()
            }
            val skullOwner: NBTTagCompound = if (this.tagCompound.hasKey("SkullOwner", Constants.NBT.TAG_COMPOUND)) {
                this.tagCompound.getCompoundTag("SkullOwner")
            } else {
                NBTTagCompound().also { this.tagCompound.setTag("SkullOwner", it) }
            }
            val properties: NBTTagCompound = if (skullOwner.hasKey("Properties", Constants.NBT.TAG_COMPOUND)) {
                skullOwner.getCompoundTag("Properties")
            } else {
                NBTTagCompound().also { skullOwner.setTag("Properties", it) }
            }
            val textures: NBTTagList = if (properties.hasKey("textures", Constants.NBT.TAG_LIST)) {
                properties.getTagList("textures", Constants.NBT.TAG_COMPOUND)
            } else {
                NBTTagList().also { properties.setTag("textures", it) }
            }
            if (textures.tagCount() == 0) {
                val textureTag = NBTTagCompound()
                textureTag.setString("Value", value)
                textures.appendTag(textureTag)
            } else {
                val textureTag = textures.getCompoundTagAt(0)
                textureTag.setString("Value", value)
            }

        }
    }
