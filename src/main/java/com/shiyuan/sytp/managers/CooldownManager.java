package com.shiyuan.sytp.managers;

import com.shiyuan.sytp.SyTP;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

    private final SyTP plugin;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    public CooldownManager(SyTP plugin) {
        this.plugin = plugin;
    }

    /**
     * 设置玩家冷却
     */
    public void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    /**
     * 检查玩家是否在冷却中
     */
    public boolean isOnCooldown(Player player) {
        Long cooldownStart = cooldowns.get(player.getUniqueId());
        if (cooldownStart == null) {
            return false;
        }

        int cooldownSeconds = plugin.getConfigManager().getTeleportCooldownSeconds();
        long elapsed = (System.currentTimeMillis() - cooldownStart) / 1000;
        
        return elapsed < cooldownSeconds;
    }

    /**
     * 获取剩余冷却时间（秒）
     */
    public int getRemainingCooldown(Player player) {
        Long cooldownStart = cooldowns.get(player.getUniqueId());
        if (cooldownStart == null) {
            return 0;
        }

        int cooldownSeconds = plugin.getConfigManager().getTeleportCooldownSeconds();
        long elapsed = (System.currentTimeMillis() - cooldownStart) / 1000;
        int remaining = cooldownSeconds - (int) elapsed;
        
        return Math.max(0, remaining);
    }

    /**
     * 清除玩家冷却
     */
    public void clearCooldown(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
}
