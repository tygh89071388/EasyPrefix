package com.christian34.easyprefix.commands.easyprefix.set;

import com.christian34.easyprefix.commands.Subcommand;
import com.christian34.easyprefix.commands.easyprefix.EasyPrefixCommand;
import com.christian34.easyprefix.user.UserPermission;
import com.christian34.easyprefix.utils.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * EasyPrefix 2020.
 *
 * @author Christian34
 */
public class SetPrefixCommand implements Subcommand {
    private final EasyPrefixCommand parentCommand;

    public SetPrefixCommand(EasyPrefixCommand parentCommand) {
        this.parentCommand = parentCommand;
    }

    @Override
    @Nullable
    public UserPermission getPermission() {
        return UserPermission.CUSTOM_PREFIX;
    }

    @Override
    @NotNull
    public String getDescription() {
        return "changes your prefix, reset it to default value with command 'setprefix reset'";
    }

    @Override
    @NotNull
    public String getCommandUsage() {
        return "setprefix <prefix> or setprefix reset";
    }

    @Override
    @NotNull
    public String getName() {
        return "setprefix";
    }

    @Override
    public void handleCommand(@NotNull CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.PREFIX + Message.CHAT_PLAYER_ONLY);
            return;
        }

        if (args.size() < 2) {
            parentCommand.getSubcommand("help").handleCommand(sender, null);
        }
    }

    @Override
    public List<String> getTabCompletion(@NotNull CommandSender sender, List<String> args) {
        return Collections.emptyList();
    }

}
