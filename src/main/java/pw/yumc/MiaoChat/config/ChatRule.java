package pw.yumc.MiaoChat.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import pw.yumc.YumCore.config.Default;
import pw.yumc.YumCore.config.FileConfig;
import pw.yumc.YumCore.config.InjectConfigurationSection;

/**
 * 聊天规则
 *
 * @since 2016年9月9日 下午4:59:47
 * @author 喵♂呜
 */
public class ChatRule extends InjectConfigurationSection {
    private transient String name;
    @Default("50")
    private Integer index;
    private String permission;
    @Default("0")
    private Integer range;
    @Default("false")
    private Boolean item;
    @Default("&6[%s&6]&r")
    private String itemformat;
    private transient ChatConfig formats;

    public ChatRule(final String name, final ConfigurationSection config) {
        super(config);
        this.name = name;
        if (permission == null) {
            permission = String.format("MiaoChat.%s", name);
        }
        formats = new ChatConfig(new FileConfig(name + ".yml"));
    }

    public boolean check(final Player player) {
        return player.hasPermission(permission);
    }

    public ChatConfig getFormats() {
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
}
