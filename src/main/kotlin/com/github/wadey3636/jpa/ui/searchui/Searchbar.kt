package com.github.wadey3636.jpa.ui.searchui

import com.github.wadey3636.jpa.Core.mc
import com.github.wadey3636.jpa.ui.clickgui.util.ColorUtil
import com.github.wadey3636.jpa.utils.render.roundedRectangle

object Searchbar {
    var searchText: String = ""
    inline val screenWidth get() = mc.displayWidth
    inline val screenHeight get() = mc.displayHeight


    fun draw(){
        roundedRectangle(
            (screenWidth / 2) - screenWidth * 0.3,
            screenHeight * 0.2,
            screenWidth * 0.3,
            screenHeight * 0.05,
            ColorUtil.elementBackground
        )
        draw()
    }





}