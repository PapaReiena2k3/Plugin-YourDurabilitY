package reieno.ydurability.events;

import static reieno.ydurability.YdyMethods.loreHasDurability;
import static reieno.ydurability.YdyMethods.getFromLore;
import static reieno.ydurability.YdyMethods.getNewMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import reieno.ydurability.Main;

public class DispenserEvents implements Listener{
	private Main plugin;
	public DispenserEvents(Main plugin) {
		this.plugin = plugin;
	}
	
	public Integer getDispenserIndex(ItemStack[] contents, Material material) {
		Integer selectedIndex = 0;
		List<Integer> numberList = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);
		Collections.shuffle(numberList);
		//System.out.println("Lista: "+numberList);
		for(Integer number: numberList) {
			ItemStack item = contents[number];
			if(item != null && item.getType().equals(material) && item.getItemMeta() != null
			&& item.getItemMeta().hasLore() && loreHasDurability(item.getItemMeta().getLore())) {
				selectedIndex = number;
				break;
			}
		}
		return selectedIndex;
	}
	
	@EventHandler
	public void dispenserFlitNSteel(BlockDispenseEvent event){
		ItemStack used = event.getItem();
		if(used.getType().equals(Material.FLINT_AND_STEEL) &&
		used.getItemMeta().hasLore() && loreHasDurability(used.getItemMeta().getLore())) {
			//used es un mechero
			Block BloqueFrente = event.getBlock().getRelative(((Directional) event.getBlock().getBlockData()).getFacing());
			if(BloqueFrente.getType().isAir() && BloqueFrente.getRelative(0,-1,0).getType().isSolid()) {
				event.setCancelled(true);
				//item es realmente usado
				ItemStack[] contents = ((Container) event.getBlock().getState()).getInventory().getContents();
				Integer index = getDispenserIndex(contents, Material.FLINT_AND_STEEL);
				ItemStack item = contents[index];
				List<Integer> durability = getFromLore(item.getItemMeta().getLore());
				BloqueFrente.setType(Material.FIRE);
				if(durability.get(0) <= 1) plugin.getServer().getScheduler().runTask(plugin, () -> ((Container) event.getBlock().getState()).getInventory().removeItem(item));
				else {
					ItemMeta newMeta = getNewMeta(item, durability, 1);
					((Damageable) newMeta).setDamage(((Damageable) newMeta).getDamage()+1);
					item.setItemMeta(newMeta);
				}
			}
		}
	}
	
	@EventHandler
	public void dispenserShearSheep(BlockShearEntityEvent event) {
		ItemStack tool = event.getTool();
		if(tool != null && tool.getItemMeta().hasLore() && loreHasDurability(tool.getItemMeta().getLore())) {
			Block block = event.getBlock();
			ItemStack[] contents = ((Container) block.getState()).getInventory().getContents();
			Integer index = getDispenserIndex(contents, Material.SHEARS);
			ItemStack item = contents[index];
			List<Integer> durability = getFromLore(item.getItemMeta().getLore());
			if(durability.get(0) <= 1) contents[index] = null;
			else {
				item.setItemMeta(getNewMeta(item, durability, 1));
			}
			plugin.getServer().getScheduler().runTask(plugin, () -> ((Container) block.getState()).getInventory().setContents(contents));
		}
	}
}
