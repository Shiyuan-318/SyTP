package com.shiyuan.sytp.managers;

import com.shiyuan.sytp.SyTP;
import com.shiyuan.sytp.requests.TeleportRequest;
import com.shiyuan.sytp.requests.RequestType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportManager {

    private final SyTP plugin;
    
    // 存储正在进行的传送任务
    private final Map<UUID, BukkitTask> teleportTasks = new ConcurrentHashMap<>();
    
    // 存储玩家传送前的位置（用于检测移动）
    private final Map<UUID, Location> preTeleportLocations = new ConcurrentHashMap<>();

    public TeleportManager(SyTP plugin) {
        this.plugin = plugin;
    }

    /**
     * 打开传送请求GUI
     */
    public void openRequestGUI(Player target, Player requester, TeleportRequest request) {
        String title = plugin.getMessageManager().colorize(plugin.getConfigManager().getGuiTitle());
        Inventory gui = Bukkit.createInventory(null, 27, title);

        // 信息物品
        ItemStack infoItem = createGuiItem(
            Material.valueOf(plugin.getConfigManager().getInfoItem()),
            plugin.getConfigManager().getInfoName(),
            Arrays.asList(
                "&7类型: &f" + getTypeName(request.getType()),
                "&7玩家: &f" + requester.getName()
            )
        );
        gui.setItem(13, infoItem);

        // 同意按钮
        ItemStack acceptItem = createGuiItem(
            Material.valueOf(plugin.getConfigManager().getAcceptItem()),
            plugin.getConfigManager().getAcceptName(),
            Arrays.asList(
                "&7点击同意此传送请求",
                "",
                "&e请求者: &f" + requester.getName(),
                "&e请求ID: &f" + request.getId().toString().substring(0, 8)
            ),
            "ACCEPT:" + request.getId().toString()
        );
        gui.setItem(11, acceptItem);

        // 拒绝按钮
        ItemStack denyItem = createGuiItem(
            Material.valueOf(plugin.getConfigManager().getDenyItem()),
            plugin.getConfigManager().getDenyName(),
            Arrays.asList(
                "&7点击拒绝此传送请求",
                "",
                "&e请求者: &f" + requester.getName(),
                "&e请求ID: &f" + request.getId().toString().substring(0, 8)
            ),
            "DENY:" + request.getId().toString()
        );
        gui.setItem(15, denyItem);

        target.openInventory(gui);
    }

    /**
     * 打开TPW请求GUI
     */
    public void openTpwGUI(Player player, Player requester, TeleportRequest request) {
        String title = plugin.getMessageManager().colorize("&8全服传送请求");
        Inventory gui = Bukkit.createInventory(null, 27, title);

        // 信息物品
        ItemStack infoItem = createGuiItem(
            Material.valueOf(plugin.getConfigManager().getInfoItem()),
            "&e&l全服传送邀请",
            Arrays.asList(
                "&7发起者: &f" + requester.getName(),
                "&7类型: &f所有人传送到发起者",
                "",
                "&7点击同意传送到该玩家位置"
            )
        );
        gui.setItem(13, infoItem);

        // 同意按钮
        ItemStack acceptItem = createGuiItem(
            Material.valueOf(plugin.getConfigManager().getAcceptItem()),
            plugin.getConfigManager().getAcceptName(),
            Arrays.asList(
                "&7点击同意传送",
                "",
                "&e发起者: &f" + requester.getName()
            ),
            "ACCEPT:" + request.getId().toString()
        );
        gui.setItem(11, acceptItem);

        // 拒绝按钮
        ItemStack denyItem = createGuiItem(
            Material.valueOf(plugin.getConfigManager().getDenyItem()),
            plugin.getConfigManager().getDenyName(),
            Arrays.asList(
                "&7点击拒绝传送",
                "",
                "&e发起者: &f" + requester.getName()
            ),
            "DENY:" + request.getId().toString()
        );
        gui.setItem(15, denyItem);

        player.openInventory(gui);
    }

    /**
     * 执行传送
     * @param player 被传送的玩家
     * @param location 目标位置
     * @param cost 费用（从发起者扣除）
     * @param costPayer 支付费用的玩家（发起者）
     */
    public void teleport(Player player, Location location, double cost, Player costPayer) {
        // 检查冷却
        if (plugin.getCooldownManager().isOnCooldown(player)) {
            int remaining = plugin.getCooldownManager().getRemainingCooldown(player);
            plugin.getMessageManager().sendMessage(player, "cooldown-active", "seconds", String.valueOf(remaining));
            return;
        }

        // 检查是否需要延迟
        if (plugin.getConfigManager().isEnableTeleportDelay() && !player.hasPermission("sytp.bypass.delay")) {
            startDelayedTeleport(player, location, cost, costPayer);
        } else {
            executeTeleport(player, location, cost, costPayer);
        }
    }

    /**
     * 执行传送（无费用）
     */
    public void teleport(Player player, Location location) {
        teleport(player, location, 0, null);
    }

    /**
     * 开始延迟传送
     */
    private void startDelayedTeleport(Player player, Location location, double cost, Player costPayer) {
        int delay = plugin.getConfigManager().getTeleportDelaySeconds();
        
        // 取消之前的传送任务
        cancelTeleport(player);
        
        // 记录当前位置
        preTeleportLocations.put(player.getUniqueId(), player.getLocation().clone());
        
        plugin.getMessageManager().sendMessage(player, "teleport-in-progress", "seconds", String.valueOf(delay));
        
        BukkitTask task = new BukkitRunnable() {
            int seconds = delay;
            
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancelTeleport(player);
                    return;
                }
                
                // 检查玩家是否移动
                Location preLoc = preTeleportLocations.get(player.getUniqueId());
                if (preLoc != null && hasMoved(player.getLocation(), preLoc)) {
                    plugin.getMessageManager().sendMessage(player, "teleport-cancelled");
                    cancelTeleport(player);
                    return;
                }
                
                seconds--;
                
                if (seconds <= 0) {
                    executeTeleport(player, location, cost, costPayer);
                    cancelTeleport(player);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
        
        teleportTasks.put(player.getUniqueId(), task);
    }

    /**
     * 执行实际传送
     */
    private void executeTeleport(Player player, Location location, double cost, Player costPayer) {
        // 先执行传送
        player.teleport(location);
        
        // 传送成功后扣除费用（从发起者）
        if (cost > 0 && costPayer != null && !costPayer.hasPermission("sytp.bypass.cost")) {
            if (!plugin.hasEconomy()) {
                plugin.getMessageManager().sendMessage(costPayer, "&c经济系统未启用，无法扣除费用!");
            } else if (plugin.getEconomy().getBalance(costPayer) < cost) {
                plugin.getMessageManager().sendMessage(costPayer, "not-enough-money", "cost", String.valueOf(cost));
            } else {
                plugin.getEconomy().withdrawPlayer(costPayer, cost);
                plugin.getMessageManager().sendMessage(costPayer, "cost-deducted", "cost", String.valueOf(cost));
            }
        } else if (costPayer != null && costPayer.hasPermission("sytp.bypass.cost")) {
            plugin.getMessageManager().sendMessage(costPayer, "cost-free");
        }
        
        plugin.getMessageManager().sendMessage(player, "teleport-success");
        plugin.getCooldownManager().setCooldown(player);
        
        // 播放粒子效果
        if (plugin.getConfigManager().isEnableParticleEffect()) {
            plugin.getParticleManager().playTeleportEffect(location);
        }
    }

    /**
     * 取消传送
     */
    public void cancelTeleport(Player player) {
        BukkitTask task = teleportTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
        preTeleportLocations.remove(player.getUniqueId());
    }

    /**
     * 检查玩家是否移动
     */
    private boolean hasMoved(Location current, Location previous) {
        return current.getBlockX() != previous.getBlockX() ||
               current.getBlockY() != previous.getBlockY() ||
               current.getBlockZ() != previous.getBlockZ();
    }

    /**
     * 创建GUI物品
     */
    private ItemStack createGuiItem(Material material, String name, List<String> lore) {
        return createGuiItem(material, name, lore, null);
    }

    /**
     * 创建GUI物品（带NBT）
     */
    private ItemStack createGuiItem(Material material, String name, List<String> lore, String nbtData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(plugin.getMessageManager().colorize(name));
            
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(plugin.getMessageManager().colorize(line));
            }
            meta.setLore(coloredLore);
            
            // 存储NBT数据
            if (nbtData != null) {
                meta.getPersistentDataContainer().set(
                    new org.bukkit.NamespacedKey(plugin, "sytp_action"),
                    org.bukkit.persistence.PersistentDataType.STRING,
                    nbtData
                );
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }

    /**
     * 获取请求类型名称
     */
    private String getTypeName(RequestType type) {
        return switch (type) {
            case TPA -> "传送到他人";
            case TPC -> "他人传送到我";
            case TPW -> "全服传送";
        };
    }

    /**
     * 玩家退出时清理
     */
    public void onPlayerQuit(Player player) {
        cancelTeleport(player);
    }
}
