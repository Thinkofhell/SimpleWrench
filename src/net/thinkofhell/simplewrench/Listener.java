package net.thinkofhell.simplewrench;

import me.MnMaxon.Wrench.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Listener implements Listener {
    public Listener() {
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        ItemStack is = e.getPlayer().getItemInHand();
        if(is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().equals(ChatColor.RED + "Wrench")) {
            if(e.getBlock().getType().equals(Material.MOB_SPAWNER)) {
                if(!e.getPlayer().hasPermission("wrench.use")) {
                    e.getPlayer().sendMessage(ChatColor.DARK_RED + "You do not have permission to do this!");
                    e.setCancelled(true);
                    return;
                }

                ItemStack spawnerStack = new ItemStack(e.getBlock().getType());
                ItemMeta im = spawnerStack.getItemMeta();
                im.setDisplayName(((CreatureSpawner)e.getBlock().getState()).getCreatureTypeName().toUpperCase() + " SPAWNER");
                spawnerStack.setItemMeta(im);
                spawnerStack.setDurability(((CreatureSpawner)e.getBlock().getState()).getSpawnedType().getTypeId());
                e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), spawnerStack);
                e.getPlayer().getInventory().setItemInHand((ItemStack)null);
                e.getPlayer().updateInventory();
            } else {
                e.getPlayer().sendMessage(ChatColor.DARK_RED + "You can\'t do that with a wrench!");
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if(e.getBlockPlaced().getType().equals(Material.MOB_SPAWNER) && e.getItemInHand().hasItemMeta() && e.getItemInHand().getItemMeta().hasDisplayName() && EntityType.fromName(e.getItemInHand().getItemMeta().getDisplayName().replace(" SPAWNER", "")) != null) {
            ((CreatureSpawner)e.getBlockPlaced().getState()).setCreatureTypeByName(e.getItemInHand().getItemMeta().getDisplayName().replace(" Spawner", ""));
        }

    }

    @EventHandler
    public void onSign(SignChangeEvent e) {
        if(e.getLine(0).equalsIgnoreCase("[Wrench]")) {
            if(!e.getPlayer().hasPermission("Wrench.sign")) {
                e.getPlayer().sendMessage(ChatColor.DARK_RED + "You do not have permission to do this!");
                e.getBlock().breakNaturally();
                return;
            }

            e.setLine(0, ChatColor.RED + "Wrench");

            try {
                e.setLine(1, ChatColor.GREEN + "Cost: " + Integer.parseInt(e.getLine(1)));
            } catch (NumberFormatException var3) {
                e.getBlock().breakNaturally();
                e.getPlayer().sendMessage("" + ChatColor.DARK_AQUA + ChatColor.ITALIC + e.getLine(1) + ChatColor.RESET + ChatColor.DARK_RED + " is not an integer!");
            }
        }

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getState() instanceof Sign) {
            Sign sign = (Sign)e.getClickedBlock().getState();
            if(sign.getLine(0).equals(ChatColor.RED + "Wrench")) {
                boolean cost = false;

                int cost1;
                try {
                    cost1 = Integer.parseInt(ChatColor.stripColor(sign.getLine(1).replace("Cost: ", "")));
                } catch (NumberFormatException var5) {
                    e.getPlayer().sendMessage(ChatColor.DARK_RED + "This sign is not set up properly");
                    return;
                }

                if(e.getPlayer().getInventory().firstEmpty() != -1) {
                    if(Main.economy.getBalance(e.getPlayer().getName()) >= (double)cost1) {
                        e.getPlayer().getInventory().addItem(new ItemStack[]{Main.getWrench()});
                        e.getPlayer().updateInventory();
                        Main.economy.withdrawPlayer(e.getPlayer().getName(), (double)cost1);
                        e.getPlayer().sendMessage(ChatColor.GREEN + "Enjoy your wrench!");
                    } else {
                        e.getPlayer().sendMessage(ChatColor.DARK_RED + "You do not have enough money to do this");
                    }
                } else {
                    e.getPlayer().sendMessage(ChatColor.DARK_RED + "Your inventory is full!");
                }
            }
        }

    }
}

