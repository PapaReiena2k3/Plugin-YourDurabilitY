package reieno.ydurability.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import reieno.ydurability.Main;

public class Usercmd implements CommandExecutor{
	private Main plugin;
	public Usercmd(Main plugin) {
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command comand, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.name+ChatColor.WHITE+" This isn't a console command.");
		}else {
			Player player = (Player) sender;
			ItemStack item = player.getInventory().getItemInMainHand();
			//falta verificar si item es nulo
			if(item != null && item.getItemMeta() != null) {
				PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
				List<String> stringKeys = new ArrayList<String>();
				Set<NamespacedKey> rawKeys = dataContainer.getKeys();
				Boolean NoItem = true;
				for(NamespacedKey key: rawKeys) {
					if(key.toString().startsWith("yourdurability:item")) {
						String string = dataContainer.get(key, PersistentDataType.STRING);
						for(String RepairItemString : plugin.RepairItems) {
							if(string.equals(RepairItemString)) {
								NoItem = false;
								FileConfiguration config = plugin.getRepairItems();
								String name = ChatColor.translateAlternateColorCodes('&', config.getString(string+".CustomName"));
								//player.sendMessage("Nombre: "+name);
								if(name == null || name.equals("") || ChatColor.stripColor(name).equals("")) {
									name = ChatColor.translateAlternateColorCodes('&', config.getString(string+".UserName"));
								}
								stringKeys.add(name+ChatColor.RESET);
							}
						}
					}
				}
				if(NoItem == true) {
					player.sendMessage(message("Messages.no-repair"));	
				}else {
					player.sendMessage(message("Messages.ri-success")+" "+ChatColor.RESET+stringKeys);	
				}
			}			
		}
		return false;
	}
	
	public String message(String path) {
		String mensaje = Main.name+ChatColor.RESET+" "+ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path));
		return mensaje;
	}

}
