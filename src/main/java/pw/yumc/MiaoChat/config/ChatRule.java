package pw.yumc.MiaoChat.config;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import pw.yumc.YumCore.config.annotation.Default;
import pw.yumc.YumCore.config.inject.InjectConfigurationSection;

/**
 * 聊天规则
 *
 * @since 2016年9月9日 下午4:59:47
 * @author 喵♂呜
 */
public class ChatRule extends InjectConfigurationSection {
    private transient static Pattern FORMAT_PATTERN = Pattern.compile("[\\[]([^\\[\\]]+)[\\]]");
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
    private transient String lastColor;

    public ChatRule(String name, ConfigurationSection config) {
        super(config);
        this.name = name;
        if (permission == null) {
            permission = String.format("MiaoChat.%s", name);
        }
        formats = new LinkedList<>();
        load();
        lastColor = ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', formats.getLast()));
    }

    public boolean check(Player player) {
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

    public String getLastColor() {
        return lastColor;
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
        Matcher m = FORMAT_PATTERN.matcher(format);
        LinkedList<String> temp = new LinkedList<>();
        while (m.find()) {
            temp.add(m.group(1));
        }
        String tempvar = format;
        if (!temp.isEmpty()) {
            for (String var : temp) {
                String[] args = tempvar.split("\\[" + var + "\\]", 2);
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
