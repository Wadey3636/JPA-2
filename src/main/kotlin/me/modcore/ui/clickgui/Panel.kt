package me.modcore.ui.clickgui


import me.modcore.features.Category
import me.modcore.features.ModuleManager.modules
import me.modcore.features.impl.render.ClickGUIModule
import me.modcore.font.FontRenderer
import me.modcore.ui.clickgui.SearchBar.currentSearch
import me.modcore.ui.clickgui.animations.impl.LinearAnimation
import me.modcore.ui.clickgui.elements.ModuleButton
import me.modcore.ui.clickgui.util.ColorUtil
import me.modcore.ui.clickgui.util.ColorUtil.brighter
import me.modcore.ui.clickgui.util.ColorUtil.darkerIf
import me.modcore.ui.clickgui.util.ColorUtil.titlePanelColor
import me.modcore.ui.util.MouseUtils.isAreaHovered
import me.modcore.ui.util.MouseUtils.mouseX
import me.modcore.ui.util.MouseUtils.mouseY
import me.modcore.utils.capitalizeFirst
import me.modcore.utils.render.*
import me.modcore.utils.render.RenderUtils.loadBufferedImage
import me.modcore.utils.round
import me.modcore.utils.skyblock.devMessage
import net.minecraft.client.renderer.texture.DynamicTexture
import kotlin.math.floor


/**
 * Renders all the panels.
 *
 * Backend made by Aton, with some changes
 * Design mostly made by Stivais
 *
 * @author Stivais, Aton
 * @see [ModuleButton]
 */
