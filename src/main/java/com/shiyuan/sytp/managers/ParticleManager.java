package com.shiyuan.sytp.managers;

import com.shiyuan.sytp.SyTP;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleManager {

    private final SyTP plugin;

    public ParticleManager(SyTP plugin) {
        this.plugin = plugin;
    }

    /**
     * 播放传送粒子效果
     */
    public void playTeleportEffect(Location location) {
        if (!plugin.getConfigManager().isEnableParticleEffect()) {
            return;
        }

        Particle particle = plugin.getConfigManager().getParticleType();
        int count = plugin.getConfigManager().getParticleCount();

        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 20) { // 持续1秒
                    cancel();
                    return;
                }

                // 在玩家位置周围生成粒子
                for (int i = 0; i < count / 20; i++) {
                    double offsetX = (Math.random() - 0.5) * 2;
                    double offsetY = Math.random() * 2;
                    double offsetZ = (Math.random() - 0.5) * 2;
                    
                    Location particleLoc = location.clone().add(offsetX, offsetY, offsetZ);
                    location.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    /**
     * 在指定位置播放粒子效果
     */
    public void playEffectAtLocation(Location location, Particle particle, int count) {
        location.getWorld().spawnParticle(particle, location, count, 0.5, 0.5, 0.5, 0.1);
    }
}
