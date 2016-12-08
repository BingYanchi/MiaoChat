package pw.yumc.MiaoChat.config;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import pw.yumc.YumCore.config.annotation.ConfigNode;
import pw.yumc.YumCore.config.annotation.Nullable;
import pw.yumc.YumCore.config.inject.InjectConfigurationSection;
import pw.yumc.YumCore.tellraw.Tellraw;

public class ChatMessagePart extends InjectConfigurationSection {
    private String text;
    @Nullable
    private List<String> tip;
    @Nullable
    @ConfigNode("click.type")
    private String typestring;
    @Nullable
    @ConfigNode("click.command")
    private String command;
    private transient CLICKTYPE type = CLICKTYPE.SUGGEST;

    public ChatMessagePart(ConfigurationSection config) {
        super(config);
        if (typestring != null) {
            type = CLICKTYPE.valueOf(typestring);
        }
    }

    public Tellraw then(Tellraw tr, Player p) {
        tr.then(f(p, text));
        if (tip != null && !tip.isEmpty()) {
            tr.tip(f(p, tip));
        }
        if (command != null && !command.isEmpty()) {
            String tc = f(p, command);
            switch (type) {
            case COMMAND:
                return tr.command(tc);
            case OPENURL:
                return tr.openurl(tc);
            case SUGGEST:
                return tr.suggest(tc);
            }
        }
        return tr;
    }

    private List<String> f(Player player, List<String> text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    private String f(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
