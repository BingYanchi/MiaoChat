package pw.yumc.MiaoChat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
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

public class MiaoChat extends JavaPlugin implements Executor, PluginMessageListener, Listener {
    private FileConfig cfg;
    private ChatConfig chatConfig;
    private String ServerName;

    public ChatConfig getChatConfig() {
        return chatConfig;
    }

    @Override
    public FileConfiguration getConfig() {
        return cfg;
    }

    @Cmd(permission = "MiaoChat.toggle", executor = Cmd.Executor.PLAYER)
    @Help("关闭聊天功能")
    public void off(Player sender) {
        ChatListener.offList.add(sender);
        Log.sender(sender, "§c聊天功能已关闭!");
    }

    @Cmd(permission = "MiaoChat.toggle", executor = Cmd.Executor.PLAYER)
    @Help("开启聊天功能")
    public void on(Player sender) {
        ChatListener.offList.remove(sender);
        Log.sender(sender, "§a聊天功能已开启!");
    }

    @Override
    public void onEnable() {
        new ChatListener();
        new CommandSub("MiaoChat", this);
        enableBungeeCord();
        hookPlaceholderAPI();
        L10N.getName(new ItemStack(Material.AIR));
    }

    private void enableBungeeCord() {
        if (getChatConfig().isBungeeCord()) {
            Log.i("已开启 BungeeCord 模式!");
            Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            Bukkit.getPluginManager().registerEvents(this, this);
            Bukkit.getMessenger().registerIncomingPluginChannel(this, MiaoMessage.CHANNEL, this);
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, MiaoMessage.CHANNEL);
            Bukkit.getMessenger().registerIncomingPluginChannel(this, MiaoMessage.NORMALCHANNEL, this);
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, MiaoMessage.NORMALCHANNEL);
        }
    }

    private void hookPlaceholderAPI() {
        PlaceholderAPI.registerPlaceholderHook("mct", new PlaceholderHook() {
            @Override
            public String onPlaceholderRequest(Player player, String s) {
                switch (s.toLowerCase()) {
                case "server":
                    return getChatConfig().getServername();
                case "bserver":
                    return ServerName;
                }
                return "未知的参数";
            }
        });
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

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(this, () -> {
            Player p = e.getPlayer();
            if (p.isOnline()) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("GetServer");
                p.sendPluginMessage(MiaoChat.this, "BungeeCord", out.toByteArray());
            }
        }, 10);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (MiaoMessage.CHANNEL.equals(channel)) {
            send(message);
        } else if (MiaoMessage.NORMALCHANNEL.equals(channel)) {
            for (Player p : C.Player.getOnlinePlayers()) {
                p.sendMessage(MiaoMessage.decode(message).getJson());
            }
        } else if ("BungeeCord".equals(channel)) {
            final ByteArrayDataInput input = ByteStreams.newDataInput(message);
            if ("GetServer".equals(input.readUTF())) {
                ServerName = input.readUTF();
                Log.d("获取服务器名称: " + ServerName);
                PlayerJoinEvent.getHandlerList().unregister((Listener) this);
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    Bukkit.getMessenger().unregisterIncomingPluginChannel(MiaoChat.this, "BungeeCord");
                    Bukkit.getMessenger().unregisterOutgoingPluginChannel(MiaoChat.this, "BungeeCord");
                }, 20);
            }
        }
    }
}
