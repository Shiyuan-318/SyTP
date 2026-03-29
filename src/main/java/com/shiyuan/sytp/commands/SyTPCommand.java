package com.shiyuan.sytp.commands;

import com.shiyuan.sytp.SyTP;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SyTPCommand implements CommandExecutor {

    private final SyTP plugin;

    public SyTPCommand(SyTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c此命令只能由玩家执行!");
            return true;
        }

        if (!player.hasPermission("sytp.admin")) {
            plugin.getMessageManager().sendMessage(player, "no-permission");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.reloadPlugin();
                player.sendMessage("§a[SyTP] 配置文件已重载!");
            }
            case "help" -> sendHelp(player);
            default -> sendHelp(player);
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage("§8========== §bSyTP §8==========");
        player.sendMessage("§7/sytp reload §8- §f重载配置文件");
        player.sendMessage("§7/sytp help §8- §f显示帮助");
        player.sendMessage("§8===========================");
    }
}
