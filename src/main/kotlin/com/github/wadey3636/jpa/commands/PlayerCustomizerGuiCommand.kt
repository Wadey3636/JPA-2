package com.github.wadey3636.jpa.commands

import me.modcore.Core
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import me.modcore.Core.display
import me.modcore.ui.clickgui.ClickGUI
import me.modcore.ui.playerCustomizerGUI.PlayerCustomizerGUI
import org.apache.logging.log4j.LogManager

class PlayerCustomizerGuiCommand : CommandBase() {
    override fun getCommandName(): String {
        return "jpplayercustomizer"
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return "Opens the Player Customizer Gui"
    }

    override fun processCommand(sender: ICommandSender?, args: Array<out String>?) {
        Core.logger.info("Attempting GUI Open!")
        display = PlayerCustomizerGUI
    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
        return true
    }
    override fun getCommandAliases(): List<String> {
        return listOf("jpc", "jppc")
    }

}