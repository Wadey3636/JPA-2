// Function copied from [https://github.com/odtheking/Odin/blob/44062aed8e0307333e45efbde24b9e384e3476ec/src/main/kotlin/me/odinmain/utils/AsyncUtils.kt#L7]
// Copyright (c) 2024, odtheking
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// 3. Neither the name of the copyright holder nor the names of its
//    contributors may be used to endorse or promote products derived from
//    this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.github.wadey3636.jpa.utils

import me.modcore.Core.mc
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

object GuiUtils {


    /**
     * @author odtheking
     *
     * Checks if the current string contains at least one of the specified strings.
     *
     * @param options List of strings to check.
     * @param ignoreCase If comparison should be case-sensitive or not.
     * @return `true` if the string contains at least one of the specified options, otherwise `false`.
     */
    fun String.containsOneOf(vararg options: String, ignoreCase: Boolean = false): Boolean {
        return options.any { this.contains(it, ignoreCase) }
    }


    /**
     * Returns the ExtraAttribute Compound
     * @author odtheking
     */
    val ItemStack?.extraAttributes: NBTTagCompound?
        get() = this?.getSubCompound("ExtraAttributes", false)

    /**
     * Returns Item ID for an Item
     * @author odtheking
     */
    val ItemStack?.skyblockID: String
        get() = this?.extraAttributes?.getString("id") ?: ""

    /**
     * Removes color codes from a string
     */
    val String.deformat: String
        get() = this.replace(Regex("§[0-9a-fk-or]"), "")


    /**
     * Takes the inputted inventory and fetches all items except glass.
     * @param inventory IInventory
     * @return MutableList<Item>
     */

    fun getGUI(inventory: IInventory): MutableList<Item> {
        val items: MutableList<Item> = mutableListOf()
        for (i in 0 until inventory.sizeInventory) {
            inventory.getStackInSlot(i)?.let { stack ->
                if (!stack.item.unlocalizedName.contains("Glass", ignoreCase = true) &&
                    !stack.item.unlocalizedName.contains("Barrier", ignoreCase = true) &&
                    !stack.item.unlocalizedName.contains("Arrow", ignoreCase = true)
                ) {
                    items.add(
                        Item(
                            stack.displayName,
                            stack.getTooltip(mc.thePlayer, false)
                                .drop(1)
                                .map { it.deformat.trim() },
                            i
                        )
                    )
                }

            }
        }
        return items
    }

    private fun getInventoryItemStacks(inventory: IInventory): MutableList<Slot> {
        val items: MutableList<Slot> = mutableListOf()
        for (i in 0 until inventory.sizeInventory) {

            inventory.getStackInSlot(i)?.let { stack ->
                items.add(
                    Slot(i, stack)
                )

            }
        }
        return items
    }

    val GuiChest.display: String
        get() = getDisplayName(this)

    private fun getDisplayName(gui: GuiChest): String {
        val container = gui.inventorySlots
        if (container is ContainerChest) {
            return container.lowerChestInventory.displayName.unformattedText
        }
        return ""
    }


    val IInventory.getStacks: MutableList<Slot>
        get() = getInventoryItemStacks(this)


    private fun getStacksFromGuiChest(guiChest: GuiChest): MutableList<Slot> {
        val gui = guiChest.inventorySlots as ContainerChest
        return gui.lowerChestInventory.getStacks


    }

    val GuiChest.getStacks: MutableList<Slot>
        get() = getStacksFromGuiChest(this)


    fun getInventory(): Array<out ItemStack>? {
        return mc.thePlayer?.inventory?.mainInventory
    }

    fun getContainer(): MutableList<ItemStack>? {
        return mc.thePlayer?.inventoryContainer?.inventory
    }


    /**
     * Returns the stackSize of the first instance of an item
     * @param itemID String of a Skyblock ItemID
     * @param inventory Array<out ItemStack>? The Inventory
     */

    fun getItemCount(itemID: String, inventory: Array<out ItemStack>?): Int {
        return inventory?.find { it.skyblockID == itemID }?.stackSize ?: 0
    }
}

data class Item(val name: String, val lore: List<String>, val position: Int)
data class Slot(val index: Int, val itemStack: ItemStack)