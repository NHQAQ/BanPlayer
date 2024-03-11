# BanPlayer By NanHai

## 简介

折磨违反规定的玩家喵 有更多的折磨方法可以提喵
请在正版服务器使用喵 离线登陆的服务器咱没测试过喵

## 待实现的功能

自动解封
离线与正版分离

## 使用教程

### 步骤一：下载插件

下载最新的插件并将其放入服务器的插件目录中。

### 步骤二：指令

#### 普通玩家权限

- 权限：`banplayer.command.Guest`

  - `/banmenu` 进入封禁菜单，也可以使用缩写 `/bm`

#### 管理员权限

- 权限：`banplayer.command.Admin`

  - `/banadmin [add] <Player> <time>` 添加一个玩家到封禁列表
  - `/banadmin [remove] <Player>` 撤销一次封禁记录
  - `/banadmin [unban] <Player>` 解封一个玩家（从菜单中移除）
  - `/banadmin [list]` 查看存在封禁记录的玩家列表
  - `/banadmin [view] <Player>` 查看特定玩家的封禁信息
  - `/banadmin [reload]` 更新数据

缩写为 `ba`

## 注意事项

- 确保下载最新版本的插件。
- 请确保按照上述指令的权限进行设置。
- time格式为 `1d` 或者 `1h`，表示1天或者1小时。
