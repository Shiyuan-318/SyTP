package com.shiyuan.sytp.commands;

import com.shiyuan.sytp.SyTP;
import com.shiyuan.sytp.requests.TeleportRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TPDenyCommand implements CommandExecutor {

    private final SyTP plugin;

    public TPDenyCommand(SyTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c此命令只能由玩家执行!");
            return true;
        }

        if (!player.hasPermission("sytp.deny")) {
            plugin.getMessageManager().sendMessage(player, "no-permission");
            return true;
        }

        // 获取玩家的待处理请求
        List<TeleportRequest> requests = plugin.getRequestManager().getPendingRequests(player);
        
        if (requests.isEmpty()) {
            plugin.getMessageManager().sendMessage(player, "tpw-no-requests");
            return true;
        }

        // 拒绝最近的请求
        TeleportRequest recentRequest = requests.get(requests.size() - 1);
        boolean success = plugin.getRequestManager().denyRequest(player, recentRequest.getId());
        
        if (!success) {
            plugin.getMessageManager().sendMessage(player, "request-expired");
        }

        return true;
    }
}
