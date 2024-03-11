package org.nanhai57501.banplayer;

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
        new File(getDataFolder(),"players").mkdir();
        data = new Data();
        guis = new GUIs();
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        getCommand("banMenu").setExecutor(new GuestCommand());
        getCommand("banadmin").setExecutor(new AdminCommand());
        logger.info("BanPlayer 成功喵");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
