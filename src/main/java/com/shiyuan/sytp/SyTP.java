package com.shiyuan.sytp;

import com.shiyuan.sytp.commands.*;
import com.shiyuan.sytp.gui.GUIListener;
import com.shiyuan.sytp.managers.*;
import com.shiyuan.sytp.utils.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SyTP extends JavaPlugin {

    private static SyTP instance;
    private Economy economy;
    private ConfigManager configManager;
    private RequestManager requestManager;
    private TeleportManager teleportManager;
    private CooldownManager cooldownManager;
    private ParticleManager particleManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        configManager = new ConfigManager(this);
        requestManager = new RequestManager(this);
        teleportManager = new TeleportManager(this);
        cooldownManager = new CooldownManager(this);
        particleManager = new ParticleManager(this);
        messageManager = new MessageManager(this);
        
        setupEconomy();
        
        registerCommands();
        registerListeners();
        
        getLogger().info("=================================");
        getLogger().info("SyTP 插件已启用!");
        getLogger().info("作者: Shiyuan");
        getLogger().info("版本: " + getDescription().getVersion());
        getLogger().info("=================================");
    }

    @Override
    public void onDisable() {
        if (requestManager != null) {
            requestManager.clearAllRequests();
        }
        
        getLogger().info("SyTP 插件已禁用!");
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("未找到 Vault 插件，经济功能将不可用!");
            return;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().warning("未找到经济服务提供者，经济功能将不可用!");
            return;
        }
        
        economy = rsp.getProvider();
        getLogger().info("Vault 经济系统已接入!");
    }

    private void registerCommands() {
        getCommand("sytp").setExecutor(new SyTPCommand(this));
        getCommand("tpa").setExecutor(new TPACommand(this));
        getCommand("tpc").setExecutor(new TPCCommand(this));
        getCommand("tpw").setExecutor(new TPWCommand(this));
        getCommand("tpaccept").setExecutor(new TPAcceptCommand(this));
        getCommand("tpdeny").setExecutor(new TPDenyCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new TeleportListener(this), this);
    }

    public static SyTP getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return economy;
    }

    public boolean hasEconomy() {
        return economy != null;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public ParticleManager getParticleManager() {
        return particleManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public void reloadPlugin() {
        reloadConfig();
        configManager.reload();
        messageManager.reload();
    }
}
