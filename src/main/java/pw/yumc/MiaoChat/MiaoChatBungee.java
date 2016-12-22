package pw.yumc.MiaoChat;

import java.net.InetSocketAddress;
import java.util.*;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import pw.yumc.MiaoChat.bungee.FileConfig;

public class MiaoChatBungee extends Plugin implements Listener {
    private Map<InetSocketAddress, Set<InetSocketAddress>> groups;
    private FileConfig config;

    @EventHandler
    public void handle(final PluginMessageEvent event) {
        if (event.getTag().equals(MiaoMessage.CHANNEL) || event.getTag().equals(MiaoMessage.NORMALCHANNEL)) {
            InetSocketAddress origin = event.getSender().getAddress();
            for (ServerInfo server : getProxy().getServers().values()) {
                if (!server.getAddress().equals(origin) && server.getPlayers().size() > 0) {
                    server.sendData(event.getTag(), event.getData());
                }
            }
        }
    }

    @Override
    public void onLoad() {
        config = new FileConfig(this, "group.yml");
    }

    public void loadGroup() {
        Map<String, InetSocketAddress> temp = new HashMap<>();
        Set<InetSocketAddress> unused = new HashSet<>();
        for (ServerInfo server : getProxy().getServers().values()) {
            temp.put(server.getName(), server.getAddress());
        }
        Configuration groupSel = config.getSection("Groups");
        Collection<String> groupname = groupSel.getKeys();
        for (String gname : groupname) {
            Set<String> servers = new HashSet<>(groupSel.getStringList(gname));
            Set<InetSocketAddress> serISA = new HashSet<>();
            for (String sname : servers) {
                serISA.add(temp.get(sname));
            }
            serISA.remove(null);
            for (String sname : servers) {
                InetSocketAddress isadd = temp.get(sname);
                if (isadd != null) {
                    unused.remove(isadd);
                    groups.put(isadd, serISA);
                }
            }
        }
        for (InetSocketAddress unser : unused) {
            groups.put(unser, unused);
        }
    }

    @Override
    public void onEnable() {
        getProxy().registerChannel(MiaoMessage.CHANNEL);
        getProxy().registerChannel(MiaoMessage.NORMALCHANNEL);
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerCommand(this, new Command("MiaoChat", "MiaoChat.admin", "mct") {
            @Override
            public void execute(CommandSender commandSender, String[] args) {
                if (args.length > 1) {
                    switch (args[0].toLowerCase()) {
                    case "reload":
                        onLoad();
                        break;
                    case "version":
                        commandSender.sendMessage(getDescription().getVersion());
                        break;
                    }
                }
            }
        });
        getLogger().info("注意: 通过BC转发的聊天信息将不会在控制台显示 仅客户端可见!");
    }
}
