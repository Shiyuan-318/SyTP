package com.shiyuan.sytp.gui;

import com.shiyuan.sytp.SyTP;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class GUIListener implements Listener {

    private final SyTP plugin;
    private final NamespacedKey actionKey;

    public GUIListener(SyTP plugin) {
        this.plugin = plugin;
        this.actionKey = new NamespacedKey(plugin, "sytp_action");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title = event.getView().getTitle();
        if (!title.contains("传送请求") && !title.contains("全服传送请求")) {
            return;
        }

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        PersistentDataContainer container = clickedItem.getItemMeta().getPersistentDataContainer();
        String action = container.get(actionKey, PersistentDataType.STRING);
        
        if (action == null) {
            return;
        }

        String[] parts = action.split(":");
        if (parts.length != 2) {
            return;
        }

        String actionType = parts[0];
        UUID requestId;
        try {
            requestId = UUID.fromString(parts[1]);
        } catch (IllegalArgumentException e) {
            return;
        }

        // 关闭GUI
        player.closeInventory();

        // 处理动作
        switch (actionType.toUpperCase()) {
            case "ACCEPT" -> {
                boolean success = plugin.getRequestManager().acceptRequest(player, requestId);
                if (!success) {
                    plugin.getMessageManager().sendMessage(player, "request-expired");
                }
            }
            case "DENY" -> {
                boolean success = plugin.getRequestManager().denyRequest(player, requestId);
                if (!success) {
                    plugin.getMessageManager().sendMessage(player, "request-expired");
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // 可以在这里添加额外的清理逻辑
    }
}
