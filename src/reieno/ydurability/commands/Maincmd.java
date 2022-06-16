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
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("version")) {
					player.sendMessage(message("Messages.version")+" "+ChatColor.RESET+plugin.version);
				}else if(args[0].equalsIgnoreCase("reload")) {
					plugin.reloadConfig();
					plugin.registerConfig();
					plugin.registerRepairItems();
					player.sendMessage(message("Messages.reload"));
					
				}else if(args[0].equalsIgnoreCase("set")) {
					//player.sendMessage(plugin.preffix);
					ItemStack item = player.getInventory().getItemInMainHand();
					int error = setCommandError(args, item);
					// 0 : no errors
					// 1 : invalid item
					// 2 : missing arguments
					// 3 : invalid integer
					// 4 : Integer2 is greathen
					if(error == 0) {
						//continue
						int loreIndex = 0;
						if(!Main.useFirstLine && item.getItemMeta().hasLore()) {
							loreIndex = (byte) (item.getItemMeta().getLore().size()-1);}
						String[] words = (args[1].split("/", 3));
						int currentDurability = Short.valueOf(words[0]);
						int maxDurability = Short.valueOf(words[1]);
						ItemMeta newMeta = item.getItemMeta();
						int totalRealDurability = item.getType().getMaxDurability();
						List<String> newLore = setCommandNewLore(newMeta, loreIndex, currentDurability, maxDurability);
						newMeta.setLore(newLore);
						if(args.length > 2) {
							PersistentDataContainer dataContainer = newMeta.getPersistentDataContainer();
							setCommandRepairItems(dataContainer, args);
						}
						int rA;
						if(currentDurability<=0) {
				        	rA = totalRealDurability;
				        }else {
				        	rA = totalRealDurability-((totalRealDurability*(currentDurability)/maxDurability));}
						((Damageable) newMeta).setDamage(rA);
						item.setItemMeta(newMeta);
						player.sendMessage(message("Messages.set-success"));
					}else {
						switch(error) {
						case 1:	player.sendMessage(message("Messages.set-invalid-item"));break;
						case 2:	player.sendMessage(message("Messages.set-missing-args"));break;
						case 3:	player.sendMessage(message("Messages.set-invalid-int"));break;
						case 4:	player.sendMessage(message("Messages.set-int2-greater"));break;
						}
					}
				}else if(args[0].equalsIgnoreCase("get")){
					ItemStack item = player.getInventory().getItemInMainHand();
					int cmdCase = getCommandCase(item, args);
					// 1 : null item
					// 2 : item with no keys
					// 3 : invalid
					// 4 : missing
					// 5 : get repair
					// 6 : get item [item]
					if(cmdCase >= 5) {
						if(cmdCase == 5) {
							PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
							List<String> stringKeys = new ArrayList<String>();
							Set<NamespacedKey> rawKeys = dataContainer.getKeys();
							for(NamespacedKey key: rawKeys) {
								String stringKey = key.toString();
								if(stringKey.startsWith("yourdurability:item")) {
									stringKeys.add(dataContainer.get(key, PersistentDataType.STRING));
								}
							}
							player.sendMessage(message("Messages.get-repair-success")+" "+ChatColor.GREEN+stringKeys);
						}else {
							Boolean NoItem = true;
							for (String RepairItem : plugin.RepairItems) {
								if(RepairItem.equals(args[2])) {
									NoItem = false;
									FileConfiguration config = plugin.getRepairItems();
									Material material = Material.getMaterial(config.getString(RepairItem+".Material").toUpperCase());
									String name = ChatColor.translateAlternateColorCodes('&', config.getString(RepairItem+".CustomName"));
									
									ItemStack newItem = new ItemStack(material);
									ItemMeta newMeta = newItem.getItemMeta();
									newMeta.setDisplayName(name);
									newItem.setItemMeta(newMeta);
									player.getInventory().addItem(newItem);
									player.sendMessage(message("Messages.get-item-success"));
									break;
								}
							}if(NoItem) {
								player.sendMessage(message("Messages.invalid-item"));
							}
						}
					}else {
						switch(cmdCase) {
						case 1:	player.sendMessage(message("Messages.no-item"));break;
						case 2: player.sendMessage(message("Messages.no-repair"));break;
						case 3:	player.sendMessage(message("Messages.get-invalid-args"));break;
						case 4:	player.sendMessage(message("Messages.get-missing-args"));break;
						}
					}
				}else if(args[0].equalsIgnoreCase("remove")){
					ItemStack item = player.getInventory().getItemInMainHand();
					int commandCase = removeCommandCase(item, args);
					// 1 : item null
					// 2 : wrong usage
					// 3 : missing arguments
					// 4 : remove repair
					// 5 : remove durability
					if(commandCase >= 4) {
						ItemMeta newMeta = item.getItemMeta();
						if(commandCase == 4) {
							PersistentDataContainer dataContainer = newMeta.getPersistentDataContainer();
							NamespacedKey key1 = new NamespacedKey(plugin, "item1");
							if(dataContainer.has(key1, PersistentDataType.STRING)) {
							//Si el item contiene al menos una llave de item YDY
								Set<NamespacedKey> rawKeys = dataContainer.getKeys();
								for(NamespacedKey key: rawKeys) {
								//Para cada llave en el item
									String stringKey = key.toString();
									if(stringKey.startsWith("yourdurability:item")) {
									//Eliminar cada llave YDY
										dataContainer.remove(key);}}
								player.sendMessage(message("Messages.remove-success1"));
							}else {
								player.sendMessage(message("Messages.no-repair"));
							}
						}else {
							if(newMeta.hasLore()) {
								//Si ya tiene lore
								List<String> newLore = newMeta.getLore();
								int loreIndex = 0;
								if(!Main.useFirstLine && item.getItemMeta().hasLore()) {
									loreIndex = item.getItemMeta().getLore().size()-1;}
								if(newLore.get(loreIndex).contains(Main.preffix)) {
									newLore.remove(loreIndex);
									newMeta.setLore(newLore);
									player.sendMessage(message("Messages.remove-success2"));
								}else {
									player.sendMessage(message("Messages.no-ydy"));
								}
							}else {
								//No tiene lore
								player.sendMessage(message("Messages.no-ydy"));
							}
						}
						item.setItemMeta(newMeta);
					}else {
						switch(commandCase) {
						case 1:	player.sendMessage(message("Messages.no-item")); break;
						case 2:	player.sendMessage(message("Messages.remove-invalid-args")); break;
						case 3:	player.sendMessage(message("Messages.remove-missing-args")); break;
						}
					}
				}else {
					player.sendMessage(message("Messages.main-ussage"));
				}
			}else {
				player.sendMessage(message("Messages.main-ussage"));
			}
		}
		return true;
		
	}
	public int setCommandError(String[] args, ItemStack item) {
		//int setError = setCommandError(args[1], item);
		int errorNumber = 0;
		int currentDurability;
		int maxDurability;
		
		if(item == null || item.getType().getMaxDurability() <= 0) {
			errorNumber = 1;
		}else {
			try {
				String[] words = (args[1].split("/", 3));
				currentDurability = Short.valueOf(words[0]);
				maxDurability = Short.valueOf(words[1]);
			}catch(ArrayIndexOutOfBoundsException exception) {
				errorNumber = 2;
			}catch(NumberFormatException exception){
				errorNumber = 3;}
		}if(errorNumber == 0) {
			String[] words = (args[1].split("/", 3));
			currentDurability = Short.valueOf(words[0]);
			maxDurability = Short.valueOf(words[1]);
			if(currentDurability > maxDurability) {
				errorNumber = 4;
			}
		}
		return errorNumber;		
	}
	public List<String> setCommandNewLore(ItemMeta newMeta, int index, int current, int max){
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
		Integer RepairItems = args.length - 2;
		if(dataContainer.has(key1, PersistentDataType.STRING)) {
		//Si el item contiene al menos una llave de item YDY
			Set<NamespacedKey> rawKeys = dataContainer.getKeys();
			for(NamespacedKey key: rawKeys) {
			//Para cada llave en el item
				String stringKey = key.toString();
				if(stringKey.startsWith("yourdurability:item")) {
				//Eliminar cada llave YDY
					dataContainer.remove(key);}}}
		for(int i = 1; i <= RepairItems; i++) {
			NamespacedKey key = new NamespacedKey(plugin, "item"+i);
			dataContainer.set(key, PersistentDataType.STRING, args[i+1]);
		}
	}
	public int getCommandCase(ItemStack item, String[] args) {
		int caseNumber = 0;
		
		if(args.length > 1) {
			if (args[1].equalsIgnoreCase("repair")) {
				caseNumber = 5;
				
				if(item == null || item.getItemMeta() == null) {
					caseNumber = 1;
				}else {
				PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
				NamespacedKey key1 = new NamespacedKey(plugin, "item1");
				if (!dataContainer.has(key1, PersistentDataType.STRING)) {
					caseNumber = 2;}
				}
				
			}else if (args[1].equalsIgnoreCase("item")) {
				caseNumber = 6;
				if(args.length <= 2) caseNumber = 4;
			}else {
				caseNumber = 3;
			}
		}else {
			caseNumber = 4;
		}

		return caseNumber;
	}
	public int removeCommandCase(ItemStack item, String[] args) {
		int caseNumber = 0;
		if(item == null || item.getItemMeta() == null) {
			caseNumber = 1;
		}else {
			if(args.length > 1) {
				if(args[1].equalsIgnoreCase("repair")) {
					caseNumber = 4;
				}else if(args[1].equalsIgnoreCase("durability")){
					caseNumber = 5;
				}else {
					caseNumber = 2;
				}
			}else {
				caseNumber = 3;
			}
			
				
		}
		return caseNumber;
	}
	public String message(String path) {
		String mensaje = Main.name+ChatColor.RESET+" "+ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path));
		return mensaje;
	}
}
