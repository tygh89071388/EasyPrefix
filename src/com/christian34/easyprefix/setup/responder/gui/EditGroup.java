package com.christian34.easyprefix.setup.responder.gui;

import com.christian34.easyprefix.groups.EasyGroup;
import com.christian34.easyprefix.groups.Group;
import com.christian34.easyprefix.messages.Message;
import com.christian34.easyprefix.messages.Messages;
import com.christian34.easyprefix.setup.Button;
import com.christian34.easyprefix.setup.CustomInventory;
import com.christian34.easyprefix.setup.responder.ChatRespond;
import com.christian34.easyprefix.setup.responder.GuiRespond;
import com.christian34.easyprefix.user.User;
import com.christian34.easyprefix.utils.ChatFormatting;
import com.christian34.easyprefix.utils.Color;
import org.bukkit.Material;

import java.util.List;

/**
 * EasyPrefix 2020.
 *
 * @author Christian34
 */
public class EditGroup {
    private final User user;
    private final EasyGroup easyGroup;

    public EditGroup(User user, EasyGroup easyGroup) {
        this.user = user;
        this.easyGroup = easyGroup;
    }

    public void editPrefix() {
        new ChatRespond(user, Messages.getText(Message.SET_PREFIX).replace("%name%", easyGroup.getName()).replace("%prefix%", easyGroup.getPrefix(null, false)), (answer) -> {
            easyGroup.setPrefix(answer);
            user.sendMessage(Messages.getText(Message.INPUT_SAVED));
            return ChatRespond.Respond.ACCEPTED;
        });
    }

    public void editSuffix() {
        new ChatRespond(user, Messages.getText(Message.CHAT_INPUT_SUFFIX).replace("%suffix%", easyGroup.getSuffix(null, false)), (answer) -> {
            easyGroup.setSuffix(answer);
            user.sendMessage(Messages.getText(Message.INPUT_SAVED));
            return ChatRespond.Respond.ACCEPTED;
        });
    }

    public void editJoinMessage() {
        if (!(easyGroup instanceof Group)) return;
        Group group = (Group) easyGroup;
        new ChatRespond(user, "§5What should be the new join message?%newline%§5Current: §7" + group.getJoinMessageText(), (answer) -> {
            group.setJoinMessage(answer);
            user.sendMessage(Messages.getText(Message.INPUT_SAVED));
            return ChatRespond.Respond.ACCEPTED;
        });
    }

    public void editQuitMessage() {
        if (!(easyGroup instanceof Group)) return;
        Group group = (Group) easyGroup;
        new ChatRespond(user, "§5What should be the new quit message?%newline%§5Current: §7" + group.getQuitMessageText(), (answer) -> {
            group.setQuitMessage(answer);
            user.sendMessage(Messages.getText(Message.INPUT_SAVED));
            return ChatRespond.Respond.ACCEPTED;
        });
    }

    public void editChatColor() {
        if (!(easyGroup instanceof Group)) return;
        Group group = (Group) easyGroup;
        CustomInventory inventory = new CustomInventory(easyGroup.getGroupColor() + easyGroup.getName() + " §8» " + Messages.getText(Message.SETTINGS_TITLE_FORMATTINGS), 5);
        int slot = 9;
        for (Color color : Color.getValues()) {
            if (color == Color.UNDEFINED) continue;
            if (slot == 18) slot++;
            Button button = color.toTerracotta().setSlot(slot);
            button.setDisplayName(color.toString());
            button.setData("color", color.name());
            if (group.getChatColor() != null && group.getChatColor().equals(color)) button.addEnchantment();
            inventory.addItem(button);
            slot++;
        }
        slot = 29;
        for (ChatFormatting chatFormatting : ChatFormatting.getValues()) {
            List<String> lore = Messages.getList(Message.LORE_SELECT_COLOR);
            Button button = new Button(Material.BOOKSHELF, chatFormatting.toString(), lore).setSlot(slot);
            button.setData("formatting", chatFormatting.name());
            if (group.getChatFormatting() != null && group.getChatFormatting().equals(chatFormatting))
                button.addEnchantment();
            inventory.addItem(button);
            slot++;
        }
        new GuiRespond(user, inventory, (respond) -> {
            String name = respond.getDisplayName();
            if (name.equals(Messages.getText(Message.BTN_BACK))) {
                new GroupProfile(user, easyGroup);
            } else {
                if (respond.getData("color") != null) {
                    Color color = Color.valueOf(respond.getData("color"));
                    if (!color.equals(Color.UNDEFINED)) {
                        if (group.getChatColor() != null && group.getChatColor().equals(color)) {
                            return;
                        }
                        group.setChatColor(color);
                        editChatColor();
                    }
                } else if (respond.getData("formatting") != null) {
                    ChatFormatting formatting = ChatFormatting.valueOf(respond.getData("formatting"));
                    if (!formatting.equals(ChatFormatting.UNDEFINED)) {
                        if (group.getChatFormatting() != null && group.getChatFormatting().equals(formatting)) {
                            if (!group.getChatFormatting().equals(ChatFormatting.RAINBOW)) {
                                formatting = null;
                            }
                        }
                        if (formatting != null && !formatting.equals(ChatFormatting.RAINBOW) && group.getChatColor() == null) {
                            return;
                        }
                        group.setChatFormatting(formatting);
                        editChatColor();
                    }
                }
            }
        });
    }

    public void deleteConfirmation() {
        CustomInventory inventory = new CustomInventory(Messages.getText(Message.SETUP_GROUP_TITLE_DELETE).replace("%group%", easyGroup.getName()), 3);
        Button btnConfirm = Color.GREEN.toTerracotta().setSlot(2, 4);
        btnConfirm.setDisplayName(Messages.getText(Message.BTN_CONFIRM));
        inventory.addItem(btnConfirm);
        Button btnCancel = Color.RED.toTerracotta().setSlot(2, 6);
        btnCancel.setDisplayName(Messages.getText(Message.BTN_CANCEL));
        inventory.addItem(btnCancel);
        new GuiRespond(user, inventory, (button) -> {
            if (button.getDisplayName().equals(Messages.getText(Message.BTN_CONFIRM))) {
                easyGroup.delete();
                new GroupsList(user);
            } else {
                new GroupProfile(user, easyGroup);
            }
        }).preventClose(true);
    }

}