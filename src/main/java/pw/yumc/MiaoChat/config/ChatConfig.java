package pw.yumc.MiaoChat.config;

import java.util.LinkedList;
import java.util.List;

import pw.yumc.YumCore.bukkit.P;
import pw.yumc.YumCore.config.FileConfig;

public class ChatConfig {
    private static String PrefixKey = "Format.Prefix";
    private static String SuffixKey = "Format.Suffix";
    private final FileConfig config;
    private final List<ChatMessagePart> prefixs;
    private final List<ChatMessagePart> suffixs;
    private ChatMessagePart player;

    public ChatConfig() {
        config = P.getConfig();
        prefixs = new LinkedList<>();
        suffixs = new LinkedList<>();
        reload();
    }

    public ChatMessagePart getPlayer() {
        return player;
    }

    public List<ChatMessagePart> getPrefixs() {
        return prefixs;
    }

    public List<ChatMessagePart> getSuffixs() {
        return suffixs;
    }

    public void reload() {
        prefixs.clear();
        if (config.isSet(PrefixKey)) {
            for (final String part : config.getConfigurationSection(PrefixKey).getKeys(false)) {
                prefixs.add(new ChatMessagePart(config.getConfigurationSection(PrefixKey + "." + part)));
            }
        }
        player = new ChatMessagePart(config.getConfigurationSection("Format.Player"));
        suffixs.clear();
        if (config.isSet(SuffixKey)) {
            for (final String part : config.getConfigurationSection(SuffixKey).getKeys(false)) {
                suffixs.add(new ChatMessagePart(config.getConfigurationSection(SuffixKey + "." + part)));
            }
        }
    }
}
