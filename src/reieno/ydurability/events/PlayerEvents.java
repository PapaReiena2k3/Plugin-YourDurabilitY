package reieno.ydurability.events;

//loreHasDurability setNewMeta getFromLore
import static reieno.ydurability.YdyMethods.getFromLore;
import static reieno.ydurability.YdyMethods.loreHasDurability;
import static reieno.ydurability.YdyMethods.getNewMeta;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import reieno.ydurability.Main;

public class PlayerEvents implements Listener{
	private Main plugin;
	public PlayerEvents(Main plugin) {
		this.plugin = plugin;
	}
	
		
	@EventHandler
	public void itemDamage(PlayerItemDamageEvent event) {
		ItemStack item = event.getItem();//Guardar el item del evento
		if(item.getItemMeta().hasLore()) {//Si item tiene lore
			ItemMeta meta = item.getItemMeta();
			if(loreHasDurability(meta.getLore())) {//Si el lore del item tiene durabilidad YDY
				Integer damage = event.getDamage();//Guardar en damage puntos de durabilidad que gatará el item
				List<Integer> YDY = getFromLore(meta.getLore());
				if(YDY.get(0) > damage) {
					event.setCancelled(true);
				}
				item.setItemMeta(getNewMeta(item, YDY, damage));
			}
		}
	}
	@EventHandler
	public void itemMending(PlayerItemMendEvent event) {
		//reparación por medio de este encantamiento
		ItemStack item = event.getItem();
		if(item != null && item.getItemMeta().hasLore()) {
			ItemMeta meta = item.getItemMeta();
			if(loreHasDurability(meta.getLore())) {
				event.setCancelled(true);
				Integer repair = event.getRepairAmount() * -1;
				List<Integer> YDY = getFromLore(meta.getLore());
				if(YDY.get(0) - repair > YDY.get(1)) {
					repair = 0;
					YDY.set(0,YDY.get(1));
				}
				if(repair > 0) {
					item.setItemMeta(getNewMeta(item, YDY, repair));
				}
			}
		}
	}
	
