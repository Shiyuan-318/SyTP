package com.shiyuan.sytp.managers;

import com.shiyuan.sytp.SyTP;
import com.shiyuan.sytp.requests.TeleportRequest;
import com.shiyuan.sytp.requests.RequestType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RequestManager {

    private final SyTP plugin;
    
    // 存储所有传送请求: 目标玩家 -> 请求列表
    private final Map<UUID, List<TeleportRequest>> requests = new ConcurrentHashMap<>();
    
    // 存储TPW请求: 请求ID -> 请求
    private final Map<UUID, TeleportRequest> tpwRequests = new ConcurrentHashMap<>();

    public RequestManager(SyTP plugin) {
        this.plugin = plugin;
        startCleanupTask();
    }

    /**
     * 创建TPA请求 (请求者传送到目标)
     */
    public void createTpaRequest(Player requester, Player target) {
        removeExistingRequest(requester, target);
        
        TeleportRequest request = new TeleportRequest(
            UUID.randomUUID(),
            requester.getUniqueId(),
            target.getUniqueId(),
            RequestType.TPA,
            System.currentTimeMillis()
        );
        
        requests.computeIfAbsent(target.getUniqueId(), k -> new ArrayList<>()).add(request);
        
        // 发送请求消息和GUI
        plugin.getMessageManager().sendRequestMessage(target, requester, RequestType.TPA);
        plugin.getServer().getScheduler().runTask(plugin, () -> 
            plugin.getTeleportManager().openRequestGUI(target, requester, request)
        );
    }

    /**
     * 创建TPC请求 (目标传送到请求者)
     */
    public void createTpcRequest(Player requester, Player target) {
        removeExistingRequest(requester, target);
        
        TeleportRequest request = new TeleportRequest(
            UUID.randomUUID(),
            requester.getUniqueId(),
            target.getUniqueId(),
            RequestType.TPC,
            System.currentTimeMillis()
        );
        
        requests.computeIfAbsent(target.getUniqueId(), k -> new ArrayList<>()).add(request);
        
        // 发送请求消息和GUI
        plugin.getMessageManager().sendRequestMessage(target, requester, RequestType.TPC);
        plugin.getServer().getScheduler().runTask(plugin, () -> 
            plugin.getTeleportManager().openRequestGUI(target, requester, request)
        );
    }

    /**
     * 创建TPW请求 (全服传送)
     */
    public void createTpwRequest(Player requester) {
        // 移除该玩家之前的TPW请求
        tpwRequests.values().removeIf(r -> r.getRequester().equals(requester.getUniqueId()));
        
        TeleportRequest request = new TeleportRequest(
            UUID.randomUUID(),
            requester.getUniqueId(),
            null, // 目标为null表示全服
            RequestType.TPW,
            System.currentTimeMillis()
        );
        
        tpwRequests.put(request.getId(), request);
        
        // 向所有在线玩家发送请求
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!player.getUniqueId().equals(requester.getUniqueId())) {
                plugin.getMessageManager().sendTpwRequestMessage(player, requester);
                plugin.getServer().getScheduler().runTask(plugin, () -> 
                    plugin.getTeleportManager().openTpwGUI(player, requester, request)
                );
            }
        }
    }

    /**
     * 接受请求
     */
    public boolean acceptRequest(Player target, UUID requestId) {
        // 先检查普通请求
        List<TeleportRequest> targetRequests = requests.get(target.getUniqueId());
        if (targetRequests != null) {
            for (TeleportRequest request : targetRequests) {
                if (request.getId().equals(requestId)) {
                    executeAccept(target, request);
                    targetRequests.remove(request);
                    return true;
                }
            }
        }
        
        // 再检查TPW请求
        TeleportRequest tpwRequest = tpwRequests.get(requestId);
        if (tpwRequest != null) {
            executeTpwAccept(target, tpwRequest);
            return true;
        }
        
        return false;
    }

    /**
     * 拒绝请求
     */
    public boolean denyRequest(Player target, UUID requestId) {
        // 先检查普通请求
        List<TeleportRequest> targetRequests = requests.get(target.getUniqueId());
        if (targetRequests != null) {
            for (TeleportRequest request : targetRequests) {
                if (request.getId().equals(requestId)) {
                    executeDeny(target, request);
                    targetRequests.remove(request);
                    return true;
                }
            }
        }
        
        // 再检查TPW请求
        TeleportRequest tpwRequest = tpwRequests.remove(requestId);
        if (tpwRequest != null) {
            executeTpwDeny(target, tpwRequest);
            return true;
        }
        
        return false;
    }

    /**
     * 获取玩家的待处理请求
     */
    public List<TeleportRequest> getPendingRequests(Player player) {
        return requests.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }

    /**
     * 获取TPW请求
     */
    public TeleportRequest getTpwRequest(UUID requestId) {
        return tpwRequests.get(requestId);
    }

    /**
     * 执行接受普通请求
     */
    private void executeAccept(Player target, TeleportRequest request) {
        Player requester = plugin.getServer().getPlayer(request.getRequester());
        if (requester == null || !requester.isOnline()) {
            plugin.getMessageManager().sendMessage(target, "player-not-found");
            return;
        }

        switch (request.getType()) {
            case TPA:
                // TPA: 请求者传送到目标，向请求者（发起者）收费
                plugin.getMessageManager().sendMessage(target, "tpa-accepted", "player", requester.getName());
                plugin.getMessageManager().sendMessage(requester, "tpa-accepted-target", "player", target.getName());
                plugin.getTeleportManager().teleport(requester, target.getLocation(), plugin.getConfigManager().getTeleportCost(), requester);
                break;
                
            case TPC:
                // TPC: 目标传送到请求者，向请求者（发起者）收费
                plugin.getMessageManager().sendMessage(target, "tpc-accepted", "player", requester.getName());
                plugin.getMessageManager().sendMessage(requester, "tpc-accepted-target", "player", target.getName());
                plugin.getTeleportManager().teleport(target, requester.getLocation(), plugin.getConfigManager().getTeleportHereCost(), requester);
                break;
        }
    }

    /**
     * 执行拒绝普通请求
     */
    private void executeDeny(Player target, TeleportRequest request) {
        Player requester = plugin.getServer().getPlayer(request.getRequester());
        
        switch (request.getType()) {
            case TPA:
                plugin.getMessageManager().sendMessage(target, "tpa-denied", "player", 
                    requester != null ? requester.getName() : "未知玩家");
                if (requester != null && requester.isOnline()) {
                    plugin.getMessageManager().sendMessage(requester, "tpa-denied-target", "player", target.getName());
                }
                break;
                
            case TPC:
                plugin.getMessageManager().sendMessage(target, "tpc-denied", "player", 
                    requester != null ? requester.getName() : "未知玩家");
                if (requester != null && requester.isOnline()) {
                    plugin.getMessageManager().sendMessage(requester, "tpc-denied-target", "player", target.getName());
                }
                break;
        }
    }

    /**
     * 执行接受TPW请求
     */
    private void executeTpwAccept(Player player, TeleportRequest request) {
        Player requester = plugin.getServer().getPlayer(request.getRequester());
        if (requester == null || !requester.isOnline()) {
            plugin.getMessageManager().sendMessage(player, "player-not-found");
            return;
        }

        // 检查该请求是否已经收取过费用
        double cost = 0;
        if (!request.isCharged()) {
            cost = plugin.getConfigManager().getTpwCost();
            request.setCharged(true); // 标记已收费
        }

        plugin.getMessageManager().sendMessage(player, "tpw-accepted", "player", requester.getName());
        plugin.getTeleportManager().teleport(player, requester.getLocation(), cost, requester);
    }

    /**
     * 执行拒绝TPW请求
     */
    private void executeTpwDeny(Player player, TeleportRequest request) {
        // TPW拒绝不需要通知发起者
    }

    /**
     * 移除已存在的请求
     */
    private void removeExistingRequest(Player requester, Player target) {
        List<TeleportRequest> targetRequests = requests.get(target.getUniqueId());
        if (targetRequests != null) {
            targetRequests.removeIf(r -> r.getRequester().equals(requester.getUniqueId()));
        }
    }

    /**
     * 清理过期请求
     */
    private void startCleanupTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long timeout = plugin.getConfigManager().getRequestTimeoutSeconds() * 1000L;
                long now = System.currentTimeMillis();
                
                // 清理普通请求
                requests.values().forEach(list -> 
                    list.removeIf(r -> now - r.getTimestamp() > timeout)
                );
                requests.values().removeIf(List::isEmpty);
                
                // 清理TPW请求
                tpwRequests.values().removeIf(r -> now - r.getTimestamp() > timeout);
            }
        }.runTaskTimerAsynchronously(plugin, 20L * 60, 20L * 60); // 每分钟清理一次
    }

    /**
     * 清除所有请求
     */
    public void clearAllRequests() {
        requests.clear();
        tpwRequests.clear();
    }
}
