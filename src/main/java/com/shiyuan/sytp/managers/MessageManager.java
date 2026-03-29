package com.shiyuan.sytp.managers;

import com.shiyuan.sytp.SyTP;
import com.shiyuan.sytp.requests.RequestType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MessageManager {

    private final SyTP plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private Map<String, String> messages = new HashMap<>();

    public MessageManager(SyTP plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    public void reload() {
        loadMessages();
    }

    private void loadMessages() {
        FileConfiguration config = plugin.getConfig();
        
        messages.clear();
        
        // 加载所有消息
        if (config.contains("messages")) {
            for (String key : config.getConfigurationSection("messages").getKeys(false)) {
                messages.put(key, config.getString("messages." + key));
            }
        }
    }

    /**
     * 发送消息给玩家
     */
    public void sendMessage(Player player, String key, Object... placeholders) {
        String message = messages.get(key);
        if (message == null) {
            // 如果是直接消息（以&开头）
            if (key.startsWith("&") || key.startsWith("§")) {
                message = key;
            } else {
                player.sendMessage(colorize("&c[缺失消息: " + key + "]"));
                return;
            }
        }

        // 替换占位符
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                String placeholder = "{" + placeholders[i] + "}";
                String value = String.valueOf(placeholders[i + 1]);
                message = message.replace(placeholder, value);
            }
        }

        player.sendMessage(colorize(getPrefix() + message));
    }

    /**
     * 发送原始消息
     */
    public void sendRawMessage(Player player, String message) {
        player.sendMessage(colorize(getPrefix() + message));
    }

    /**
     * 发送带点击事件的请求消息
     */
    public void sendRequestMessage(Player target, Player requester, RequestType type) {
        String messageKey = type == RequestType.TPA ? "tpa-received" : "tpc-received";
        String message = messages.get(messageKey);
        
        if (message == null) return;
        
        message = message.replace("{player}", requester.getName());
        
        // 创建可点击的消息
        Component mainMessage = miniMessage.deserialize(colorizeToMiniMessage(getPrefix() + message));
        
        // 创建同意按钮
        String acceptText = messages.getOrDefault("click-accept", "&a&l[点击同意]");
        Component acceptButton = miniMessage.deserialize(colorizeToMiniMessage(acceptText))
            .clickEvent(ClickEvent.runCommand("/tpaccept"))
            .hoverEvent(HoverEvent.showText(miniMessage.deserialize("<green>点击同意传送请求")));
        
        // 创建拒绝按钮
        String denyText = messages.getOrDefault("click-deny", "&c&l[点击拒绝]");
        Component denyButton = miniMessage.deserialize(colorizeToMiniMessage(denyText))
            .clickEvent(ClickEvent.runCommand("/tpdeny"))
            .hoverEvent(HoverEvent.showText(miniMessage.deserialize("<red>点击拒绝传送请求")));
        
        // 组合消息
        Component finalMessage = mainMessage.append(Component.text(" ")).append(acceptButton).append(Component.text(" ")).append(denyButton);
        
        target.sendMessage(finalMessage);
    }

    /**
     * 发送TPW请求消息
     */
    public void sendTpwRequestMessage(Player player, Player requester) {
        String message = messages.get("tpw-received");
        
        if (message == null) return;
        
        message = message.replace("{player}", requester.getName());
        
        Component mainMessage = miniMessage.deserialize(colorizeToMiniMessage(getPrefix() + message));
        
        String acceptText = messages.getOrDefault("click-accept", "&a&l[点击同意]");
        Component acceptButton = miniMessage.deserialize(colorizeToMiniMessage(acceptText))
            .clickEvent(ClickEvent.runCommand("/tpaccept"))
            .hoverEvent(HoverEvent.showText(miniMessage.deserialize("<green>点击同意传送")));
        
        String denyText = messages.getOrDefault("click-deny", "&c&l[点击拒绝]");
        Component denyButton = miniMessage.deserialize(colorizeToMiniMessage(denyText))
            .clickEvent(ClickEvent.runCommand("/tpdeny"))
            .hoverEvent(HoverEvent.showText(miniMessage.deserialize("<red>点击拒绝传送")));
        
        Component finalMessage = mainMessage.append(Component.text(" ")).append(acceptButton).append(Component.text(" ")).append(denyButton);
        
        player.sendMessage(finalMessage);
    }

    /**
     * 颜色代码转换 (& -> §)
     */
    public String colorize(String message) {
        if (message == null) return "";
        return message.replace('&', '§');
    }

    /**
     * 转换为MiniMessage格式
     */
    private String colorizeToMiniMessage(String message) {
        if (message == null) return "";
        
        // 将 & 颜色代码转换为 MiniMessage 格式
        message = message.replace("§0", "<black>")
                        .replace("§1", "<dark_blue>")
                        .replace("§2", "<dark_green>")
                        .replace("§3", "<dark_aqua>")
                        .replace("§4", "<dark_red>")
                        .replace("§5", "<dark_purple>")
                        .replace("§6", "<gold>")
                        .replace("§7", "<gray>")
                        .replace("§8", "<dark_gray>")
                        .replace("§9", "<blue>")
                        .replace("§a", "<green>")
                        .replace("§b", "<aqua>")
                        .replace("§c", "<red>")
                        .replace("§d", "<light_purple>")
                        .replace("§e", "<yellow>")
                        .replace("§f", "<white>")
                        .replace("§l", "<bold>")
                        .replace("§m", "<strikethrough>")
                        .replace("§n", "<underline>")
                        .replace("§o", "<italic>")
                        .replace("§r", "<reset>");
        
        // 处理 & 开头的颜色代码
        message = message.replace("&0", "<black>")
                        .replace("&1", "<dark_blue>")
                        .replace("&2", "<dark_green>")
                        .replace("&3", "<dark_aqua>")
                        .replace("&4", "<dark_red>")
                        .replace("&5", "<dark_purple>")
                        .replace("&6", "<gold>")
                        .replace("&7", "<gray>")
                        .replace("&8", "<dark_gray>")
                        .replace("&9", "<blue>")
                        .replace("&a", "<green>")
                        .replace("&b", "<aqua>")
                        .replace("&c", "<red>")
                        .replace("&d", "<light_purple>")
                        .replace("&e", "<yellow>")
                        .replace("&f", "<white>")
                        .replace("&l", "<bold>")
                        .replace("&m", "<strikethrough>")
                        .replace("&n", "<underline>")
                        .replace("&o", "<italic>")
                        .replace("&r", "<reset>");
        
        return message;
    }

    /**
     * 获取前缀
     */
    private String getPrefix() {
        return messages.getOrDefault("prefix", "&8[&bSyTP&8] &r");
    }
}