	@EventHandler
	public void anvilRepairPrevent(PrepareAnvilEvent event) {
		//HumanEntity player = event.getView().getPlayer();
		AnvilInventory inventory = event.getInventory();
		ItemStack item = inventory.getItem(0);
		ItemStack repair = inventory.getItem(1);
		
		if(item != null && repair != null && item.hasItemMeta() && loreHasDurability(item.getItemMeta().getLore())) {
			//Primer item tiene durabilidad YDY y hay segundo item
			if(repair.getType().equals(Material.ENCHANTED_BOOK)) {
				return;
			}else {
				//Segundo item no es libro encantado
				inventory.setRepairCost(0);
				event.setResult(Main.vanillaError);
			}
		}else if (repair != null && repair.hasItemMeta() && loreHasDurability(repair.getItemMeta().getLore())){
			//Segundo item tiene durabilidad YDY
			inventory.setRepairCost(0);
			event.setResult(Main.vanillaError);
		}		
	}
	@EventHandler
	public void anvilDragFix(InventoryDragEvent event) {
		if(event.getInventory().getType().equals(InventoryType.ANVIL)) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					AnvilInventory inventory = (AnvilInventory) event.getInventory();					
					if(inventory.getItem(1) != null && inventory.getItem(2) != null && inventory.getItem(2).equals(Main.vanillaError)) {
						ItemStack backup = inventory.getItem(1);
						inventory.setRepairCost(0);
						inventory.setItem(1, backup);
					}
				}
			}, 0L);
		}
	}
	@EventHandler
	public void openInventory(PlayerInteractEvent event) {
		if(event.getClickedBlock() != null && event.getClickedBlock().getType().toString().endsWith("ANVIL") && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			//Clic derecho a un yunque
			ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
			if(item != null && item.hasItemMeta() && loreHasDurability(item.getItemMeta().getLore())) {
				event.setCancelled(true);
				PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
				if(dataContainer.has(new NamespacedKey(plugin, "item1"), PersistentDataType.STRING)) {
					//Tiene items de reparación
					RepairInventory inv = new RepairInventory(plugin);
					inv.crearInventarioReparar(event.getPlayer());					
				}else {
					event.getPlayer().sendMessage(Main.name+ChatColor.RESET+" "+ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Messages.no-repair")));
					//No tiene items de reparación
				}
			}
		}
		
	}
/*	public void anvilRepairClick(InventoryClickEvent event) {
		if(event.getInventory().getType().equals(InventoryType.ANVIL)) {
			//System.out.println("Click");
			AnvilInventory inventory = (AnvilInventory) event.getInventory();
			if(event.getSlotType().equals(SlotType.RESULT)) {
				ItemStack item = inventory.getItem(2);
				if(item != null && item.hasItemMeta() && item.equals(Main.anvilItem)){
					inventory.setRepairCost(0);
					event.setCancelled(true);
				}
			}*/
			/*if(event.getSlotType().equals(SlotType.CRAFTING) || event.getSlotType().equals(SlotType.RESULT)) {
				if(event.getSlotType().equals(SlotType.RESULT) && event.getCurrentItem() != null && event.getCurrentItem().getType().toString().endsWith("GLASS_PANE")) {
					event.setCancelled(true);
					
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						if(event.isCancelled()) {
							System.out.println("0");
							//Solo aquí se calcula el item resultado
							ItemStack item = inventory.getItem(0);//El que se va a reparar
							ItemStack repair = inventory.getItem(1);//El que se usará de ingrediente
							if(item != null && item.hasItemMeta() && repair != null && repair.hasItemMeta()) {
								// VERIFICAR Y CANCELAR CUANDO EL SEGUNDO ITEM TIENE YDY
								// REPARACION SÍ PUEDE SER UN LIBRO ENCANTADO Y NO LO INTERRUMPIRA ESTE SISTEMA
								System.out.println("1");
								PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
								if(dataContainer.has(new NamespacedKey(plugin, "item1"), PersistentDataType.STRING)) {
									System.out.println("2");
									//Tiene items de reparación
									List<Integer> YDY = getFromLore(item.getItemMeta().getLore());
									if(YDY.get(1) != YDY.get(0)) {
										System.out.println("3");
										//La durabilidad no está completa
										Integer NumeroError = 0;
										FileConfiguration config = plugin.getRepairItems();
										for(NamespacedKey key: dataContainer.getKeys()) {
											NumeroError = 0;
											//Para cada clave del primer item
											if(key.toString().startsWith("yourdurability:item")) {
												//Es una clave de reparación
												String repairItemID = dataContainer.get(key, PersistentDataType.STRING);
												if(config.getString(repairItemID+".Material").equalsIgnoreCase(repair.getType().toString())) {
													//El material coincide
													Boolean NoErrors = true;
													Float amount; // float porque puede ser porcentual
													Integer cost;
													try {
														amount = Float.valueOf(config.getString(repairItemID+".Amount"));
														cost = Integer.valueOf(config.getString(repairItemID+".Cost"));
													}catch(NumberFormatException e) { NoErrors = false;}
													if(NoErrors) {
														//No hay errores de numero incorrecto
														String name = ChatColor.translateAlternateColorCodes('&', config.getString(repairItemID+".CustomName"));
														if(!name.equals(null) && !name.equals("") && !name.equals(repair.getItemMeta().getDisplayName())) NoErrors = false;
														if(NoErrors) {
															//El nombre del ingrediente es correcto
															amount = Float.valueOf(config.getString(repairItemID+".Amount"));
															cost = Integer.valueOf(config.getString(repairItemID+".Cost"));
															amount = -amount; // no me acuerdo xd
															if(Boolean.valueOf(config.getString(repairItemID+".Percentage")) == true) amount = (YDY.get(1) * amount / 100);
															// cambiado a pocentaje
															Float totalAmount = amount * repair.getAmount();
															if(repair.getAmount() > 1) {
																//Ajustar cuando hay mas de un item de reparación
																NoErrors = false;
																if((YDY.get(1) - YDY.get(0) + totalAmount) > amount) {
																	cost = cost * repair.getAmount();
																	NoErrors = true;}}
															if(NoErrors) {
																//Monto no se pasa de lo necesario
																Integer intAmount = Math.round(totalAmount);
																if(YDY.get(0) - intAmount > YDY.get(1)) {
																	intAmount = 0;
																	YDY.set(0,YDY.get(1));}//Primer numero mayor que el segundo, se vuelven iguales
																ItemStack result = item.clone();
																ItemMeta newMeta = getNewMeta(result, YDY, intAmount);
																//if(Main.renameWhileRepair)
																newMeta.setDisplayName(inventory.getRenameText());	
																result.setItemMeta(newMeta);
																inventory.setItem(2, result);
																inventory.setRepairCost(cost);
																break;
															}else NumeroError = 3; //Demasiados items reparacion
														}else NumeroError = 1; //Item incorrecto
													}else NumeroError = 2; //Error monto o costo
												}else NumeroError = 1; //Item incorrecto
											}//else NumeroError = 1; //Sin items de reparación
										}
										//fin bucle
										if(NumeroError != 0) {
											if(NumeroError == 1) inventory.setItem(2, glassPane(3, "&fItem de reparación &eincorrecto&f."));
											else if(NumeroError == 2) inventory.setItem(2, glassPane(3, "&eError de configuración&f monto o costo incorrrectos."));
											else inventory.setItem(2, glassPane(2, "&eDemasiados&f items de reparación en yunque."));
											inventory.setRepairCost(0);
										}
									}
								}
							}else {
								inventory.setItem(2, glassPane(3, "&fItem de reparación &eincorrecto&f."));
							}
						}else if(inventory.getItem(0) != null && inventory.getItem(1) != null && inventory.getItem(0).hasItemMeta()
						&& loreHasDurability(inventory.getItem(0).getItemMeta().getLore())){
							inventory.setItem(2, glassPane(1, "&eClic aquí&f para reparar y renombrar."));
							inventory.setRepairCost(0);
						}
					}
				}, 0L);
			}*/
		
	

}

