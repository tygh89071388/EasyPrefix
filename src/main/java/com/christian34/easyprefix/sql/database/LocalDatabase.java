package com.christian34.easyprefix.sql.database;

import com.christian34.easyprefix.EasyPrefix;
import com.christian34.easyprefix.files.FileManager;
import com.christian34.easyprefix.messages.Messages;
import com.christian34.easyprefix.utils.Debug;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * EasyPrefix 2020.
 *
 * @author Christian34
 */
public class LocalDatabase implements Database {
    private final EasyPrefix instance;
    private Connection connection;

    public LocalDatabase(EasyPrefix instance) {
        this.instance = instance;
        File file = new File(FileManager.getPluginFolder() + "/storage.db");
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        connect();

        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `" + getTablePrefix() + "users` (`uuid` CHAR(36) NOT NULL, `username` VARCHAR(20) NULL DEFAULT NULL, `group` VARCHAR(64) NULL DEFAULT NULL, `force_group` BOOLEAN NULL DEFAULT NULL, `subgroup` VARCHAR(64) NULL DEFAULT NULL, " + "`custom_prefix` VARCHAR(128) NULL DEFAULT NULL, `custom_prefix_update` TIMESTAMP NULL DEFAULT NULL, " + "`custom_suffix` VARCHAR(128) NULL DEFAULT NULL, `custom_suffix_update` TIMESTAMP NULL DEFAULT NULL, " + "`gender` VARCHAR(32) NULL DEFAULT NULL, `chat_color` CHAR(2) NULL DEFAULT NULL, `chat_formatting` CHAR(2) NULL DEFAULT NULL, PRIMARY KEY(`uuid`))");
            stmt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void connect() {
        synchronized (this) {
            try {
                if (connection != null && !connection.isClosed()) return;
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + FileManager.getPluginFolder() + "/storage" + ".db");
            } catch (SQLException e) {
                Messages.log("§cCouldn't connect to local storage!");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                Messages.log("§cYour installation does not support sqlite!");
            }
        }
    }

    @Override
    public void close() {
        synchronized (this) {
            try {
                if (getConnection() != null && !getConnection().isClosed()) {
                    getConnection().close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean exists(String statement) {
        try {
            if (connection.isClosed()) connect();
            Statement stmt = connection.createStatement();
            statement = statement.replace("%p%", getTablePrefix());
            return stmt.executeQuery(statement).next();
        } catch (SQLException e) {
            Messages.log("§cCouldn't get value from statement '" + statement + "'!");
            Messages.log("§c" + e.getMessage());
            Debug.captureException(e);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getTablePrefix() {
        return "";
    }

}