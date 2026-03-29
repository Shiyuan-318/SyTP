package com.shiyuan.sytp.commands;

import com.shiyuan.sytp.SyTP;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPCCommand implements CommandExecutor {

    private final SyTP plugin;

    public TPCCommand(SyTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c此命令只能由玩家执行!");
            return true;
        }

        if (!player.hasPermission("sytp.tpc")) {
            plugin.getMessageManager().sendMessage(player, "no-permission");
            return true;
        }

        if (args.length < 1) {
            plugin.getMessageManager().sendRawMessage(player, "&c用法: /tpc <玩家名>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            plugin.getMessageManager().sendMessage(player, "player-not-found");
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            plugin.getMessageManager().sendRawMessage(player, "&c你不能邀请自己!");
            return true;
        }

        // 检查费用（费用为0时不检查）
        if (plugin.getConfigManager().isEnableTeleportHereCost() && !player.hasPermission("sytp.bypass.cost")) {
            double cost = plugin.getConfigManager().getTeleportHereCost();
            if (cost > 0 && plugin.hasEconomy() && plugin.getEconomy().getBalance(player) < cost) {
                plugin.getMessageManager().sendMessage(player, "not-enough-money", "cost", String.valueOf(cost));
                return true;
            }
        }

        // 创建传送请求
        plugin.getRequestManager().createTpcRequest(player, target);
        plugin.getMessageManager().sendMessage(player, "tpc-sent", "player", target.getName());

        return true;
    }
}
