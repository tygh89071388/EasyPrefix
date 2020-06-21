package com.christian34.easyprefix.commands;

import com.christian34.easyprefix.EasyPrefix;
import com.christian34.easyprefix.groups.Group;
import com.christian34.easyprefix.groups.GroupHandler;
import com.christian34.easyprefix.groups.Subgroup;
import com.christian34.easyprefix.groups.gender.GenderType;
import com.christian34.easyprefix.messages.Message;
import com.christian34.easyprefix.messages.Messages;
import com.christian34.easyprefix.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.List;

/**
 * EasyPrefix 2020.
 *
 * @author Christian34
 */
public class Command_User implements EasyCommand {

    public boolean handleCommand(CommandSender sender, List<String> args) {
        Player player = Bukkit.getPlayer(args.get(1));
        EasyPrefix instance = EasyPrefix.getInstance();
        GroupHandler groupHandler = instance.getGroupHandler();
        if (player == null) {
            sender.sendMessage(Messages.getMessage(Message.PLAYER_NOT_FOUND));
            return true;
        }
        User target = new User(player);
        target.login();
        if (args.size() >= 3) {
            if (args.get(2).equalsIgnoreCase("reload")) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(EasyPrefix.getInstance().getPlugin(), () -> {
                    target.login();
                    sender.sendMessage(Messages.getMessage(Message.SUCCESS));
                }, 20L);
                return true;
            } else if (args.get(2).equalsIgnoreCase("info")) {
                sender.sendMessage(" \n§7--------------=== §5§l" + target.getPlayer().getName() + " §7===--------------\n ");
                sender.sendMessage("§5Group§f: §7" + target.getGroup().getName());
                String subgroup = (target.getSubgroup() != null) ? target.getSubgroup().getName() : "-";
                sender.sendMessage("§5Subgroup§f: §7" + subgroup);
                sender.sendMessage("§5Prefix§f: §8«§7" + target.getPrefix().replace("§", "&") + "§8»"
                        + (target.hasCustomPrefix() ? " §7(§5customized§7)" : ""));
                if (target.hasCustomPrefix()) {
                    sender.sendMessage(" §7↳ §5last update§f: §7" + new Timestamp(target.getLastPrefixUpdate()).toString());
                }
                sender.sendMessage("§5Suffix§f: §8«§7" + target.getSuffix().replace("§", "&") + "§8»"
                        + (target.hasCustomSuffix() ? " §7(§5customized§7)" : ""));
                if (target.hasCustomSuffix()) {
                    sender.sendMessage(" §7↳ §5last update§f: §7" + new Timestamp(target.getLastSuffixUpdate()).toString());
                }
                String cc = (target.getChatColor() != null) ? target.getChatColor().getCode() : "-";
                if (target.getChatFormatting() != null) cc = cc + target.getChatFormatting().getCode();
                sender.sendMessage("§5Chatcolor§f: §7" + cc.replace("§", "&"));
                if (target.getGenderType() != null) {
                    sender.sendMessage("§5Gender§f: §7" + target.getGenderType().getDisplayName() + "§7/§7" + target.getGenderType().getName());
                }
                sender.sendMessage(" \n§7-----------------------------------------------\n ");
                return true;
            } else if (args.get(2).equalsIgnoreCase("setgroup")) {
                if (args.size() == 4) {
                    if (groupHandler.isGroup(args.get(3))) {
                        Group targetGroup = groupHandler.getGroup(args.get(3));
                        target.setGroup(targetGroup, true);
                        sender.sendMessage(Messages.getMessage(Message.SUCCESS));
                    } else {
                        sender.sendMessage(Messages.getMessage(Message.GROUP_NOT_FOUND));
                    }
                    return true;
                }
            } else if (args.get(2).equalsIgnoreCase("setsubgroup")) {
                if (args.size() == 4) {
                    if (groupHandler.isSubgroup(args.get(3))) {
                        Subgroup targetGroup = groupHandler.getSubgroup(args.get(3));
                        target.setSubgroup(targetGroup);
                        sender.sendMessage(Messages.getMessage(Message.SUCCESS));
                        return true;
                    } else if (args.get(3).equalsIgnoreCase("none")) {
                        target.setSubgroup(null);
                        sender.sendMessage(Messages.getMessage(Message.SUCCESS));
                        return true;
                    } else {
                        sender.sendMessage(Messages.getMessage(Message.GROUP_NOT_FOUND));
                        return true;
                    }
                }
            } else if (args.get(2).equalsIgnoreCase("setgender")) {
                if (args.size() == 4) {
                    GenderType genderType = groupHandler.getGender(args.get(3));
                    if (genderType != null) {
                        target.setGenderType(genderType);
                        sender.sendMessage(Messages.getMessage(Message.SUCCESS));
                    } else {
                        sender.sendMessage(Messages.getPrefix() + "§cThis gender doesn't exist");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public String getPermission() {
        return "EasyPrefix.admin";
    }

}