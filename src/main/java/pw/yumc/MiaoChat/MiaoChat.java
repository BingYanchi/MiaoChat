package pw.yumc.MiaoChat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import pw.yumc.MiaoChat.config.Config;
import pw.yumc.MiaoChat.listeners.ChatListener;
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

    @Override
    public void onEnable() {
        new ChatListener();
        new CommandManager("MiaoChat", this);
    }

    @Override
    public void onLoad() {
        cfg = new FileConfig();
    }

    @Cmd(permission = "MiaoChat.reload")
    @Help("重载配置文件")
    public void reload(final CommandArgument e) {
        cfg.reload();
        config.reload();
        e.getSender().sendMessage("§a配置文件已重载!");
    }
}
