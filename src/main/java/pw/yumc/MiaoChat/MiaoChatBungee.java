package pw.yumc.MiaoChat;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import pw.yumc.MiaoChat.bungee.FileConfig;
import pw.yumc.MiaoChat.bungee.Log;

public class MiaoChatBungee extends Plugin implements Listener {
    private Map<InetSocketAddress, Set<ServerInfo>> groups;
    private FileConfig config;

    @EventHandler
    public void handle(final PluginMessageEvent event) {
        if (event.getTag().equals(MiaoMessage.CHANNEL) || event.getTag().equals(MiaoMessage.NORMALCHANNEL)) {
            InetSocketAddress origin = event.getSender().getAddress();
            groups.get(origin).forEach(server -> {
                if (!server.getAddress().equals(origin) && server.getPlayers().size() > 0) {
                    server.sendData(event.getTag(), event.getData());
                }
            });
        }
    }

    @Override
    public void onLoad() {
        Log.init(this);
        config = new FileConfig(this, "group.yml");
    }

    public void loadGroup() {
        groups = new HashMap<>();
        Map<String, ServerInfo> temp = getProxy().getServers();
        Set<ServerInfo> unused = new HashSet<>();
        Configuration groupSel = config.getSection("Groups");
        Collection<String> groupname = groupSel.getKeys();
        groupname.forEach(gname -> {
            Set<String> servers = new HashSet<>(groupSel.getStringList(gname));
            Set<ServerInfo> sers = new HashSet<>();
            servers.forEach(sname -> sers.add(temp.get(sname)));
            sers.remove(null);
            servers.forEach(sname -> {
                ServerInfo isadd = temp.get(sname);
                if (isadd != null) {
                    unused.remove(isadd);
                    groups.put(isadd.getAddress(), sers);
                }
            });
        });
        unused.forEach(unser -> groups.put(unser.getAddress(), unused));
    }

    @Override
    public void onEnable() {
        loadGroup();
        getProxy().registerChannel(MiaoMessage.CHANNEL);
        getProxy().registerChannel(MiaoMessage.NORMALCHANNEL);
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerCommand(this, new Command("MiaoChat", "MiaoChat.admin", "mct") {
            @Override
            public void execute(CommandSender commandSender, String[] args) {
                if (args.length > 0) {
                    switch (args[0].toLowerCase()) {
                    case "reload":
                        config.reload();
                        loadGroup();
                        commandSender.sendMessage("§a配置文件已重载!");
                        return;
                    case "version":
                    default:
                    }
                }
                commandSender.sendMessage("§6插件版本: §av" + getDescription().getVersion());
            }
        });
        getLogger().info("注意: 通过BC转发的聊天信息将不会在控制台显示 仅客户端可见!");
    }
}
