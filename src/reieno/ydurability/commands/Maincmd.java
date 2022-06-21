package reieno.ydurability.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import reieno.ydurability.Main;

import static reieno.ydurability.YdyMethods.loreHasDurability;

public class Maincmd implements CommandExecutor{
	private Main plugin;
	public Maincmd(Main plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command comand, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.name+ChatColor.WHITE+" This isn't a console command.");
		}else {
			Player player = (Player) sender;
			if(args.length <= 0) {
				// Missing arguments
				player.sendMessage(message("missing-args"));
				player.sendMessage(message("main-usage"));
				return true;
			}
			/*
			//	ydy version  \\  
			*/
			if(args[0].equalsIgnoreCase("version")) {
				player.sendMessage(message("version")+" "+ChatColor.RESET+plugin.version);
			/*
			//	ydy reload  \\  
			*/
			}else if(args[0].equalsIgnoreCase("reload")) {
				plugin.reloadConfig();
				plugin.registerConfig();
				plugin.registerRepairItems();
				player.sendMessage(message("reload"));
			/*
			//	ydy set  \\  
			*/
			}else if(args[0].equalsIgnoreCase("set")) {
				//  Catch errors  \\  
				ItemStack item = player.getInventory().getItemInMainHand();
				if(args.length <= 1) {
					player.sendMessage(message("missing-args"));
					player.sendMessage(message("set-usage"));
					return true;
				}
				if(item == null || item.getType().getMaxDurability() <= 0) {
					player.sendMessage(message("unsupported-durability"));
					return true;
				}
				int currentDurability;
				int maxDurability;
				try {
					String[] words = (args[1].split("/", 3));
					currentDurability = Integer.valueOf(words[0]);
					maxDurability = Integer.valueOf(words[1]);
				}catch(NumberFormatException exception){
					player.sendMessage(message("invalid-number"));
					player.sendMessage(message("set-usage"));
					return true;
				}
				String[] words = (args[1].split("/", 3));
				currentDurability = Short.valueOf(words[0]);
				maxDurability = Short.valueOf(words[1]);
				if(currentDurability > maxDurability) {
					currentDurability = maxDurability;
				}
				//  No errors  \\
				int loreIndex = 0;
				if(!Main.useFirstLine && item.getItemMeta().hasLore()) 
					loreIndex = item.getItemMeta().getLore().size()-1;
				ItemMeta newMeta = item.getItemMeta();
				int totalRealDurability = item.getType().getMaxDurability();
				List<String> newLore = getNewLore(newMeta, loreIndex, currentDurability, maxDurability);//UPDATE METHOD NAME
				newMeta.setLore(newLore);
				if(args.length > 2) {
					PersistentDataContainer dataContainer = newMeta.getPersistentDataContainer();
					setCommandRepairItems(dataContainer, args);
				}
				int itemDamage;
				if(currentDurability<=0) itemDamage = totalRealDurability;
			    else itemDamage = totalRealDurability-((totalRealDurability*(currentDurability)/maxDurability));
				((Damageable) newMeta).setDamage(itemDamage);
				item.setItemMeta(newMeta);
				player.sendMessage(message("set-success"));
			/*
			//	ydy get  \\  
			*/
			}else if(args[0].equalsIgnoreCase("get")){
				//  Catch errors  \\ 
				ItemStack item = player.getInventory().getItemInMainHand();
				if(args.length <= 1) {
					player.sendMessage(message("missing-args"));
					player.sendMessage(message("get-usage"));
					return true;
				}
				//  No errors  \\ 
				/*
				//	ydy get repair  \\  
				*/
				if (args[1].equalsIgnoreCase("repair")) {
					//  Catch errors  \\
					if(item == null || item.getItemMeta() == null) {//Moved
						player.sendMessage(message("no-item"));
						return true;
					}//Moved
					PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
					NamespacedKey key1 = new NamespacedKey(plugin, "item1");
					if (!dataContainer.has(key1, PersistentDataType.STRING)) {
						player.sendMessage(message("no-repair"));
						return true;
					}
					//  No errors  \\
					List<String> stringKeys = new ArrayList<String>();
					Set<NamespacedKey> rawKeys = dataContainer.getKeys();
					for(NamespacedKey key: rawKeys) {
						String stringKey = key.toString();
						if(stringKey.startsWith("yourdurability:item")) {
							stringKeys.add(dataContainer.get(key, PersistentDataType.STRING));
						}
					}
					player.sendMessage(message("get-repair-success")+" "+ChatColor.GREEN+stringKeys);
				/*
				//	ydy get item  \\
				*/
				}else if (args[1].equalsIgnoreCase("item")) {
					//  Catch errors  \\
					if(args.length <= 2) {//Error reparado
						player.sendMessage(message("missing-args"));
						player.sendMessage(message("get-item-usage"));
						return true;
					}
					Integer index = plugin.RepairItems.indexOf(args[2]);
					if(index == -1) {
						player.sendMessage(message("invalid-repair"));
						return true;
					}
					//  No errors  \\
					FileConfiguration config = plugin.getRepairItems();
					Material material = Material.getMaterial(config.getString(args[2]+".Material").toUpperCase());
					String name = ChatColor.translateAlternateColorCodes('&', config.getString(args[2]+".CustomName"));
					ItemStack newItem = new ItemStack(material);
					ItemMeta newMeta = newItem.getItemMeta();
					int amount = 1;
					if(args.length > 3) {
						boolean error = false;
						try {
							amount = Integer.valueOf(args[3]);
						}catch(NumberFormatException exception){
							error = true;			
						}
						if(!error) {
							amount = Integer.valueOf(args[3]);
						}
					}
					newItem.setAmount(amount);
					newMeta.setDisplayName(name);
					newItem.setItemMeta(newMeta);
					player.getInventory().addItem(newItem);
					player.sendMessage(message("get-item-success")+" "+ChatColor.RESET+name+ChatColor.RESET+" x"+amount);//Item recieved message
					
				}else {
					player.sendMessage(message("invalid-args"));
					player.sendMessage(message("get-usage"));
				}
			/*
			//	ydy remove  \\  
			*/
			}else if(args[0].equalsIgnoreCase("remove")){
				//  Catch errors  \\
				ItemStack item = player.getInventory().getItemInMainHand();
				if(item == null || item.getItemMeta() == null) {
					player.sendMessage(message("no-item"));
					return true;
				}
				if(args.length <= 1) {
					player.sendMessage(message("missing-args"));
					player.sendMessage(message("remove-usage"));
					return true;
				}
				ItemMeta newMeta = item.getItemMeta();
				//  No errors  \\
				/*
				//	ydy remove repair  \\  
				*/
				if(args[1].equalsIgnoreCase("repair")) {
					//  Catch errors  \\
					PersistentDataContainer dataContainer = newMeta.getPersistentDataContainer();
					NamespacedKey key1 = new NamespacedKey(plugin, "item1");
					if(!dataContainer.has(key1, PersistentDataType.STRING)) {
						player.sendMessage(message("no-repair"));
						return true;
					}
					//  No errors  \\
						Set<NamespacedKey> rawKeys = dataContainer.getKeys();
						for(NamespacedKey key: rawKeys) {
							String stringKey = key.toString();
							if(stringKey.startsWith("yourdurability:item")) {
								dataContainer.remove(key);
							}}
						player.sendMessage(message("remove-repair-success"));
				/*
				//	ydy remove durability  \\  
				*/
				}else if(args[1].equalsIgnoreCase("durability")){
					//  Catch errors  \\
					if(!newMeta.hasLore()) {
						player.sendMessage(message("no-ydy"));
						return true;
					}
					if(!loreHasDurability(newMeta.getLore())) {
						player.sendMessage(message("no-ydy"));
						return true;
					}
					//  No errors  \\
					List<String> newLore = newMeta.getLore();
					int loreIndex = 0;
					if(!Main.useFirstLine) loreIndex = newLore.size() - 1;
					newLore.remove(loreIndex);
					newMeta.setLore(newLore);
					player.sendMessage(message("remove-durability-success"));
				/*
				//	ydy remove line  \\  
				*/
				}else if(args[1].equalsIgnoreCase("line")){
					//  Catch errors  \\
					if(args.length <= 2) {
						player.sendMessage(message("missing-args"));
						player.sendMessage(message("remove-line-usage"));
						return true;
					}
					try {
						Integer.valueOf(args[2]);
					}catch(NumberFormatException exception){
						player.sendMessage(message("invalid-number"));
						player.sendMessage(message("remove-line-usage"));
						return true;
					}
					//  No errors  \\
					int loreIndex = Integer.valueOf(args[2])-1;
					List<String> newLore = newMeta.getLore();						
					newLore.remove(loreIndex);
					newMeta.setLore(newLore);
					player.sendMessage(message("remove-line-success")+ChatColor.RESET+" "+loreIndex);
				}else {
					// Invalid arguments
					player.sendMessage(message("invalid-args"));
					player.sendMessage(message("remove-usage"));
					return true;
				}
				item.setItemMeta(newMeta);
			}else {
				// Invalid arguments
				player.sendMessage(message("invalid-args"));
				player.sendMessage(message("main-usage"));
			}
		}
		return true;
		
	}
	public List<String> getNewLore(ItemMeta newMeta, int index, int current, int max){
		List<String> newLore = new ArrayList<String>();
		if(newMeta.hasLore()) {
			//Si ya tiene lore
			newLore = newMeta.getLore();
			if(newLore.get(index).contains(Main.preffix)) {
				//Si ya tiene YDY
				newLore.set(index,Main.preffix+" ["+current+"/"+max+"]");
			}else {
				//Si no tiene YDY
				if(!Main.useFirstLine) {
					index = index+1;}
				newLore.add(index,Main.preffix+" ["+current+"/"+max+"]");
			}
		}else {
			//Si no tiene lore
			newLore = Arrays.asList(Main.preffix+" ["+current+"/"+max+"]");;
		}
		return newLore;
	}
	public void setCommandRepairItems(PersistentDataContainer dataContainer, String[] args) {
		NamespacedKey key1 = new NamespacedKey(plugin, "item1");
		if(dataContainer.has(key1, PersistentDataType.STRING)) {
		//Si el item contiene al menos una llave de item YDY
			Set<NamespacedKey> rawKeys = dataContainer.getKeys();
			for(NamespacedKey key: rawKeys) {
			//Para cada llave en el item
				String stringKey = key.toString();
				if(stringKey.startsWith("yourdurability:item")) {
				//Eliminar cada llave YDY
					dataContainer.remove(key);}}}
		int itemNumber = 1;
		for(int i = 2; i < args.length; i++) {
			if(plugin.RepairItems.contains(args[i])) {
				NamespacedKey key = new NamespacedKey(plugin, "item"+itemNumber);
				dataContainer.set(key, PersistentDataType.STRING, args[i]);
				itemNumber++;
			}
		}
	}
	public String message(String path) {
		String mensaje = 
				Main.name+ChatColor.RESET+" "
				+ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Messages."+path));
		return mensaje;
	}
}
