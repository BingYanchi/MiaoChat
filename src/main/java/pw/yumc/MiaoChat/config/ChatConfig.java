package pw.yumc.MiaoChat.config;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    private static final String F = "Formats";
    private final Map<String, ChatMessagePart> formats;
    private final RuleComparator rulecomp;
    private final List<ChatRule> rules;
    private final FileConfig config;
    private final FileConfig format;

    public ChatConfig() {
        config = P.getConfig();
        format = new FileConfig("format.yml");
        rulecomp = new RuleComparator();
        formats = new HashMap<>();
        rules = new LinkedList<>();
        reload();
    }

    /**
     * 获得玩家可用的消息处理
     *
     * @param player
     * @return {@link ChatConfig}
     */
    public ChatRule getChatRule(final Player player) {
        for (final ChatRule cr : rules) {
            Log.debug(cr.getName());
            if (cr.check(player)) {
                return cr;
            }
        }
        return null;
    }

    public ChatMessagePart getFormat(final String name) {
        return formats.get(name);
    }

    public void reload() {
        formats.clear();
        for (final String name : format.getKeys(false)) {
            formats.put(name, new ChatMessagePart(format.getConfigurationSection(name)));
        }
        rules.clear();
        if (config.isSet(F)) {
            for (final String rule : config.getConfigurationSection(F).getKeys(false)) {
                rules.add(new ChatRule(rule, config.getConfigurationSection(F + "." + rule)));
            }
        }
        Collections.sort(rules, rulecomp);
    }

    private class RuleComparator implements Comparator<ChatRule> {
        @Override
        public int compare(final ChatRule o1, final ChatRule o2) {
            return o1.getIndex().compareTo(o2.getIndex());
        }
    }
}
