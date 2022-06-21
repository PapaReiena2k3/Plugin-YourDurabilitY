package reieno.ydurability.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import reieno.ydurability.Main;
import static reieno.ydurability.YdyMethods.*;//loreHasDurability setNewMeta getFromLore


public class MaincmdTab implements TabCompleter{
	private Main plugin;
	public MaincmdTab(Main plugin) {
		this.plugin = plugin;
	}
	private static final List<String> COMMANDS = Arrays.asList("set","get","remove","reload","version");
	private static final List<String> SET = Arrays.asList("20/20","50/50","100/100");
	private static final List<String> REMOVE = Arrays.asList("durability","repair","line");
	private static final List<String> GET = Arrays.asList("item","repair");
	
public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
       List<String> completions = new ArrayList<>();
       List<String> commands = new ArrayList<>();
       if (args.length == 1) {
    	   commands = COMMANDS;
    	   StringUtil.copyPartialMatches(args[0], commands, completions);
       }else if (args.length == 2) {
        	if (args[0].equalsIgnoreCase("set")) {
        		if(sender instanceof Player) {
        			ItemStack item = ((Player) (sender)).getEquipment().getItemInMainHand();
        			if(item != null && item.getItemMeta() != null && item.getItemMeta().hasLore() && loreHasDurability(item.getItemMeta().getLore())) {
        				List<Integer> durability = getFromLore(item.getItemMeta().getLore());
        				String actual = durability.get(0)+"/"+durability.get(1);
        				commands.add(actual);
        			}else {
            			commands.addAll(SET);
            		}
        		}
        	}else if (args[0].equalsIgnoreCase("remove")) {
        		commands = REMOVE;
        	}else if(args[0].equalsIgnoreCase("get")) {
        		commands = GET;
        	}
        	StringUtil.copyPartialMatches(args[1], commands, completions);
       }else if(args.length > 2) {
    	   if(args[0].equalsIgnoreCase("set") || (args[0].equalsIgnoreCase("get") && args[1].equalsIgnoreCase("item"))) {
    		   commands = plugin.RepairItems;
        	   StringUtil.copyPartialMatches(args[args.length-1], commands, completions);    		   
    	   }
        }
        return completions;
	}

}
