package pw.yumc.MiaoChat.config;

import org.bukkit.configuration.ConfigurationSection;

import pw.yumc.YumCore.config.InjectConfigurationSection;

/**
 * 聊天规则
 *
 * @since 2016年9月9日 下午4:59:47
 * @author 喵♂呜
 */
public class ChatRule extends InjectConfigurationSection {
    private Integer index;
    private String permission;
    private Integer range;
    private boolean item;

    public ChatRule(final ConfigurationSection config) {
        super(config);
    }

    /**
     * @return the index
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * @return the permission
     */
    public String getPermission() {
        return permission;
    }

    /**
     * @return the range
     */
    public Integer getRange() {
        return range;
    }

    /**
     * @return the item
     */
    public boolean isItem() {
        return item;
    }

}
