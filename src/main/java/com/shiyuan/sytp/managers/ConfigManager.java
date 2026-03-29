package com.shiyuan.sytp.managers;

import com.shiyuan.sytp.SyTP;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final SyTP plugin;
    private FileConfiguration config;

    // 功能开关
    private boolean enableTeleportCost;
    private boolean enableTeleportHereCost;
    private boolean enableTpwCost;
    private boolean enableTeleportDelay;
    private boolean enableParticleEffect;

    // 费用设置
    private double teleportCost;
    private double teleportHereCost;
    private double tpwCost;

    // 传送设置
    private int teleportDelaySeconds;
    private int requestTimeoutSeconds;
    private int teleportCooldownSeconds;

    // 粒子效果设置
    private Particle particleType;
    private int particleCount;

    // GUI设置
    private String guiTitle;
    private String acceptItem;
    private String acceptName;
    private String denyItem;
    private String denyName;
    private String infoItem;
    private String infoName;

    public ConfigManager(SyTP plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        loadConfig();
    }

    public void reload() {
        this.config = plugin.getConfig();
        loadConfig();
    }

    private void loadConfig() {
        // 功能开关
        enableTeleportCost = config.getBoolean("enable-teleport-cost", true);
        enableTeleportHereCost = config.getBoolean("enable-teleport-here-cost", true);
        enableTpwCost = config.getBoolean("enable-tpw-cost", true);
        enableTeleportDelay = config.getBoolean("enable-teleport-delay", true);
        enableParticleEffect = config.getBoolean("enable-particle-effect", true);

        // 费用设置
        teleportCost = config.getDouble("teleport-cost", 100.0);
        teleportHereCost = config.getDouble("teleport-here-cost", 100.0);
        tpwCost = config.getDouble("tpw-cost", 500.0);

        // 传送设置
        teleportDelaySeconds = config.getInt("teleport-delay-seconds", 3);
        requestTimeoutSeconds = config.getInt("request-timeout-seconds", 30);
        teleportCooldownSeconds = config.getInt("teleport-cooldown-seconds", 10);

        // 粒子效果设置
        String particleName = config.getString("particle-type", "PORTAL");
        try {
            particleType = Particle.valueOf(particleName);
        } catch (IllegalArgumentException e) {
            particleType = Particle.PORTAL;
            plugin.getLogger().warning("无效的粒子类型: " + particleName + ", 使用默认类型 PORTAL");
        }
        particleCount = config.getInt("particle-count", 50);

        // GUI设置
        guiTitle = config.getString("gui.title", "&8传送请求");
        acceptItem = config.getString("gui.accept-item", "LIME_WOOL");
        acceptName = config.getString("gui.accept-name", "&a&l同意传送");
        denyItem = config.getString("gui.deny-item", "RED_WOOL");
        denyName = config.getString("gui.deny-name", "&c&l拒绝传送");
        infoItem = config.getString("gui.info-item", "PAPER");
        infoName = config.getString("gui.info-name", "&e&l传送信息");
    }

    // Getters
    public boolean isEnableTeleportCost() { return enableTeleportCost; }
    public boolean isEnableTeleportHereCost() { return enableTeleportHereCost; }
    public boolean isEnableTpwCost() { return enableTpwCost; }
    public boolean isEnableTeleportDelay() { return enableTeleportDelay; }
    public boolean isEnableParticleEffect() { return enableParticleEffect; }

    public double getTeleportCost() { return teleportCost; }
    public double getTeleportHereCost() { return teleportHereCost; }
    public double getTpwCost() { return tpwCost; }

    public int getTeleportDelaySeconds() { return teleportDelaySeconds; }
    public int getRequestTimeoutSeconds() { return requestTimeoutSeconds; }
    public int getTeleportCooldownSeconds() { return teleportCooldownSeconds; }

    public Particle getParticleType() { return particleType; }
    public int getParticleCount() { return particleCount; }

    public String getGuiTitle() { return guiTitle; }
    public String getAcceptItem() { return acceptItem; }
    public String getAcceptName() { return acceptName; }
    public String getDenyItem() { return denyItem; }
    public String getDenyName() { return denyName; }
    public String getInfoItem() { return infoItem; }
    public String getInfoName() { return infoName; }
}
