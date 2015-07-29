package net.thinkofhell.simplewrench;

import java.util.logging.Level;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public static Main plugin;
    public static Economy economy = null;

    public Main() {
    }

    public void onEnable() {
        plugin = this;
        this.getServer().getPluginManager().registerEvents(new MainListener(), this);
        if(!this.setupEconomy()) {
            System.out.print("[SimpleWrench] Could not hook into Vault!");
        }

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            System.out.print("[SimpleWrench] Console cannot perform wrench commands.");
            return false;
        } else {
            Player p = (Player)sender;
            if(args.length == 1 && args[0].equalsIgnoreCase("give")) {
                if(p.hasPermission("simplewrench.give")) {
                    if(p.getInventory().firstEmpty() != -1) {
                        p.getInventory().addItem(new ItemStack[]{getWrench()});
                    } else {
                        p.sendMessage(ChatColor.RED + "Empty your inventory to receive a wrench!");
                    }
                } else {
                    p.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command.");
                }
            } else {
                this.displayHelp(sender);
            }

            return false;
        }
    }

    private void displayHelp(CommandSender s) {
        s.sendMessage(ChatColor.DARK_RED + "Use like /wrench give");
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class);
        if(economyProvider != null) {
            economy = (Economy)economyProvider.getProvider();
        }

        return economy != null;
    }

    public static ItemStack getWrench() {
        ItemStack is = new ItemStack(Material.GOLD_PICKAXE);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.GOLD + "Wrench");
        is.setItemMeta(im);
        return is;
    }
}

