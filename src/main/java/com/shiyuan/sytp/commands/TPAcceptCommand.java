package com.shiyuan.sytp.commands;

import com.shiyuan.sytp.SyTP;
import com.shiyuan.sytp.requests.TeleportRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TPAcceptCommand implements CommandExecutor {

    private final SyTP plugin;

    public TPAcceptCommand(SyTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c此命令只能由玩家执行!");
            return true;
        }

        if (!player.hasPermission("sytp.accept")) {
            plugin.getMessageManager().sendMessage(player, "no-permission");
            return true;
        }

        // 获取玩家的待处理请求
        List<TeleportRequest> requests = plugin.getRequestManager().getPendingRequests(player);
        
        // 如果没有普通请求，尝试接受最近的TPW请求
        if (requests.isEmpty()) {
            // 尝试找到最近的TPW请求
            TeleportRequest recentTpw = findRecentTpwRequest(player);
            if (recentTpw != null) {
                plugin.getRequestManager().acceptRequest(player, recentTpw.getId());
                return true;
            }
            
            plugin.getMessageManager().sendMessage(player, "tpw-no-requests");
            return true;
        }

        // 接受最近的请求
        TeleportRequest recentRequest = requests.get(requests.size() - 1);
        boolean success = plugin.getRequestManager().acceptRequest(player, recentRequest.getId());
        
        if (!success) {
            plugin.getMessageManager().sendMessage(player, "request-expired");
        }

        return true;
    }

    /**
     * 查找最近的TPW请求
     */
    private TeleportRequest findRecentTpwRequest(Player player) {
        TeleportRequest mostRecent = null;
        long mostRecentTime = 0;
        
        // 遍历所有在线玩家的TPW请求
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            // 这里简化处理，实际应该在RequestManager中存储TPW请求
            // 目前通过命令接受TPW需要通过GUI
        }
        
        return mostRecent;
    }
}
