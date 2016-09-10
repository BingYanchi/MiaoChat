package pw.yumc.MiaoChat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import pw.yumc.MiaoChat.config.Config;
import pw.yumc.MiaoChat.listeners.ChatListener;
import pw.yumc.YumCore.bukkit.Log;
import pw.yumc.YumCore.commands.CommandArgument;
import pw.yumc.YumCore.commands.CommandExecutor;
import pw.yumc.YumCore.commands.CommandManager;
import pw.yumc.YumCore.commands.annotation.Cmd;
import pw.yumc.YumCore.commands.annotation.Help;
import pw.yumc.YumCore.config.FileConfig;

public class MiaoChat extends JavaPlugin implements CommandExecutor {
    private FileConfig cfg;
    private Config config;

    @Override
    public FileConfiguration getConfig() {
        return cfg;
    }

    public Config getConfigExt() {
        return config;
    }

    @Cmd(permission = "MiaoChat.toggle")
    @Help("关闭聊天功能")
    public void off(final CommandArgument e) {
        ChatListener.offList.add(e.getSender().getName());
        Log.toSender(e.getSender(), "§c聊天功能已关闭!");
    }

    @Cmd(permission = "MiaoChat.toggle")
    @Help("开启聊天功能")
    public void on(final CommandArgument e) {
        ChatListener.offList.remove(e.getSender().getName());
        Log.toSender(e.getSender(), "§a聊天功能已开启!");
    }

    @Override
    public void onEnable() {
        new ChatListener();
        new CommandManager("MiaoChat", this);
    }

    @Override
    public void onLoad() {
        cfg = new FileConfig();
        config = new Config();
    }

    @Cmd(permission = "MiaoChat.reload")
    @Help("重载配置文件")
    public void reload(final CommandArgument e) {
        cfg.reload();
        config.reload();
        Log.toSender(e.getSender(), "§a配置文件已重载!");
    }
}
