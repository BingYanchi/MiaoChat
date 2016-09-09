package pw.yumc.MiaoChat.config;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;

import pw.yumc.YumCore.bukkit.Log;
import pw.yumc.YumCore.bukkit.P;
import pw.yumc.YumCore.config.FileConfig;

/**
 *
 * @since 2016年9月9日 下午4:40:50
 * @author 喵♂呜
 */
public class Config {
    private static final String F = "Formats";
    private final RuleComparator rulecomp;
    private final List<ChatRule> rules;
    private final FileConfig config;

    public Config() {
        config = P.getConfig();
        rulecomp = new RuleComparator();
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

    public void reload() {
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
