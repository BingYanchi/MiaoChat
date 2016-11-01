package pw.yumc.MiaoChat;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;

public class MiaoChatBungee extends Plugin implements Listener {
    @EventHandler
    public void handle(final PluginMessageEvent event) {
        if (event.getTag().equals(MiaoMessage.CHANNEL)) {
            InetSocketAddress origin = event.getSender().getAddress();
            for (ServerInfo server : getProxy().getServers().values()) {
                if (!server.getAddress().equals(origin) && server.getPlayers().size() > 0) {
                    server.sendData(MiaoMessage.CHANNEL, event.getData());
                }
            }
        }
    }

    @Override
    public void onEnable() {
        getProxy().registerChannel(MiaoMessage.CHANNEL);
        getProxy().getPluginManager().registerListener(this, this);
    }
}
