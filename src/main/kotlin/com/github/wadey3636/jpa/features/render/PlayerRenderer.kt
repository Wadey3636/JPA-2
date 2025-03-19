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
package com.github.wadey3636.jpa.features.render

import me.modcore.Core.mc
import com.mojang.authlib.GameProfile
import me.modcore.Core.display
import me.modcore.features.Module
import me.modcore.features.settings.impl.ActionSetting
import me.modcore.ui.playerCustomizerGUI.PlayerCustomizerGUI
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.renderer.GlStateManager.scale
import net.minecraft.client.renderer.GlStateManager.translate
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.io.File
import javax.imageio.ImageIO

var playerEntries: MutableList<PlayerEntry> = mutableListOf()
@SideOnly(Side.CLIENT)
object PlayerRenderer : Module(name = "Player Customizer", description = "The scale, skin, and armor of players") {
    private val openGUI by ActionSetting(
        "Open GUI",
        description = "Opens the GUI to customize players",
        default = { display = PlayerCustomizerGUI }
    )

    private var helmet: ItemStack? = null
    private var chestplate: ItemStack? = null
    private var leggings: ItemStack? = null
    private var boots: ItemStack? = null
    /**
     * Adapted from odin
     */
    fun preRenderCallbackScaleHook(entityLivingBaseIn: AbstractClientPlayer) {
        if (!enabled) return
        val entry =
            playerEntries.toList().firstOrNull { it.name.lowercase() == entityLivingBaseIn.name.lowercase() } ?: return
        if (entry.toggle) {
            if (entry.dinnerBone) {
                scale(entry.entryX, -entry.entryY, entry.entryZ)
                translate(0.0, 2.0, 0.0)
            } else {
                scale(entry.entryX, entry.entryY, entry.entryZ)
            }

        }
    }

    fun injectCustomSkin(entityLivingBaseIn: AbstractClientPlayer): ResourceLocation? {
        if (!enabled) return entityLivingBaseIn.locationSkin
        val entry = playerEntries.firstOrNull { it.name.lowercase() == entityLivingBaseIn.name.lowercase() }
            ?: return entityLivingBaseIn.locationSkin
        if (!entry.toggle || entry.texture == null || !entry.toggleTexture) return entityLivingBaseIn.locationSkin
        val file = File(mc.mcDataDir, "config/jpa/textures/${entry.texture}")
        if (!file.exists()) return entityLivingBaseIn.locationSkin
        return try {
            val bufferedImage = ImageIO.read(file)
            val dynamicTexture = DynamicTexture(bufferedImage)
            mc.textureManager.getDynamicTextureLocation("custom_skin", dynamicTexture)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }
/*
    fun renderLayer(entityLivingBaseIn: EntityLivingBase, armorSlot: Int): Boolean {
        return false
        if (!enabled) return false
        val entry = playerEntries.toList().firstOrNull { it.name.lowercase() == entityLivingBaseIn.name.lowercase() }
            ?: return false
        if (entry.toggle) {
            when (armorSlot) {
                4 -> {
                    if (entry.hideHelmet) return true
                }

                3 -> {
                    if (entry.hideChestplate) return true
                }

                2 -> {
                    if (entry.hideLeggings) return true
                }

                1 -> {
                    if (entry.hideBoots) return true
                }

            }

        }

        return false
    }
 */


    @SubscribeEvent
    fun renderPlayer(event: RenderPlayerEvent.Pre) {
        val entry =
            playerEntries.toList().firstOrNull { it.name.lowercase() == event.entityPlayer.name.lowercase() } ?: return
        if (!entry.toggle) return

        val entityArmor = event.entityPlayer.inventory.armorInventory
        if (event.entityPlayer == mc.thePlayer) {
            helmet = entityArmor[3]
            chestplate = entityArmor[2]
            leggings = entityArmor[1]
            boots = entityArmor[0]
        }
        if (entry.hideHelmet) {
            entityArmor[3] = null
        }
        if (entry.hideChestplate) {
            entityArmor[2] = null
        }
        if (entry.hideLeggings) {
            entityArmor[1] = null
        }
        if (entry.hideBoots) {
            entityArmor[0] = null
        }

    }
    @SubscribeEvent
    fun resetArmor(event: RenderPlayerEvent.Post) {
        if (event.entityPlayer != mc.thePlayer) return
        val playerArmor = event.entityPlayer.inventory.armorInventory
        if (helmet != null) {
            playerArmor[3] = helmet
            helmet = null
        }
        if (chestplate != null) {
            playerArmor[2] = chestplate
            chestplate = null
        }
        if (leggings != null) {
            playerArmor[1] = leggings
            leggings = null
        }
        if (boots != null) {
            playerArmor[0] = boots
            boots = null
        }
    }

}

data class PlayerEntry(
    var name: String,
    var entryX: Double,
    var entryY: Double,
    var entryZ: Double,
    var toggleTexture: Boolean,
    var texture: String?,
    var dinnerBone: Boolean,
    var hideHelmet: Boolean,
    var hideChestplate: Boolean,
    var hideLeggings: Boolean,
    var hideBoots: Boolean,
    var toggle: Boolean,
    var profile: GameProfile?
)