class Panel(
    var category: Category,
) {
    private val renderIcon = DynamicTexture(loadBufferedImage("/assets/modcore/clickgui/render.png"))
    private val floor7Icon = DynamicTexture(loadBufferedImage("/assets/modcore/clickgui/wither.png"))
    private val miscIcon = DynamicTexture(loadBufferedImage("/assets/modcore/clickgui/misc.png"))
    private val dungeonIcon = DynamicTexture(loadBufferedImage("/assets/modcore/clickgui/dungeon.png"))

    val displayName = category.name.lowercase().capitalizeFirst()

    private var dragging = false
    val moduleButtons: ArrayList<ModuleButton> = ArrayList()

    var x = ClickGUIModule.panelX[category]!!.value
    var y = ClickGUIModule.panelY[category]!!.value

    var extended: Boolean = ClickGUIModule.panelExtended[category]!!.enabled

    private var length = 0f

    private var x2 = 0f
    private var y2 = 0f

    private var scrollTarget = 0f
    private var scrollOffset = 0f
    private val scrollAnimation = LinearAnimation<Float>(200)

    init {
        for (module in modules.sortedByDescending { getTextWidth(it.name, 18f) }) {
            if (module.category != this@Panel.category) continue
            moduleButtons.add(ModuleButton(module, this@Panel))
        }
    }

    fun draw() {

        if (dragging) {
            x = floor(x2 + mouseX)
            y = floor(y2 + mouseY)
        }

        scrollOffset = scrollAnimation.get(scrollOffset, scrollTarget).round(0).toFloat()
        var startY = scrollOffset + HEIGHT
        scale(1f / scaleFactor, 1f / scaleFactor, 1f)
        dropShadow(x, y, WIDTH, if (extended) (length + 5f).coerceAtLeast(HEIGHT) else 50f, ColorUtil.moduleButtonColor, 5f, 3f, 3f, 3f, 3f)

        roundedRectangle(x, y, WIDTH, HEIGHT, titlePanelColor, titlePanelColor, titlePanelColor, 0f, 10f, 10f, 0f, 0f, 0f)
        var additionalOffset = 0.0
        val imageSize = 25
        when(category){
            Category.RENDER -> {
                additionalOffset = 4.0
                drawDynamicTexture(renderIcon, x + WIDTH * 0.08 - imageSize / 2 + additionalOffset, y + HEIGHT / 2 - imageSize / 2 - 2, imageSize, imageSize)
            }
            Category.FLOOR7 -> {
                additionalOffset = 8.0
                drawDynamicTexture(floor7Icon, x + WIDTH * 0.08  - imageSize / 2 + 4, y + HEIGHT / 2  - imageSize / 2, imageSize, imageSize)
            }
            Category.MISC -> {
                additionalOffset = -4.0
                drawDynamicTexture(miscIcon, x + WIDTH * 0.08  - imageSize / 2 + 4, y + HEIGHT / 2  - imageSize / 2, imageSize, imageSize)
            }
            Category.DUNGEONS -> {
                additionalOffset = 4.0
                drawDynamicTexture(dungeonIcon, x + WIDTH * 0.08  - imageSize / 2, y + HEIGHT / 2  - imageSize / 2, imageSize, imageSize)
            }

            else -> {

            }

        }

        text(if (displayName == "Floor7") "Floor 7" else displayName, x + WIDTH * 0.3 + additionalOffset, y + HEIGHT / 2f, ColorUtil.textColor, 15f, type = FontRenderer.BOLD, TextAlign.Middle)

        //draw Panel Line
        if (extended && moduleButtons.isNotEmpty()) roundedRectangle(x, y + HEIGHT - 2, WIDTH, 2, ColorUtil.clickGUIColor.brighter(1.65f))
        //draw minus sign
        roundedRectangle(x + WIDTH * 0.85 + 5, y + HEIGHT * 0.4 , 20, 5, Color.WHITE.darkerIf(isHoveredOverExtendToggle, 0.7f), radius = 1.5f)




        val s = scissor(x, y + HEIGHT, WIDTH, 5000f)
        if (extended && moduleButtons.isNotEmpty()) {

            for (button in moduleButtons.filter { it.module.name.contains(currentSearch, true) }) {
                button.y = startY
                startY += button.draw()
            }
            length = startY + 5f
        }
        //draw bottom
        if (extended) {
            roundedRectangle(
                x,
                y + startY,
                WIDTH,
                10f,
                ColorUtil.moduleButtonColor,
                ColorUtil.moduleButtonColor,
                ColorUtil.moduleButtonColor,
                0f,
                0f,
                0f,
                10f,
                10f,
                0f
            )
        } else {
            roundedRectangle(
                x,
                y + startY,
                WIDTH,
                10f,
                titlePanelColor,
                titlePanelColor,
                titlePanelColor,
                0f,
                0f,
                0f,
                10f,
                10f,
                0f
            )
        }
        resetScissor(s)
        scale(scaleFactor, scaleFactor, 1f)
    }

    fun handleScroll(amount: Int): Boolean {
        if (isMouseOverExtended && currentSearch.isBlank()) {
            scrollTarget = (scrollTarget + amount).coerceIn(-length + scrollOffset + 72f, 0f)
            scrollAnimation.start(true)
            return true
        }
        return false
    }

    fun mouseClicked(mouseButton: Int): Boolean {
        if (isHoveredOverExtendToggle) {
            extended = !extended
            return true
        }
        if (isHovered) {
            if (mouseButton == 0) {
                x2 = x - mouseX
                y2 = y - mouseY
                dragging = true
                return true
            } else if (mouseButton == 1) {
                extended = !extended
                return true
            }
        } else if (isMouseOverExtended) {
            return moduleButtons.filter { it.module.name.contains(currentSearch, true) }.reversed().any {
                it.mouseClicked(mouseButton)
            }
        }
        return moduleButtons.filter { it.module.name.contains(currentSearch, true) }.reversed().any {
            it.mouseClickedAnywhere(mouseButton)
        }
    }

    fun mouseReleased(state: Int) {
        if (state == 0) dragging = false

        ClickGUIModule.panelX[category]!!.value = x
        ClickGUIModule.panelY[category]!!.value = y
        ClickGUIModule.panelExtended[category]!!.enabled = extended

        if (extended) {
            moduleButtons.filter { it.module.name.contains(currentSearch, true) }.reversed().forEach {
                it.mouseReleased(state)
            }
        }
    }

    fun keyTyped(typedChar: Char, keyCode: Int): Boolean {
        return if (extended) {
            moduleButtons.filter { it.module.name.contains(currentSearch, true) }.reversed().any {
                it.keyTyped(typedChar, keyCode)
            }
        } else false
    }

    private val isHovered
        get() = isAreaHovered(x, y, WIDTH, HEIGHT)

    private val isHoveredOverExtendToggle
        get() = isAreaHovered(x + WIDTH * 0.85f - 5f, y, WIDTH * 0.15f + 5f, HEIGHT - 2f)


    private val isMouseOverExtended
        get() = extended && isAreaHovered(x, y, WIDTH, length.coerceAtLeast(HEIGHT))

    companion object {
        const val WIDTH = 240f
        const val HEIGHT = 40f
    }
}