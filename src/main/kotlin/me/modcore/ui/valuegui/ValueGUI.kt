package me.modcore.ui.valuegui

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import me.modcore.config.DataManager
import me.modcore.ui.Screen
import me.modcore.utils.render.Color
import me.modcore.utils.render.roundedRectangle
import me.modcore.utils.render.translate
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Mouse
import kotlin.math.sign

object ValueGUI : Screen() {
    private val gson = GsonBuilder().setPrettyPrinting().create()


    private var values: MutableMap<String, Float> = mutableMapOf()
    init {
        loadValues()
    }

    override fun draw() {
        GlStateManager.pushMatrix()
        translate(0f, 0f, 200f)
        roundedRectangle(100, 100, 500, 500, Color.BLUE)


        translate(0f, 0f, -200f)
        GlStateManager.popMatrix()
    }
    override fun initGui() {
        loadValues()
    }
    private fun loadValues() {
        values.clear()
        val array = DataManager.loadDataFromFile("Values")
        for (element in array) {
            val entry = gson.fromJson(element, Map.Entry::class.java) as Map.Entry<String, Float>
            values[entry.key] = entry.value
        }
    }

    override fun onGuiClosed() {
        super.onGuiClosed()
        saveValues()
    }
    private fun saveValues(){
        val array = JsonArray()
        for (value in values) {
            array.add(gson.toJsonTree(value))
        }
        DataManager.saveDataToFile("Values", array)
    }


    override fun onScroll(amount: Int) {
        if (Mouse.getEventDWheel() != 0) {
            val actualAmount = amount.sign * 16
        }
    }

}
