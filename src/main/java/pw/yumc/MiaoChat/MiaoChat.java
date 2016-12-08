package pw.yumc.MiaoChat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import pw.yumc.MiaoChat.config.ChatConfig;
import pw.yumc.MiaoChat.listeners.ChatListener;
import pw.yumc.YumCore.bukkit.Log;
import pw.yumc.YumCore.bukkit.compatible.C;
import pw.yumc.YumCore.commands.CommandSub;
import pw.yumc.YumCore.commands.annotation.Cmd;
import pw.yumc.YumCore.commands.annotation.Help;
import pw.yumc.YumCore.commands.interfaces.Executor;
import pw.yumc.YumCore.config.FileConfig;
import pw.yumc.YumCore.global.L10N;

public class MiaoChat extends JavaPlugin implements Executor, PluginMessageListener {
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
        Log.sender(sender, "§c聊天功能已关闭!");
    }

    @Cmd(permission = "MiaoChat.toggle")
    @Help("开启聊天功能")
    public void on(CommandSender sender) {
        ChatListener.offList.remove(sender.getName());
        Log.sender(sender, "§a聊天功能已开启!");
    }

    @Override
    public void onEnable() {
        new ChatListener();
        new CommandSub("MiaoChat", this);
        if (getChatConfig().isBungeeCord()) {
            Log.info("已开启 BUngeeCord 模式!");
            Bukkit.getMessenger().registerIncomingPluginChannel(this, MiaoMessage.CHANNEL, this);
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, MiaoMessage.CHANNEL);
            Bukkit.getMessenger().registerIncomingPluginChannel(this, MiaoMessage.NORMALCHANNEL, this);
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, MiaoMessage.NORMALCHANNEL);
        }
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
        Log.sender(sender, "§a配置文件已重载!");
    }

    public static void send(byte[] in) {
        send(MiaoMessage.decode(in).getJson());
    }

    public static void send(String json) {
        for (Player player : C.Player.getOnlinePlayers()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + json);
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (MiaoMessage.CHANNEL.equals(channel)) {
            send(message);
        } else if (MiaoMessage.NORMALCHANNEL.equals(channel)) {
            for (Player p : C.Player.getOnlinePlayers()) {
                p.sendMessage(MiaoMessage.decode(message).getJson());
            }
        }
    }
}
