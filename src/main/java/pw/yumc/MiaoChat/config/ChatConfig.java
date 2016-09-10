package pw.yumc.MiaoChat.config;

import java.util.HashMap;
import java.util.Map;

import pw.yumc.YumCore.config.FileConfig;

public class ChatConfig {
    private final FileConfig config;
    private final Map<String, ChatMessagePart> formats;

    public ChatConfig(final FileConfig cfg) {
        config = cfg;
        formats = new HashMap<>();
        reload();
    }

    public ChatMessagePart getFormat(final String name) {
        return formats.get(name);
    }

    public void reload() {
        formats.clear();
        for (final String name : config.getKeys(false)) {
            formats.put(name, new ChatMessagePart(config.getConfigurationSection(name)));
        }
    }
}
