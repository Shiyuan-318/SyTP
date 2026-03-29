package com.shiyuan.sytp.commands;

import com.shiyuan.sytp.SyTP;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPWCommand implements CommandExecutor {

    private final SyTP plugin;

    public TPWCommand(SyTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c此命令只能由玩家执行!");
            return true;
        }

        if (!player.hasPermission("sytp.tpw")) {
            plugin.getMessageManager().sendMessage(player, "no-permission");
            return true;
        }

        // 检查费用（只检查余额，不预先扣除，传送成功后才扣费；费用为0时不检查）
        if (plugin.getConfigManager().isEnableTpwCost() && !player.hasPermission("sytp.bypass.cost")) {
            double cost = plugin.getConfigManager().getTpwCost();
            if (cost > 0 && plugin.hasEconomy() && plugin.getEconomy().getBalance(player) < cost) {
                plugin.getMessageManager().sendMessage(player, "not-enough-money", "cost", String.valueOf(cost));
                return true;
            }
        }

        // 创建全服传送请求
        plugin.getRequestManager().createTpwRequest(player);
        plugin.getMessageManager().sendMessage(player, "tpw-sent");

        return true;
    }
}
