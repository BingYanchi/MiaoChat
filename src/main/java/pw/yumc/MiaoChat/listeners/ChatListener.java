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
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import pw.yumc.MiaoChat.MiaoChat;
import pw.yumc.MiaoChat.config.ChatConfig;
import pw.yumc.MiaoChat.config.ChatMessagePart;
import pw.yumc.MiaoChat.config.ChatRule;
import pw.yumc.YumCore.bukkit.Log;
import pw.yumc.YumCore.bukkit.P;
import pw.yumc.YumCore.bukkit.compatible.C;
import pw.yumc.YumCore.statistic.Statistics;
import pw.yumc.YumCore.tellraw.Tellraw;
import pw.yumc.YumCore.update.SubscribeTask;

public class ChatListener implements Listener {
    public static Set<String> offList = new HashSet<>();
    static final Pattern ITEM_PATTERN = Pattern.compile("%([i1-9]?)");

    MiaoChat plugin = P.getPlugin();

    public ChatListener() {
        Bukkit.getPluginManager().registerEvents(this, P.instance);
        new Statistics();
        new SubscribeTask(true, true);
    }

    @EventHandler
    public void onChat(final AsyncPlayerChatEvent e) {
        final Player p = e.getPlayer();
        final ChatRule cr = plugin.getConfigExt().getChatRule(e.getPlayer());
        if (cr == null) {
            return;
        }
        final ChatConfig cc = cr.getFormats();
        e.setCancelled(true);
        final String msg = e.getMessage();
        final Tellraw tr = Tellraw.create();
        for (final ChatMessagePart cmp : cc.getPrefixs()) {
            cmp.then(tr, p);
        }
        cc.getPlayer().then(tr, p);
        for (final ChatMessagePart cmp : cc.getSuffixs()) {
            cmp.then(tr, p);
        }
        final String message = ChatColor.translateAlternateColorCodes('&', msg);
        if (!message.isEmpty() && cr.isItem()) {
            if (!handlerTellraw(p, tr, message, cr.getItemformat())) {
                Log.toSender(p, "§c不允许展示相同的物品!");
                return;
            }
        }
        final int range = cr.getRange();
        Collection<? extends Entity> plist = Collections.emptyList();
        if (range != 0) {
            plist = p.getNearbyEntities(range, range, range);
        } else {
            plist = C.Player.getOnlinePlayers();
        }
        for (final Entity ne : plist) {
            if (ne instanceof Player && !offList.contains(ne.getName())) {
                tr.send(ne);
            }
        }
    }

    private LinkedList<String> handlerMessage(final LinkedList<String> il, String message) {
        final LinkedList<String> mlist = new LinkedList<>();
        if (!il.isEmpty()) {
            for (final String k : il) {
                final String[] args = message.split(k, 2);
                mlist.add(args[0]);
                mlist.add(k);
                message = args[1];
            }
        }
        if (!message.isEmpty()) {
            mlist.add(message);
        }
        return mlist;
    }

    private LinkedList<String> handlerPattern(final String message) {
        final Matcher m = ITEM_PATTERN.matcher(message);
        final Set<String> temp = new HashSet<>();
        final LinkedList<String> ilist = new LinkedList<>();
        while (m.find()) {
            final String key = m.group(0);
            if (key.length() == 2) {
                if (temp.add(key)) {
                    ilist.add(key);
                } else {
                    return null;
                }
            }
        }
        return ilist;
    }

    private boolean handlerTellraw(final Player player, final Tellraw tr, final String message, final String itemformat) {
        final LinkedList<String> il = handlerPattern(message);
        if (il == null) {
            return false;
        }
        final LinkedList<String> ml = handlerMessage(il, message);
        while (!ml.isEmpty()) {
            final String mm = ml.removeFirst();
            if (il.contains(mm)) {
                ItemStack is = null;
                final char k = mm.charAt(1);
                if (k == 'i') {
                    is = player.getItemInHand();
                } else {
                    is = player.getInventory().getItem(k - '0' - 1);
                }
                if (is != null && is.getType() != Material.AIR) {
                    tr.then(String.format(ChatColor.translateAlternateColorCodes('&', itemformat),
                            is.hasItemMeta() && is.getItemMeta().hasDisplayName() ? is.getItemMeta().getDisplayName() : is.getType().name()));
                    tr.item(is);
                }
            } else {
                tr.then(mm);
            }
        }
        return true;
    }
}
