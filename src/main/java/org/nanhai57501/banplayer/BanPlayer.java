package org.nanhai57501.banplayer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.nanhai57501.banplayer.commands.AdminCommand;
import org.nanhai57501.banplayer.commands.GuestCommand;
import org.nanhai57501.banplayer.listeners.GuiListener;
import org.nanhai57501.banplayer.listeners.PlayerListener;

import java.io.File;
import java.util.logging.Logger;

public final class BanPlayer extends JavaPlugin {

    public static Data data;
    public static GUIs guis;
    public static Logger logger;
    public static File file;

    //instance
    public static BanPlayer getInstance() {
        return getPlugin(BanPlayer.class);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();
        logger.info("BanPlayer 启动！");
        if (!(getDataFolder().exists())) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(), "players");
        file.mkdir();
        new File(file.getPath(), "online").mkdir(); //正版
        new File(file.getPath(), "offline").mkdir();//离线
        if (Bukkit.getOnlineMode()) {
            //正版
            this.file = new File(getDataFolder() + "/players/online");
        } else {
            //离线
            this.file = new File(getDataFolder() + "/players/offline");
        }
        data = new Data();
        guis = new GUIs();
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        getCommand("banMenu").setExecutor(new GuestCommand());
        getCommand("banadmin").setExecutor(new AdminCommand());
        logger.info("BanPlayer 成功喵");

        Bukkit.getOnlineMode();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
