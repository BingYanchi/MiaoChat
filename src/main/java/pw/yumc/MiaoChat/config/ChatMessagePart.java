package pw.yumc.MiaoChat.config;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import pw.yumc.YumCore.config.ConfigNode;
import pw.yumc.YumCore.config.InjectConfigurationSection;
import pw.yumc.YumCore.tellraw.Tellraw;

public class ChatMessagePart extends InjectConfigurationSection {
    private String text;
    private List<String> tip;
    @ConfigNode("click.type")
    private String typestring;
    private transient CLICKTYPE type = CLICKTYPE.SUGGEST;
    @ConfigNode("click.command")
    private String command;

    public ChatMessagePart(final ConfigurationSection config) {
        super(config);
        if (typestring != null) {
            type = CLICKTYPE.valueOf(typestring);
        }
    }

    public Tellraw then(final Tellraw tr, final Player p) {
        tr.then(f(p, text));
        if (tip != null && !tip.isEmpty()) {
            tr.tip(f(p, tip));
        }
        if (command != null && !command.isEmpty()) {
            final String tc = f(p, command);
            if (type == CLICKTYPE.SUGGEST) {
                tr.suggest(tc);
            } else if (type == CLICKTYPE.COMMAND) {
                tr.command(tc);
            }
        }
        return tr;
    }

    private List<String> f(final Player player, final List<String> text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    private String f(final Player player, final String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
