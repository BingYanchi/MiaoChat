package pw.yumc.MiaoChat.listeners;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import me.clip.placeholderapi.PlaceholderAPI;
import pw.yumc.MiaoChat.MiaoChat;
import pw.yumc.MiaoChat.config.ChatConfig;
import pw.yumc.MiaoChat.config.ChatMessagePart;
import pw.yumc.MiaoChat.config.ChatRule;
import pw.yumc.YumCore.bukkit.Log;
import pw.yumc.YumCore.bukkit.P;
import pw.yumc.YumCore.bukkit.compatible.C;
import pw.yumc.YumCore.misc.L10N;
import pw.yumc.YumCore.statistic.Statistics;
import pw.yumc.YumCore.tellraw.Tellraw;
import pw.yumc.YumCore.update.SubscribeTask;

public class ChatListener implements Listener {
    public static Set<String> offList = new HashSet<>();
    private static final Pattern ITEM_PATTERN = Pattern.compile("%([i1-9]?)");

    private final MiaoChat plugin = P.getPlugin();
    private final ChatConfig cc = plugin.getChatConfig();

    public ChatListener() {
        Bukkit.getPluginManager().registerEvents(this, P.instance);
        new Statistics();
        new SubscribeTask(true, true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent e) {
        final Player p = e.getPlayer();
        final ChatRule cr = cc.getChatRule(e.getPlayer());
        if (cr == null) {
            // Log.d("玩家: %s 未发现可用ChatRule!", p.getName());
            return;
        }
        e.setCancelled(true);
        final Tellraw tr = Tellraw.create();
        handleChat(p, tr, cr, e.getMessage());
    }

    private void handleChat(final Player p, final Tellraw tr, final ChatRule cr, final String message) {
        // Log.d("玩家: %s 使用 %s 规则 解析 %s", p.getName(), cr.getName(), message);
        handleFormat(p, tr, cr);
        handleTellraw(p, tr, cr, message);
        handleSend(p, tr, cr.getRange());
    }

    private void handleFormat(final Player p, final Tellraw tr, final ChatRule cr) {
        final LinkedList<String> formats = cr.getFormats();
        // Log.d("处理前缀信息...");
        for (final String format : formats) {
            final ChatMessagePart cmp = cc.getFormat(format);
            if (cmp != null) {
                // Log.d("解析格式: %s", format);
                cmp.then(tr, p);
            } else {
                // Log.d("追加文本: %s", format);
                tr.then(PlaceholderAPI.setPlaceholders(p, format));
            }
        }
    }

    private LinkedList<String> handleMessage(final LinkedList<String> il, String message) {
        final LinkedList<String> mlist = new LinkedList<>();
        // Log.d("处理聊天信息...");
        if (!il.isEmpty()) {
            for (final String k : il) {
                final String[] args = message.split(k, 2);
                if (!args[0].isEmpty()) {
                    // Log.d("追加文本: %s", args[0]);
                    mlist.add(args[0]);
                }
                // Log.d("解析物品: %s", args[0]);
                mlist.add(k);
                message = args[1];
            }
        }
        if (!message.isEmpty()) {
            // Log.d("追加文本: %s", message);
            mlist.add(message);
        }
        return mlist;
    }

    private LinkedList<String> handlePattern(final String message) {
        final Matcher m = ITEM_PATTERN.matcher(message);
        final Set<String> temp = new HashSet<>();
        final LinkedList<String> ilist = new LinkedList<>();
        // Log.d("处理聊天物品信息...");
        while (m.find()) {
            final String key = m.group(0);
            if (key.length() == 2) {
                if (temp.add(key)) {
                    // Log.d("解析物品关键词: %s", key);
                    ilist.add(key);
                } else {
                    return null;
                }
            }
        }
        return ilist;
    }

    private void handleSend(final Player p, final Tellraw tr, final int range) {
        Collection<? extends Entity> plist = Collections.emptyList();
        if (range != 0) {
            plist = p.getNearbyEntities(range, range, range);
            tr.send(p);
        } else {
            plist = C.Player.getOnlinePlayers();
        }
        for (final Entity ne : plist) {
            if (ne instanceof Player && !offList.contains(((Player) ne).getName())) {
                tr.send(ne);
            }
        }
        Bukkit.getConsoleSender().sendMessage(tr.toOldMessageFormat());
    }

    private void handleTellraw(final Player player, final Tellraw tr, final ChatRule cr, String message) {
        if (message.isEmpty()) {
            return;
        }
        if (player.hasPermission("MiaoChat.color")) {
            message = ChatColor.translateAlternateColorCodes('&', message);
        }
        if (!cr.isItem()) {
            tr.then(message);
            return;
        }
        final LinkedList<String> il = handlePattern(message);
        if (il == null) {
            Log.toSender(player, "§c不允许展示相同的物品!");
            return;
        }
        final LinkedList<String> ml = handleMessage(il, message);
        // Log.d("处理Tellraw格式...");
        while (!ml.isEmpty()) {
            final String mm = ml.removeFirst();
            if (il.contains(mm)) {
                final char k = mm.charAt(1);
                final ItemStack is = k == 'i' ? player.getItemInHand() : player.getInventory().getItem(k - '0' - 1);
                if (is != null && is.getType() != Material.AIR) {
                    // Log.d("处理物品: %s", mm);
                    tr.then(String.format(ChatColor.translateAlternateColorCodes('&', cr.getItemformat()), L10N.getItemName(is)));
                    tr.item(is);
                }
            } else {
                // Log.d("追加聊天: %s", mm);
                tr.then(mm);
            }
        }
    }
}
