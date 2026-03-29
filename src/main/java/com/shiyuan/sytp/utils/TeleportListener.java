package com.shiyuan.sytp.utils;

import com.shiyuan.sytp.SyTP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class TeleportListener implements Listener {

    private final SyTP plugin;

    public TeleportListener(SyTP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 玩家退出时取消正在进行的传送
        plugin.getTeleportManager().onPlayerQuit(event.getPlayer());
        
        // 清除玩家冷却数据
        plugin.getCooldownManager().clearCooldown(event.getPlayer());
    }
}
