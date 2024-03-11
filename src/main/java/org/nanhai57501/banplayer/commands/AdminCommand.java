package org.nanhai57501.banplayer.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nanhai57501.banplayer.BanPlayer;
import org.nanhai57501.banplayer.Keys;

import java.util.*;

public class AdminCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!commandSender.hasPermission("banplayer.command.Admin")) return false;
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command!");
            return true;
        }
        Player player = (Player) commandSender;
        if (!(player.isOp())) {
            player.sendMessage("先买个管理再来喵");
            return true;
        }
        if (strings.length == 0) {
            player.sendMessage("=========================");
            player.sendMessage("time格式: 1d 或者 1h 喵");
            player.sendMessage("/banadmin [add] <Player> <time> - 添加一个玩家到封禁列表");
            player.sendMessage("/banadmin [remove] <Player> - 撤回一次记录喵");
            player.sendMessage("/banadmin [unban] <Player> - 解封一个玩家喵(从菜单中下架)");
            player.sendMessage("/banadmin [list] - 存在封禁记录的玩家列表喵");
            player.sendMessage("/banadmin [view] <Player> - 查看玩家最新封禁信息喵");
            player.sendMessage("/banadmin [reload] - 更新数据");
            player.sendMessage("=========================");
            return true;
        }

        String name;
        Player target;

        switch (strings[0]) {
            case "add":
                if (strings.length < 3) {
                    player.sendMessage("time格式: 1d 或者 1h 喵");
                    player.sendMessage("/banadmin [add] <Player> <time> - 添加一个玩家到封禁列表");
                    return true;
                }
                name = strings[1];
                target = (Player) Bukkit.getOfflinePlayer(name);
                String time = strings[2];

                if (BanPlayer.data.addPlayer(target, time)) {
                    player.sendMessage(Keys.getUuidName(target) + " 该玩家以被加入喵");
                    return true;
                }
                player.sendMessage("无效操作喵 请检查玩家是否进入过服务器 时间格式是否正确喵");
                return true;
            case "remove":
                if (strings.length < 2) {
                    player.sendMessage("/banadmin [remove] <Player> - 撤回一次记录喵");
                    return true;
                }
                name = strings[1];
                target = (Player) Bukkit.getOfflinePlayer(name);
                if (BanPlayer.data.reHistoryPlayer(target)) {
                    player.sendMessage(Keys.getUuidName(target) + " 撤回成功喵");
                    return true;
                }
                player.sendMessage("该玩家没有被封禁(也有可能是玩家不在线喵)");
                return true;
            case "unban":
                if (strings.length < 2) {
                    player.sendMessage("/banadmin [unban] <Player> - 解封一个玩家喵(从菜单中下架)");
                    return true;
                }
                name = strings[1];
                target = (Player) Bukkit.getOfflinePlayer(name);
                if (BanPlayer.data.unbanPlayer(target)) {
                    player.sendMessage(Keys.getUuidName(target) + " 该玩家解封成功喵");
                    return true;
                }
                player.sendMessage("如果你看到这句话 说明咱喵没有想到还能等于这个结果");
                return true;
            case "list": //
                List<String> pn = BanPlayer.data.getAllBannedPlayerName();
                StringBuilder sb = new StringBuilder();
                for (String pname: pn) {
                    sb.append(pname).append(",");
                }
                if (sb.length() > 0)
                    sb.deleteCharAt(sb.length()-1);
                player.sendMessage(sb.toString());
                return true;
            case "view":
                if (strings.length < 2) {
                    player.sendMessage("/banadmin [view] <Player> - 查看玩家最新封禁信息喵");
                    return true;
                }
                name = strings[1];
                target = (Player) Bukkit.getOfflinePlayer(name);
                player.sendMessage(BanPlayer.data.dataPlayerToString(target.getUniqueId().toString()));
                return true;
            case "reload":
                BanPlayer.data.update();
                return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return Arrays.asList(
                    "add",
                    "remove",
                    "unban",
                    "list",
                    "view",
                    "reload"
            );
        }
        if (strings.length == 2) {
            switch (strings[0]) {
                case "add":
                case "remove":
                case "unban":
                case "view":
                    //所有进入过服务器的玩家
                    return BanPlayer.data.getAllPlayerName();
            }
        }
        return Arrays.asList();
    }
}
