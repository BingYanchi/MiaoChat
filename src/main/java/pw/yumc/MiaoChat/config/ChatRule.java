package pw.yumc.MiaoChat.config;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import pw.yumc.YumCore.config.Default;
import pw.yumc.YumCore.config.InjectConfigurationSection;

/**
 * 聊天规则
 *
 * @since 2016年9月9日 下午4:59:47
 * @author 喵♂呜
 */
public class ChatRule extends InjectConfigurationSection {
    private transient static final Pattern FORMAT_PATTERN = Pattern.compile("[\\[]([^\\[\\]]+)[\\]]");
    private transient String name;
    @Default("50")
    private Integer index;
    @Default("MiaoChat.default")
    private String permission;
    @Default("[world][player]: ")
    private String format;
    @Default("0")
    private Integer range;
    @Default("false")
    private Boolean item;
    @Default("&6[&b%s&6]&r")
    private String itemformat;
    private transient LinkedList<String> formats;

    public ChatRule(final String name, final ConfigurationSection config) {
        super(config);
        this.name = name;
        if (permission == null) {
            permission = String.format("MiaoChat.%s", name);
        }
        formats = new LinkedList<>();
        load();
    }

    public boolean check(final Player player) {
        return player.hasPermission(permission);
    }

    public String getFormat() {
        return format;
    }

    public LinkedList<String> getFormats() {
        return formats;
    }

    public Integer getIndex() {
        return index;
    }

    public Boolean getItem() {
        return item;
    }

    public String getItemformat() {
        return itemformat;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public Integer getRange() {
        return range;
    }

    public boolean isItem() {
        return item;
    }

    private void load() {
        final Matcher m = FORMAT_PATTERN.matcher(format);
        final LinkedList<String> temp = new LinkedList<>();
        while (m.find()) {
            temp.add(m.group(1));
        }
        String tempvar = format;
        if (!temp.isEmpty()) {
            for (final String var : temp) {
                final String[] args = tempvar.split("\\[" + var + "\\]", 2);
                if (!"".equals(args[0])) {
                    formats.add(args[0]);
                }
                formats.add(var);
                tempvar = args[1];
            }
            if (!tempvar.isEmpty()) {
                formats.add(tempvar);
            }
        }
    }
}
