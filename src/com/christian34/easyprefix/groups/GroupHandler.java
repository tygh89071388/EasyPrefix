package com.christian34.easyprefix.groups;

import com.christian34.easyprefix.EasyPrefix;
import com.christian34.easyprefix.database.Database;
import com.christian34.easyprefix.files.ConfigData;
import com.christian34.easyprefix.files.FileManager;
import com.christian34.easyprefix.files.GroupsData;
import com.christian34.easyprefix.groups.gender.GenderType;
import com.christian34.easyprefix.messages.Messages;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

/**
 * EasyPrefix 2020.
 *
 * @author Christian34
 */
public class GroupHandler {
    private final EasyPrefix instance;
    private final GroupsData groupsData;
    private ArrayList<Group> groups;
    private ArrayList<Subgroup> subgroups;
    private ArrayList<GenderType> genderTypes = new ArrayList<>();
    private Group defaultGroup;

    public GroupHandler(EasyPrefix instance) {
        this.instance = instance;
        this.groupsData = instance.getFileManager().getGroupsData();

        if (EasyPrefix.getInstance().getSqlDatabase() == null) {
            GroupsData groupsData = getGroupsData();
            FileConfiguration fileData = groupsData.getData();
            if (fileData.getString("groups.default.prefix") == null) {
                groupsData.set("groups.default.prefix", "&7");
            }
            if (fileData.getString("groups.default.suffix") == null) {
                groupsData.set("groups.default.suffix", "&f:");
            }
            if (fileData.getString("groups.default.chat-color") == null) {
                groupsData.set("groups.default.chat-color", "&7");
            }
            if (fileData.getString("groups.default.join-msg") == null) {
                groupsData.set("groups.default.join-msg", "&8» %ep_user_prefix% %player% &8joined the game");
            }
            if (fileData.getString("groups.default.quit-msg") == null) {
                groupsData.set("groups.default.quit-msg", "&8« %ep_user_prefix% %player% &8left the game");
            }
            groupsData.save();
        } else {
            Database database = EasyPrefix.getInstance().getSqlDatabase();
            if (!database.exists("SELECT `prefix` FROM `%p%groups` WHERE `group` = 'default'")) {
                database.update("INSERT INTO `%p%groups`(`group`, `prefix`, `suffix`, `chat_color`, `join_msg`, `quit_msg`) " + "VALUES ('default','&7','&f:','&7','&8» %ep_user_prefix%%player% &7joined the game','&8« %ep_user_prefix%%player% &7left the game')");
                Messages.log("&cError: You haven't uploaded any data to the sql database yet. Please upload your data" + " with: /easyprefix database upload");
            }
        }
    }

    public void load() {
        this.groups = new ArrayList<>();
        this.subgroups = new ArrayList<>();
        this.instance.getUsers().clear();
        if (instance.getFileManager().getConfig().getBoolean(ConfigData.ConfigKeys.USE_GENDER)) {
            loadGenders();
        }
        this.defaultGroup = new Group(this, "default");
        groups.add(defaultGroup);

        ArrayList<String> groupNames = new ArrayList<>();
        ArrayList<String> subgroupNames = new ArrayList<>();

        if (EasyPrefix.getInstance().getSqlDatabase() == null) {
            GroupsData groupsData = getGroupsData();
            Set<String> groupsList = getGroupsData().getSection("groups");
            groupNames = new ArrayList<>(groupsList);
            if (groupNames.isEmpty()) {
                File old = new File(FileManager.getPluginFolder(), "groups.yml");
                if (old.renameTo(new File(FileManager.getPluginFolder(), "backup-groups.yml"))) {
                    groupsData.load();
                    load();
                }
            }
            if (instance.getFileManager().getConfig().getBoolean(ConfigData.ConfigKeys.USE_SUBGROUPS)) {
                subgroupNames.addAll(groupsData.getSection("subgroups"));
            }
        } else {
            Database database = EasyPrefix.getInstance().getSqlDatabase();
            ResultSet groupsResult = database.getValue("SELECT `group` FROM `%p%groups`");
            try {
                while (groupsResult.next()) {
                    String value = groupsResult.getString("group");
                    if (!value.equals("default")) groupNames.add(value);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            ResultSet subgroupsResult = database.getValue("SELECT `group` FROM `%p%subgroups`");
            try {
                while (subgroupsResult.next()) {
                    String value = subgroupsResult.getString("group");
                    subgroupNames.add(value);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
        }

        groupNames.remove("default");
        for (String name : groupNames) {
            groups.add(new Group(this, name));
        }

        for (String name : subgroupNames) {
            subgroups.add(new Subgroup(this, name));
        }

    }

    public boolean handleGenders() {
        return instance.getFileManager().getConfig().getBoolean(ConfigData.ConfigKeys.USE_GENDER);
    }

    public void loadGenders() {
        this.genderTypes = new ArrayList<>();
        ConfigData config = instance.getFileManager().getConfig();
        Set<String> types = Objects.requireNonNull(config.getData().getConfigurationSection("config.gender.types")).getKeys(false);
        for (String name : types) {
            GenderType genderType = new GenderType(name);
            this.genderTypes.add(genderType);
        }
    }

    public ArrayList<GenderType> getGenderTypes() {
        return genderTypes;
    }

    public GenderType getGender(String name) {
        if (name == null) return null;
        for (GenderType genderType : this.genderTypes) {
            if (genderType.getName().equalsIgnoreCase(name)) return genderType;
        }
        return null;
    }

    public Group getGroup(String name) {
        for (Group crntGroup : groups) {
            if (crntGroup.getName().equalsIgnoreCase(name)) {
                return crntGroup;
            }
        }
        return defaultGroup;
    }

    public Subgroup getSubgroup(String subgroupName) {
        for (Subgroup group : subgroups) {
            if (group.getName().equalsIgnoreCase(subgroupName)) return group;
        }
        return null;
    }

    public Boolean isGroup(String groupName) {
        for (Group group : groups) {
            if (group.getName().equalsIgnoreCase(groupName)) return true;
        }
        return false;
    }

    public Boolean isSubgroup(String group) {
        for (Subgroup crntGroup : subgroups) {
            if (crntGroup.getName().equalsIgnoreCase(group)) return true;
        }
        return false;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public ArrayList<Subgroup> getSubgroups() {
        return subgroups;
    }

    public EasyPrefix getInstance() {
        return instance;
    }

    public void createGroup(String groupName) {
        Database database = EasyPrefix.getInstance().getSqlDatabase();
        if (database == null) {
            String path = "groups." + groupName + ".";
            getGroupsData().set(path + "prefix", "&6" + groupName + " &7| &8");
            getGroupsData().set(path + "suffix", "&f:");
            getGroupsData().set(path + "chat-color", "&7");
            getGroupsData().set(path + "chat-formatting", "&o");
            getGroupsData().save();
        } else {
            database.update("INSERT INTO `%p%groups`(`group`) VALUES ('" + groupName + "')");
        }
        load();
    }

    private GroupsData getGroupsData() {
        return groupsData;
    }

}