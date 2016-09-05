package pw.yumc.MiaoChat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import pw.yumc.MiaoChat.config.ChatConfig;
import pw.yumc.MiaoChat.listeners.ChatListener;
import pw.yumc.YumCore.commands.CommandArgument;
import pw.yumc.YumCore.commands.CommandExecutor;
import pw.yumc.YumCore.commands.CommandManager;
import pw.yumc.YumCore.commands.annotation.Cmd;
import pw.yumc.YumCore.commands.annotation.Help;
import pw.yumc.YumCore.config.FileConfig;

public class MiaoChat extends JavaPlugin implements CommandExecutor {
    private ChatConfig chatConfig;
    private FileConfig config;

    public ChatConfig getChatConfig() {
        return chatConfig;
    }

    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    @Override
    public void onEnable() {
        new ChatListener();
        new CommandManager("MiaoChat", this);
    }

    @Override
    public void onLoad() {
        config = new FileConfig();
        chatConfig = new ChatConfig();
    }

    @Cmd(permission = "MiaoChat.reload")
    @Help("重载配置文件")
    public void reload(final CommandArgument e) {
        config.reload();
        chatConfig.reload();
        e.getSender().sendMessage("§a配置文件已重载!");
    }
}
