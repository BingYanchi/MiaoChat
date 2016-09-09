package pw.yumc.MiaoChat.config;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import cn.citycraft.PluginHelper.config.FileConfig;
import pw.yumc.YumCore.bukkit.P;

/**
 *
 * @since 2016年9月9日 下午4:40:50
 * @author 喵♂呜
 */
public class Config {
    FileConfig config = P.getConfig();
    Map<String, ChatConfig> formats = new HashMap<>();

    public Config() {
        reload();
    }

    /**
     * 获得玩家可用的消息处理
     *
     * @param player
     * @return {@link ChatConfig}
     */
    public ChatConfig getChatConfig(final Player player) {
        return null;
    }

    public void reload() {

    }
}
