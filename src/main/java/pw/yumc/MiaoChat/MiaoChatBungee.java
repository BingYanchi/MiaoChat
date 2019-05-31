package pw.yumc.MiaoChat;

import java.net.InetSocketAddress;
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

/**
 * @author MiaoWoo
 */
public class MiaoChatBungee extends Plugin implements Listener {
    private Map<InetSocketAddress, Set<ServerInfo>> groups;
    private FileConfig config;

    @EventHandler
    public void handle(final PluginMessageEvent event) {
        if (event.getTag().equals(MiaoMessage.CHANNEL) || event.getTag().equals(MiaoMessage.NORMAL_CHANNEL)) {
            InetSocketAddress origin = event.getSender().getAddress();
            if (groups.containsKey(origin)) {
                groups.get(origin).forEach(server -> {
                    if (!server.getAddress().equals(origin) && server.getPlayers().size() > 0) {
                        server.sendData(event.getTag(), event.getData());
                    }
                });
            }
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
        Set<ServerInfo> unused = new HashSet<>(temp.values());
        Configuration groupSel = config.getSection("Groups");
        groupSel.getKeys().forEach(groupName -> {
            Set<String> servers = new HashSet<>(groupSel.getStringList(groupName));
            Set<ServerInfo> serverInfos = new HashSet<>();
            servers.forEach(s -> {
                if (temp.containsKey(s)) {
                    ServerInfo serverInfo = temp.get(s);
                    unused.remove(serverInfo);
                    groups.put(serverInfo.getAddress(), serverInfos);
                }
            });
        });
        unused.forEach(serverInfo -> groups.put(serverInfo.getAddress(), unused));
    }

    @Override
    public void onEnable() {
        loadGroup();
        getProxy().registerChannel(MiaoMessage.CHANNEL);
        getProxy().registerChannel(MiaoMessage.NORMAL_CHANNEL);
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
