package pw.yumc.MiaoChat.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import pw.yumc.MiaoChat.MiaoChat;
import pw.yumc.MiaoChat.config.ChatConfig;
import pw.yumc.MiaoChat.config.ChatMessagePart;
import pw.yumc.YumCore.bukkit.P;
import pw.yumc.YumCore.statistic.Statistics;
import pw.yumc.YumCore.tellraw.Tellraw;
import pw.yumc.YumCore.update.SubscribeTask;

public class ChatListener implements Listener {
    MiaoChat plugin = P.getPlugin();

    public ChatListener() {
        Bukkit.getPluginManager().registerEvents(this, P.instance);
        new Statistics();
        new SubscribeTask(true, true);
    }

    @EventHandler
    public void onChat(final AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        final Player p = e.getPlayer();
        final ChatConfig cc = plugin.getChatConfig();
        final String msg = e.getMessage();
        final Tellraw tr = Tellraw.create();
        for (final ChatMessagePart cmp : cc.getPrefixs()) {
            cmp.then(tr, p);
        }
        cc.getPlayer().then(tr, p);
        for (final ChatMessagePart cmp : cc.getSuffixs()) {
            cmp.then(tr, p);
        }
        tr.then(ChatColor.translateAlternateColorCodes('&', msg));
        tr.broadcast();
    }
}
