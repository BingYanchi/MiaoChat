package pw.yumc.MiaoChat;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import pw.yumc.MiaoChat.config.ChatConfig;
import pw.yumc.MiaoChat.listeners.ChatListener;
import pw.yumc.YumCore.bukkit.Log;
import pw.yumc.YumCore.commands.CommandManager;
import pw.yumc.YumCore.commands.annotation.Cmd;
import pw.yumc.YumCore.commands.annotation.Help;
import pw.yumc.YumCore.commands.interfaces.CommandExecutor;
import pw.yumc.YumCore.config.FileConfig;
import pw.yumc.YumCore.global.L10N;

public class MiaoChat extends JavaPlugin implements CommandExecutor {
    private FileConfig cfg;
    private ChatConfig chatConfig;

    public ChatConfig getChatConfig() {
        return chatConfig;
    }

    @Override
    public FileConfiguration getConfig() {
        return cfg;
    }

    @Cmd(permission = "MiaoChat.toggle")
    @Help("关闭聊天功能")
    public void off(CommandSender sender) {
        ChatListener.offList.add(sender.getName());
        Log.toSender(sender, "§c聊天功能已关闭!");
    }

    @Cmd(permission = "MiaoChat.toggle")
    @Help("开启聊天功能")
    public void on(CommandSender sender) {
        ChatListener.offList.remove(sender.getName());
        Log.toSender(sender, "§a聊天功能已开启!");
    }

    @Override
    public void onEnable() {
        new ChatListener();
        new CommandManager("MiaoChat", this);
        L10N.getName(new ItemStack(Material.AIR));
    }

    @Override
    public void onLoad() {
        cfg = new FileConfig();
        chatConfig = new ChatConfig();
    }

    @Cmd(permission = "MiaoChat.reload")
    @Help("重载配置文件")
    public void reload(CommandSender sender) {
        cfg.reload();
        chatConfig.reload();
        Log.toSender(sender, "§a配置文件已重载!");
    }
}
