package pw.yumc.MiaoChat.config;

import java.util.*;

import org.bukkit.entity.Player;

import pw.yumc.YumCore.bukkit.Log;
import pw.yumc.YumCore.bukkit.P;
import pw.yumc.YumCore.config.FileConfig;

/**
 *
 * @since 2016年9月9日 下午4:40:50
 * @author 喵♂呜
 */
public class ChatConfig {
    private static String F = "Formats";
    private Map<String, ChatMessagePart> formats;
    private RuleComparator rulecomp;
    private LinkedList<ChatRule> rules;
    private FileConfig config;
    private FileConfig format;
    private String servername;
    private boolean BungeeCord;

    public ChatConfig() {
        config = P.getConfig();
        BungeeCord = config.getBoolean("BungeeCord");
        format = new FileConfig("format.yml");
        rulecomp = new RuleComparator();
        formats = new HashMap<>();
        rules = new LinkedList<>();
        load();
    }

    /**
     * @return 是否为BC模式
     */
    public boolean isBungeeCord() {
        return BungeeCord;
    }

    /**
     * 获得玩家可用的消息处理
     *
     * @param player
     * @return {@link ChatConfig}
     */
    public ChatRule getChatRule(Player player) {
        for (ChatRule cr : rules) {
            Log.debug(cr.getName());
            if (cr.check(player)) { return cr; }
        }
        return null;
    }

    public Map<String, ChatMessagePart> getFormats() {
        return formats;
    }

    public void load() {
        servername = config.getMessage("Server");
        formats.clear();
        for (String name : format.getKeys(false)) {
            formats.put(name, new ChatMessagePart(format.getConfigurationSection(name)));
            Log.d("载入聊天格式: %s", name);
        }
        rules.clear();
        if (config.isSet(F)) {
            for (String rule : config.getConfigurationSection(F).getKeys(false)) {
                rules.add(new ChatRule(rule, config.getConfigurationSection(F + "." + rule)));
                Log.d("载入聊天规则: %s => \"%s\"", rule, rules.getLast().getFormat());
            }
        }
        Collections.sort(rules, rulecomp);
    }

    public String getServername() {
        return servername;
    }

    public void reload() {
        format.reload();
        load();
    }

    private class RuleComparator implements Comparator<ChatRule> {
        @Override
        public int compare(ChatRule o1, ChatRule o2) {
            return o1.getIndex().compareTo(o2.getIndex());
        }
    }
}
