package reieno.ydurability;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import reieno.ydurability.commands.MaincmdTab;
import reieno.ydurability.commands.Usercmd;
import reieno.ydurability.commands.Maincmd;
import reieno.ydurability.events.DispenserEvents;
import reieno.ydurability.events.PlayerEvents;
import reieno.ydurability.events.RepairInventory;

public class Main extends JavaPlugin{
	public String rutaConfig;
	PluginDescriptionFile pdffile = getDescription();
	public String version = pdffile.getVersion();
	
	FileConfiguration customConfig;
	public List<String> RepairItems = new ArrayList<String>();
	
	public static String name = "";
	public static String preffix = "";
	public static String invName = "";
	public static boolean useFirstLine = true;
	//public static boolean renameWhileRepair = true;
	public static ItemStack glassPane;
	public static ItemStack blackPane;
	public static ItemStack grayPane;
	public static ItemStack yellowPane;
	public static ItemStack whitePane;
	public static ItemStack vanillaError;
	public static ItemStack anvilRepair;
	public static ItemStack anvilMaterials;
	
	@Override
	public void onEnable() {
		registerEvents();
		registerCommands();
		registerConfig();
		registerRepairItems();
	}
	public void registerEvents() {
		PluginManager eventos = getServer().getPluginManager();
		eventos.registerEvents(new PlayerEvents(this),this);
		eventos.registerEvents(new DispenserEvents(this),this);
		eventos.registerEvents(new RepairInventory(this),this);
	}
	public void registerCommands() {
		this.getCommand("ydy").setExecutor(new Maincmd(this));
		this.getCommand("ydy").setTabCompleter(new MaincmdTab(this));
		this.getCommand("repairitems").setExecutor(new Usercmd(this));
	}	
	public void registerConfig() {
		File configuration = new File(this.getDataFolder(),"config.yml");
		//rutaConfig = configuration.getPath();
		if(!configuration.exists()) {
			this.getConfig().options().copyDefaults(true);
			saveDefaultConfig();
		}
		Main.preffix = line("preffix-format")+line("color-durability");
		Main.name = line("Messages.preffix-plugin")+ChatColor.RESET;
		if(this.getConfig().getString("first-lore-line").equalsIgnoreCase("false")) {
			useFirstLine = false;}
		//if(this.getConfig().getString("rename-while-repair").equalsIgnoreCase("false")) {
		//	renameWhileRepair = false;}
		Main.invName = line("Inventory.inventory-name");
		
		ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		ItemMeta newMeta = item.getItemMeta();
		newMeta.setDisplayName(line("Inventory.vanilla-name"));
		newMeta.setLore(list("Inventory.vanilla-lore"));
		item.setItemMeta(newMeta);
		vanillaError = item;
		
		item = new ItemStack(Material.ANVIL);
		newMeta = item.getItemMeta();
		newMeta.setDisplayName(line("Inventory.repair-info-name"));
		newMeta.setLore(list("Inventory.repair-info-lore"));
		item.setItemMeta(newMeta);
		anvilRepair = item;
		
		item = new ItemStack(Material.ANVIL);
		newMeta = item.getItemMeta();
		newMeta.setDisplayName(line("Inventory.materials-info-name"));
		newMeta.setLore(list("Inventory.materials-info-lore"));
		item.setItemMeta(newMeta);
		anvilMaterials = item;
		
		item = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
		newMeta = item.getItemMeta();
		newMeta.setDisplayName(line("Inventory.repair-cost-name")+ " 0");
		item.setItemMeta(newMeta);
		whitePane = item;
		
		item = new ItemStack(Material.GLASS_PANE);
		newMeta = item.getItemMeta();
		newMeta.setDisplayName(" ");
		item.setItemMeta(newMeta);
		glassPane = item;
		
		item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		newMeta = item.getItemMeta();
		newMeta.setDisplayName(" ");
		item.setItemMeta(newMeta);
		blackPane = item;
		
		item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		newMeta = item.getItemMeta();
		newMeta.setDisplayName(" ");
		item.setItemMeta(newMeta);
		grayPane = item;
		
		item = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
		newMeta = item.getItemMeta();
		newMeta.setDisplayName(" ");
		item.setItemMeta(newMeta);
		yellowPane = item;
		
        /*ConfigurationSection sec = Main.getPlugin().getConfig().getConfigurationSection("items");
        for(String key : sec.getKeys(false)){
            String name = Main.getPlugin().getConfig().getString("items." + key + ".name");
        }*/
		
	}
    public void registerRepairItems() {
    	File customConfigFile;
        customConfigFile = new File(getDataFolder(), "repairitems.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("repairitems.yml", false);
         }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        
        Set<String> setKeys = this.getRepairItems().getKeys(false);
        for (String k : setKeys) {
        	RepairItems.add(k);
        }
        
    }
	public String line(String path) {
		String mensaje = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString(path));
		return mensaje;
	}
	public List<String> list(String path) {
		
		List<String> list = this.getConfig().getStringList(path);
		//System.out.println(list);
		List<String> returnList = new ArrayList<String>(); 
		for (String s : list) {
			returnList.add(ChatColor.translateAlternateColorCodes('&', s));			
		}
		return returnList;
	}
    public FileConfiguration getRepairItems() {
        return this.customConfig;
    }
   
    //Referenciar este archivo de configuracion con:
    //plugin.getCustomConfig().getString("some-path");
    //plugin.getrepairItems()
}
