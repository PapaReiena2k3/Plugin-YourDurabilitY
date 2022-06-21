package reieno.ydurability.events;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import reieno.ydurability.Main;
import static reieno.ydurability.YdyMethods.getFromLore;
import static reieno.ydurability.YdyMethods.getNewMeta;

public class RepairInventory implements Listener{
	
	private Main plugin;
	public RepairInventory(Main plugin) {
		this.plugin = plugin;
	}
	
	public void crearInventarioReparar(HumanEntity jugador) {
		Inventory inv = Bukkit.createInventory(null, 27, Main.invName);
		inv.setItem(11, jugador.getInventory().getItem(jugador.getInventory().getHeldItemSlot()));
		jugador.getInventory().setItem(jugador.getInventory().getHeldItemSlot(), null);
		inv.setItem(10, Main.anvilRepair);
		inv.setItem(5, Main.yellowPane);
		inv.setItem(23, Main.yellowPane);
		inv.setItem(4, Main.grayPane);
		inv.setItem(13, Main.grayPane);
		inv.setItem(22, Main.grayPane);
		inv.setItem(6, Main.grayPane);
		inv.setItem(7, Main.grayPane);
		inv.setItem(8, Main.grayPane);
		inv.setItem(15, Main.grayPane);
		inv.setItem(24, Main.grayPane);
		inv.setItem(25, Main.grayPane);
		inv.setItem(26, Main.grayPane);
		inv.setItem(0, Main.blackPane);
		inv.setItem(1, Main.blackPane);
		inv.setItem(2, Main.blackPane);
		inv.setItem(3, Main.blackPane);
		inv.setItem(9, Main.blackPane);
		inv.setItem(12, Main.blackPane);
		inv.setItem(18, Main.blackPane);
		inv.setItem(19, Main.blackPane);
		inv.setItem(20, Main.blackPane);
		inv.setItem(21, Main.blackPane);
		inv.setItem(16, Main.glassPane);
		inv.setItem(17, Main.whitePane);
		jugador.openInventory(inv);
	}
	public void cambiarAMateriales(Inventory inv) {
		inv.setItem(10, Main.anvilMaterials);
//		inv.setItem(8, Main.grayPane);
		inv.setItem(17, Main.grayPane);
//		inv.setItem(26, Main.grayPane);
		inv.setItem(4, null);
		inv.setItem(5, null);
		inv.setItem(6, null);
		inv.setItem(7, null);
		inv.setItem(13, null);
		inv.setItem(14, null);
		inv.setItem(15, null);
		inv.setItem(16, null);
		inv.setItem(22, null);
		inv.setItem(23, null);
		inv.setItem(24, null);
		inv.setItem(25, null);
//		04 05 06 07   08
//		13 14 15 16   17
//		22 23 24 25   26
		Integer itemIndex = 4;
		ItemStack item = inv.getItem(11);
		PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
		FileConfiguration config = plugin.getRepairItems();
		FileConfiguration config2 = plugin.getConfig();
		ItemStack newItem;
		ItemMeta newMeta;		
		for(NamespacedKey key: dataContainer.getKeys()) {
			if(key.toString().startsWith("yourdurability:item")) {
				String repairItemID = dataContainer.get(key, PersistentDataType.STRING);
				Material material = Material.getMaterial(config.getString(repairItemID+".Material").toUpperCase());
				Integer amount; // float porque puede ser porcentual
				Integer cost;
				try {
					amount = Integer.valueOf(config.getString(repairItemID+".Amount"));
					cost = Integer.valueOf(config.getString(repairItemID+".Cost"));
				}catch(NumberFormatException e) {
					//errorID = 1;
					//No se pudo convertir el costo o monto
					continue;
				}
				String name = ChatColor.translateAlternateColorCodes('&', config.getString(repairItemID+".CustomName"));
				amount = Integer.valueOf(config.getString(repairItemID+".Amount"));
				cost = Integer.valueOf(config.getString(repairItemID+".Cost"));
				String sAmount;
				if(Boolean.valueOf(config.getString(repairItemID+".Percentage")) == true) {
					sAmount = amount+"%";}
				else {sAmount = amount+"";}
								
				newItem = new ItemStack(material);
				newMeta = newItem.getItemMeta();
				if(name != null && !name.equals("")) newMeta.setDisplayName(name);
				List<String> newLore = Arrays.asList(
						ChatColor.translateAlternateColorCodes('&',config2.getString("Inventory.materials-lore-amount"))+" "+sAmount,
						ChatColor.translateAlternateColorCodes('&',config2.getString("Inventory.materials-lore-cost"))+" "+cost
						
				);
				newMeta.setLore(newLore);
				newItem.setItemMeta(newMeta);
				inv.setItem(itemIndex, newItem);
				
				itemIndex += 1;
				if(itemIndex == 8 || itemIndex == 17) {
					itemIndex += 5;
				}
			}
		}
		
	}
	public void cambiarAReparar(Inventory inv) {
//		jugador.closeInventory(inv);
		inv.setItem(10, Main.anvilRepair);
		inv.setItem(5, Main.yellowPane);
		inv.setItem(23, Main.yellowPane);
		inv.setItem(4, Main.grayPane);
		inv.setItem(13, Main.grayPane);
		inv.setItem(22, Main.grayPane);
		inv.setItem(6, Main.grayPane);
		inv.setItem(7, Main.grayPane);
		inv.setItem(14, null);
		inv.setItem(15, Main.grayPane);
		inv.setItem(24, Main.grayPane);
		inv.setItem(25, Main.grayPane);
//		inv.setItem(26, Main.grayPane);
		inv.setItem(16, Main.glassPane);
		inv.setItem(17, Main.whitePane);
//		jugador.openInventory(inv);
	}
	@EventHandler
	public void clicInventario(InventoryClickEvent event) {
		if(event.getClickedInventory() != null && event.getView().getTitle().equals(Main.invName)) {
			Boolean isRepairInv = false;
			Inventory inv = event.getClickedInventory();
			InventoryAction accion = event.getAction();
			//event.getView()
			if(inv.getType().equals(InventoryType.CHEST)) {
				if(inv.getItem(10).equals(Main.anvilRepair)) isRepairInv = true;
				Integer slot = event.getSlot();
				if(slot == 14) {
					if(isRepairInv) {
						//calcular reparacion
						calcularReparacion(inv);
						return;
					}else {
						//cancelar
						event.setCancelled(true);
						return;
					}
				}else if(slot == 10) {
					//Cambiando de inventario
					event.setCancelled(true);
					if(isRepairInv) {
						//System.out.println("1");
						if(inv.getItem(14) != null) {
							//Devolver el material de reparacion
							Map<Integer, ItemStack> map;
							map = event.getWhoClicked().getInventory().addItem(inv.getItem(14));
							for (ItemStack item : map.values()) {
						       event.getWhoClicked().getWorld().dropItem(event.getWhoClicked().getLocation(), item);
						    }
						}
						cambiarAMateriales(inv);
						return;
					}else {
						cambiarAReparar(inv);
						//System.out.println("2");
						return;
					}
				}else if(slot == 16) {
					event.setCancelled(true);
					ItemStack result = event.getCurrentItem();
					if(inv.getItem(14) != null && result != null && result != Main.glassPane && result.getItemMeta().hasLore()) {
						Integer cost = Integer.valueOf(inv.getItem(17).getItemMeta().getDisplayName().substring(plugin.getConfig().getString("Inventory.repair-cost-name").length()+1));
						
						Player jugador = (Player) event.getWhoClicked();
						Integer nivel = jugador.getLevel();
						if(!jugador.getGameMode().equals(GameMode.CREATIVE)) {
							if(cost > nivel) return;				
							jugador.setLevel(nivel - cost);	
						}
						jugador.getWorld().playSound(jugador.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.5f);
						Integer restantes = inv.getItem(17).getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "remaining"), PersistentDataType.INTEGER);
						if(restantes > 0) {
							ItemStack i = inv.getItem(14).clone();
							i.setAmount(restantes);
							inv.setItem(14, i);
						}else inv.setItem(14, null);
						inv.setItem(11, result);
						inv.setItem(16, Main.glassPane);
			        	inv.setItem(17, Main.whitePane);
					}
					return;									
				}else {
					//Cualquier otra accion
					event.setCancelled(true);
					return;
				}
			}
			ItemStack item;
			Inventory inv2 = event.getView().getTopInventory();
			if(accion.equals(InventoryAction.COLLECT_TO_CURSOR)) {
				item = event.getCursor();
			}else if (accion.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)){				
				if(inv2.getItem(10).equals(Main.anvilMaterials)) {
					event.setCancelled(true);
					return;
				}else {
					item = event.getCurrentItem();
				}
			}else {
				return;
			}
			if(
				item.isSimilar(inv2.getItem(10)) ||
				item.isSimilar(Main.yellowPane) ||
				item.isSimilar(Main.grayPane) ||
				item.isSimilar(Main.blackPane) ||
				item.isSimilar(Main.glassPane) ||
				item.isSimilar(inv2.getItem(17))
				) {
				event.setCancelled(true);
			}else {
				if(inv2.getItem(10).equals(Main.anvilMaterials)) {
					return;
				}else {
					calcularReparacion(inv2);
				}
			}
		}
	}
	@EventHandler
	public void arrastraInventario(InventoryDragEvent event) {
		if(event.getView().getTitle().equals(Main.invName)) {
			Set<Integer> slots = event.getRawSlots();
			for(Integer id : slots) {
				if(id <= 26 && id != 14) {
					event.setCancelled(true);
					return;
				}
				Inventory inv = event.getView().getTopInventory();
				if(inv.getItem(10).equals(Main.anvilRepair)) {
					calcularReparacion(inv);
				}
			}			
		}
	}
	@EventHandler
	public void cerrarInventario(InventoryCloseEvent event) {
		if(event.getView().getTitle().equals(Main.invName)) {
			//System.out.println("close");
			Inventory inv = event.getView().getTopInventory();
			Boolean isRepairInv = false;
			if(inv.getItem(10).equals(Main.anvilRepair)) isRepairInv = true;
			Map<Integer, ItemStack> map;
			if(isRepairInv) {
				if(inv.getItem(14) != null) {
					//Devolver el item a reparar y material de reparacion
					 map = event.getPlayer().getInventory().addItem(inv.getItem(11), inv.getItem(14));
				}else {
					//Solo devoler el item a reparar
					map = event.getPlayer().getInventory().addItem(inv.getItem(11));
				}
			}else {
				//Solo devoler el item a reparar
				map = event.getPlayer().getInventory().addItem(inv.getItem(11));
			}
			
			for (ItemStack item : map.values()) {
	        	event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), item);
	        }
		}
	}
	
	public void calcularReparacion(Inventory inv) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				ItemStack item = inv.getItem(11);//El que se va a reparar
				ItemStack repair = inv.getItem(14);//El que se usará de ingrediente
				List<Integer> YDY = getFromLore(item.getItemMeta().getLore());
				if(YDY.get(0).equals(YDY.get(1))) {
					inv.setItem(16, Main.glassPane);
					inv.setItem(17, Main.whitePane);
					return;
				}
				if(repair != null) {
					PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
					FileConfiguration config = plugin.getRepairItems();
					
					for(NamespacedKey key: dataContainer.getKeys()) {
						if(key.toString().startsWith("yourdurability:item")) {
							//Es una clave de reparación
							String repairItemID = dataContainer.get(key, PersistentDataType.STRING);
							if(config.getString(repairItemID+".Material").equalsIgnoreCase(repair.getType().toString())) {
								Float amount; // float porque puede ser porcentual
								Integer cost;
								try {
									amount = Float.valueOf(config.getString(repairItemID+".Amount"));
									cost = Integer.valueOf(config.getString(repairItemID+".Cost"));
								}catch(NumberFormatException e) {
									//No se pudo convertir el costo o monto
									continue;
								}
								String name = ChatColor.translateAlternateColorCodes('&', config.getString(repairItemID+".CustomName"));
								if( ( (!name.equals(null) && !name.equals("")) || repair.getItemMeta().hasDisplayName()) &&
									!repair.getItemMeta().getDisplayName().equals(name) ) {
									continue;
								}
								amount = Float.valueOf(config.getString(repairItemID+".Amount"));
								cost = Integer.valueOf(config.getString(repairItemID+".Cost"));
								amount = -amount; // no me acuerdo xd
								
								if(Boolean.valueOf(config.getString(repairItemID+".Percentage")) == true)
									amount = (YDY.get(1) * amount / 100);
								
								Float totalAmount = amount * repair.getAmount();
								Integer cantidadRestante = 0;
								if(repair.getAmount() > 1) {										
									Float a = YDY.get(1) - YDY.get(0) + totalAmount;
									if(a > amount) {
										cost = cost * repair.getAmount();														
									}else {
										//monto se pasa de lo necesario
										a = a - (a % amount);
										cantidadRestante = (int) (a/amount);
										//cantidad que debe permanecer despues de la reparacion
										cost = cost * (repair.getAmount() - cantidadRestante);
									}
								}												
								Integer intAmount = Math.round(totalAmount);
								if(YDY.get(0) - intAmount > YDY.get(1)) {
									intAmount = 0;
									YDY.set(0,YDY.get(1));}//Primer numero mayor que el segundo, se vuelven iguales
								ItemStack result = item.clone();
								result.setItemMeta(getNewMeta(item, YDY, intAmount));
								inv.setItem(16, result);
								
								result = inv.getItem(17);
								ItemMeta newMeta = result.getItemMeta();
								PersistentDataContainer container = newMeta.getPersistentDataContainer();
								container.set(new NamespacedKey(plugin, "remaining"), PersistentDataType.INTEGER, cantidadRestante);
								newMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Inventory.repair-cost-name"))+ " "+cost);
								result.setItemMeta(newMeta);
								return;
							}
						}
					}
					inv.setItem(16, Main.glassPane);
					inv.setItem(17, Main.whitePane);
					
				}else {
					inv.setItem(16, Main.glassPane);
					inv.setItem(17, Main.whitePane);
				}
			}
		}, 0L);
	}

	/*
	 if(repair.getAmount() > 1) {
		//Ajustar cuando hay mas de un item de reparación
		Integer cantidadRestante = 0;
		Float a = YDY.get(1) - YDY.get(0) + totalAmount;
		if(a > amount) {
			cost = cost * repair.getAmount();														
		}else {
			//monto se pasa de lo necesario
			a = a - (a % amount);
			if(a > 0) {
				cantidadRestante = (int) (a/amount);
				//cantidad que debe permanecer despues de la reparacion
			}
		}
	}
	*/
	
}
