package org.nanhai57501.banplayer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Data {
    private final Gson gson = new Gson();
    public Set<DataPlayer> dataPlayers;

    /**
     * 将所有在服务器内游玩过的玩家新建数据
     */
    public void init() {
        @NotNull OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        IO io = new IO();
        for (OfflinePlayer target: offlinePlayers) {
            DataPlayer playerData = findPlayerData(target.getUniqueId());
            io.write(io.getPlayerFile(target.getUniqueId().toString()), playerData.toJson());
        }
    }

    public void update() { //读取所有数据 也可以是在数据被更改后重新读取
        List<JsonObject> data = new ArrayList<>();
        File file = new File(BanPlayer.getInstance().getDataFolder() + "/players");
        File[] files = file.listFiles(); //已经存在的数据
//        for (File file1 : files) {
//            BanPlayer.logger.info(file1.getPath());
//        }
        if (files != null) {
            for (File f : files) {
                if (f.getName().equals(".DS_Store")) continue;
//                BanPlayer.logger.info(f.getPath());
                JsonObject jsonObject = new IO().readFromFile(f);
                data.add(jsonObject);
            }
        }
        Set<DataPlayer> dataObj = new HashSet<>();
        for (JsonObject jo : data) {
            DataPlayer pData = new DataPlayer(jo);
            dataObj.add(pData);
        }
        dataPlayers = dataObj;
    }

    public Data() {
        update(); //更新一次 读取所有文件
        init(); //写入
        update(); //再次更新写入部分
    }

    class IO {
        public File getPlayerFile(String uuid) {
            File file = new File(BanPlayer.getInstance().getDataFolder() + "/players", uuid + ".data");
            if (!file.exists()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.add("data", new JsonObject());
                this.write(file, jsonObject);
            }
            return file;
        }
        public JsonObject readFromFile(File file) {
            try {
                JsonObject json = gson.fromJson(new String(Files.readAllBytes(file.toPath())), JsonObject.class);
                return json;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * 给目标文件写入json
         * @param file
         * @param jsonObject
         * @return
         */
        public JsonObject write(File file, JsonObject jsonObject) {
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(gson.toJson(jsonObject));
                writer.close();
                return jsonObject;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * 更新数据并保存
         * @param dataPlayer
         */
        public void update(DataPlayer dataPlayer) {
            write(getPlayerFile(dataPlayer.getTarget()), dataPlayer.toJson());
        }

    }

    /**
     * 返回找到的玩家数据，如果不存在就建一个返回 此操作会直接写入一个文件
     * @param target Player
     * @return DataPlayer(绝对不为空)
     */
    public DataPlayer findPlayerData(UUID target) {
        for (DataPlayer dataPlayer : dataPlayers) {
            if (dataPlayer.getTarget().equals(target.toString())) {
                return dataPlayer; //存在
            }
        }
        //不存在
        return createPlayerDataFile(target);
    }
    public DataPlayer findPlayerData(String target) {
        for (DataPlayer dataPlayer : dataPlayers) {
            if (dataPlayer.getTarget().equals(target)) {
                return dataPlayer; //存在
            }
        }
        //不存在
        return createPlayerDataFile(target);
    }


    private DataPlayer createPlayerDataFile(UUID target) {
        if (target == null) return null;
        DataPlayer dataPlayer = new DataPlayer(target.toString());
        IO io = new IO();
        io.write(io.getPlayerFile(target.toString()), dataPlayer.toJson()); //写入文件
        dataPlayers.add(dataPlayer);
        return dataPlayer;
    }

    private DataPlayer createPlayerDataFile(String target) {
        String uuid = target;
        DataPlayer dataPlayer = new DataPlayer(uuid);
        IO io = new IO();
        io.write(io.getPlayerFile(uuid), dataPlayer.toJson()); //写入文件
        dataPlayers.add(dataPlayer);
        return dataPlayer;
    }

    /**
     * 1. 玩家存在添加记录
     * 2. 玩家存在更新时间
     * @param target Player
     * @param time 封禁时长
     * @return true 添加成功 false 数据无效
     */
    public boolean addPlayer(Player target, String time) {
        DataPlayer playerData = findPlayerData(target.getUniqueId().toString());
        if (!checkTime(time)) return false;
        long millis = toMillis(time);
        if (millis == -1) {
            return false;
        }
        IO io = new IO();
        //有记录的情况下
        if (!(playerData.getHistories().isEmpty())) {
            //1. 正在被封禁
            if (playerData.isBan()) {
                List<Histories> Histories = playerData.getHistories();

                Histories histories = playerData.addHistory(Histories.get(Histories.size() - 1), millis);
                io.update(playerData);
                update();

                BanPlayer.logger.info(Keys.getUuidName(target) + " 添加成功喵"
                        + "\n " + histories.toString());
                return true;
            }
        }
        //2. 已经解封/根本没有记录
        Histories histories = playerData.addHistory(System.currentTimeMillis(), millis);
        playerData.setBan(true);
        io.update(playerData);
        update();
        BanPlayer.logger.info(Keys.getUuidName(target) + " 添加成功喵"
                + "\n " + histories.toString());
        return true;
    }

    /**
     * 撤回一次记录
     * @param target
     * @return true 撤回成功 false 玩家没有被封禁
     */
    public boolean reHistoryPlayer(Player target) {
        DataPlayer playerData = findPlayerData(target.getUniqueId().toString());
        if (!(playerData.getHistories().isEmpty())) {
            Histories histories = playerData.getHistories().get(playerData.getHistories().size() - 1);
            playerData.removeHistory(playerData.getHistories().size() - 1, false);
            new IO().update(playerData);
            update();
            BanPlayer.logger.info(Keys.getUuidName(target) + " 撤回成功喵" +
                    "\n " + histories.toString());
            return true;
        }
        BanPlayer.logger.info(Keys.getUuidName(target) + " 没有封禁记录喵");
        return false;
    }

    /**
     * 手动解封玩家喵(实现移除玩家在主菜单中的记录喵
     *  (DataPlayer.isBan = false && DataPlayer.getHistories.get(DataPlayer.getHistories.size() - 1).isBan)
     * )
     * @param target
     * @return 理论上不可能出现false
     */
    public boolean unbanPlayer(Player target) {
        //解封玩家
        //找到最后一次的记录 比对两个isBan是否为true
        DataPlayer playerData = findPlayerData(target.getUniqueId().toString());
        if (playerData.getHistories().isEmpty()) {
            BanPlayer.logger.info(Keys.getUuidName(target) + " 目标数据不存在喵");
            return true;
        }
        Histories history = playerData.getHistories().get(playerData.getHistories().size() - 1);
        if (history.isBan) { //数据正常
            history.isBan = false;
            playerData.setBan(false);
            new IO().update(playerData);
            update();
            BanPlayer.logger.info(Keys.getUuidName(target) + " 被ban用户下架于主菜单喵");
            return true;
        }
        //异常 history.isBan = false but playerData.isBan = true
        //修复一次数据 将DataPlayer.isBan = 最新数据的isBan
        updateDataPlayerIsBan(playerData);
        new IO().update(playerData);
        update();
        BanPlayer.logger.info(Keys.getUuidName(target) + " 被ban用户下架于主菜单喵");
        return true;
    }
    //todo 自动解封功能喵(有需要再写)


    //修复错误数据喵(一切以历史数据为准喵 DataPlayer.isBan = DataPlayer.getHistories.get(DataPlayer.getHistories.size() - 1))
    public void updateDataPlayerIsBan(DataPlayer dataPlayer) {
        dataPlayer.setBan(dataPlayer.getHistories().get(dataPlayer.getHistories().size() - 1).isBan);
    }

    /**
     * 将封禁时间转换成long
     * @param time
     * @return 为-1时数据无效
     */
    public long toMillis(String time) {
        int sub = Integer.parseInt(time.substring(0, time.length()-1));
        switch (time.substring(time.length() - 1)) {
            case "h":
                return 1000L * 60 * 60 * sub;
            case "d":
                return 1000L * 60 * 60 * 24 * sub;
        }
        return -1;
    }

    /**
     * 将时间戳转成 xxd/xxh
     * @param time
     * @return
     */
    public static String toTime(long time) {
        time = time / 1000 / 60 / 60; //换成小时喵
        if (time % 24 == 0) {
            return time / 24 + "d";
        }
        return time + "h";
    }

    /**
     * time.matches("^\\d+[hd]$\n")
     * @param time
     * @return
     */
    public boolean checkTime(String time) {
        return time.matches("^\\d+[hd]$");
    }

    public List<String> getAllPlayerName() {
        List<String> names = new ArrayList<>();
        List<UUID> uuids = new ArrayList<>();
        for (OfflinePlayer player: Bukkit.getOfflinePlayers()) {
            uuids.add(player.getUniqueId());
        }
        for (UUID uuid: uuids) {
            names.add(Bukkit.getOfflinePlayer(uuid).getName());
        }
        return names;
    }

    public List<String> getAllBannedPlayerName() {
        List<String> names = new ArrayList<>();
        for (DataPlayer data: getAllBanedPlayer()) {
            names.add(Bukkit.getOfflinePlayer(UUID.fromString(data.getTarget())).getName());
        }
        return names;
    }

    private List<DataPlayer> getAllBanedPlayer() {
        List<DataPlayer> dp = new ArrayList<>();
        for (DataPlayer data: dataPlayers) {
            if (!(data.getHistories().isEmpty())) {
                dp.add(data);
            }
        }
        return dp;
    }

    public Set<String> getAllPlayerUuid() {
        Set<String> uuids = new HashSet<>();
        for (OfflinePlayer player: Bukkit.getOfflinePlayers()) {
            uuids.add(player.getUniqueId().toString());
        }
        return uuids;
    }

    /**
     * dataPlayer.toString
     * @param target uuid
     * @return
     */
    public String dataPlayerToString(String target) {
        return findPlayerData(target).toString();
    }



}

class DataPlayer {
    private final String target; //uuid 唯一性
    private boolean isBan = false; //玩家是否处于封禁状态 默认未封禁
    /**
     * 历史记录
     * histories.size()-1 为最新记录
     * 加时长的解决办法: (histories.size()-1).unbanTime + 新的banTimes
     *                  addHistory((histories.size()-1).banTime, (histories.size()-1).unbanTime
     */
    private final List<Histories> histories; //当没有记录的时候为null

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return target.equals(((DataPlayer) obj).target);
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{target:").append(target).append(",isBan:").append(isBan()).append(",历史记录:[");
        int length = sb.length();
        for (Histories history: this.histories) {
            sb.append(history.toString()).append(",\n");
        }
        if (length < sb.length()) {
            sb.delete(sb.length()-2, sb.length());
        }
        sb.append("]}");
        return sb.toString();
    }

    /**
     * 添加玩家 不做任何操作
     * @param target
     */
    public DataPlayer(String target) {
        this.target = target;
        this.histories = new ArrayList<>();
    }

    /**
     * 添加历史记录
     * @param banTime 封禁开始时间戳
     * @param banTimes 封禁时长时间戳
     */
    public Histories addHistory(long banTime, long banTimes) {
        Histories history = new Histories();
        history.banTime = banTime;
        history.unbanTime = banTime + banTimes;
        history.banTimes = banTimes;
        this.histories.add(history);
        this.isBan = true;
        return history;
    }

    /**
     * 添加新的历史记录（更新封禁时间喵）
     * @param histories 最新的一次历史记录
     * @param banTimes 需要添加的封禁时长
     */
    public Histories addHistory(Histories histories, long banTimes) {
        Histories history = new Histories();
        history.banTime = histories.banTime;
        history.unbanTime = histories.unbanTime + banTimes;
        history.banTimes = banTimes;
        this.histories.add(history);
        this.isBan = true;
        return history;
    }

    /**
     * 删除指定记录喵(撤回)
     * @param index
     * @return
     */
    public boolean removeHistory(int index) {
        return histories.remove(histories.get(index));
    }

    /**
     * 撤回一次记录并解封喵 如果上一条记录并未解封 则不更改isBan
     * @param index
     * @param isBan
     * @return
     */
    public boolean removeHistory(int index, boolean isBan) {
        if (histories.get(index - 1).isBan) {
            return histories.remove(histories.get(index));
        }
        this.isBan = isBan;
        return histories.remove(histories.get(index));
    }

    public DataPlayer(JsonObject jsonObject) {
        this.target = jsonObject.get("target").getAsString();
        this.isBan = jsonObject.get("isBan").getAsBoolean();
        this.histories = new ArrayList<>();
        for (int i = 0; i < jsonObject.get("histories").getAsJsonArray().size(); i++) {
            JsonObject history = jsonObject.get("histories").getAsJsonArray().get(i).getAsJsonObject();
            Histories histories = new Histories();
            histories.banTime = history.get("banTime").getAsLong();
            histories.unbanTime = history.get("unbanTime").getAsLong();
            histories.banTimes = history.get("banTimes").getAsLong();
            histories.isBan = history.get("isBan").getAsBoolean();
            this.histories.add(histories);
        }
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("target", this.target);
        jsonObject.addProperty("isBan", this.isBan);
        jsonObject.add("histories", new JsonArray());
        for (int i = 0; i < this.histories.size(); i++) {
            Histories history = this.histories.get(i);
            JsonObject historyJson = new JsonObject();
            historyJson.addProperty("banTime", history.banTime);
            historyJson.addProperty("unbanTime", history.unbanTime);
            historyJson.addProperty("banTimes", history.banTimes);
            historyJson.addProperty("isBan", history.isBan);
            jsonObject.get("histories").getAsJsonArray().add(historyJson);
        }
        return jsonObject;
    }

    public static String toTime(long timeMillis){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM:dd:yyyy HH:mm:ss");
        return dateTimeFormatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), ZoneId.systemDefault()));
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(UUID.fromString(target));
    }

    public String getTarget() {
        return target;
    }

    public List<Histories> getHistories() {
        return histories;
    }

    public boolean isBan() {
        return isBan;
    }

    public void setBan(boolean ban) {
        isBan = ban;
    }
}

class Histories {
    long banTime;
    long unbanTime;
    long banTimes;
    /*
     * 当前记录是否处于被封禁状态 (当记录建立时 默认为true)
     * 解封时改成false
     * 应对撤回的情况
     * 如果上一条记录的isBan == true, 则撤回本次封禁 继续进行上一条封禁
     */
    boolean isBan = true;

    @Override
    public String toString() {
        return new StringBuilder()
                .append("{banTime:").append(DataPlayer.toTime(this.banTime))
                .append(",unbanTime:").append(DataPlayer.toTime(this.unbanTime))
                .append(",banTimes:").append(Data.toTime(this.banTimes))
                .append(",isBan:").append(this.isBan)
                .append("}")
                .toString();
    }

    public long getBanTime() {
        return banTime;
    }

    public long getUnbanTime() {
        return unbanTime;
    }

    public long getBanTimes() {
        return banTimes;
    }

    public boolean isBan() {
        return isBan;
    }
}