package com.github.wadey3636.jpa.commands

import com.github.wadey3636.jpa.Core
import com.github.wadey3636.jpa.Core.display
import com.github.wadey3636.jpa.ui.clickgui.ClickGUI
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender

class ModCommand : CommandBase() {
    override fun getCommandName(): String {
        return "jpa"
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return "Opens JPA GUI"
    }

    override fun processCommand(sender: ICommandSender?, args: Array<out String>?) {
        Core.logger.info("Attempting GUI Open!")
        display = ClickGUI
    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
        return true
    }

    override fun getCommandAliases(): List<String> {
        return listOf("jp", "jpp", "jpenis")
    }

}