package reieno.ydurability;

import java.util.Arrays;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;


public class YdyMethods{
	
	@SuppressWarnings("unused")
	private Main plugin;
	public YdyMethods(Main plugin) {
		this.plugin = plugin;
	}
	
	public static Boolean loreHasDurability(List<String> L) {//M�todo para saber si el lore tiene durabilidad YDY
		if(L != null) {
			if(Main.useFirstLine) {//Si la configuraci�n dice usar la primera l�nea
				if(L.get(0).contains(Main.preffix)) { return true; //Si la primera linea contiene el prefijo YDY, retorna verdadero
				}else { return false;} //Sino, retorna falso
			}else {//Si no se usa la primera l�nea
				if(L.get(L.size() - 1).contains(Main.preffix)) { return true; //Si la ultima linea contiene el prefijo YDY, retorna verdadero
				}else {	return false;} //Sino, retorna falso
			}
		}else {
			return false;
		}}	
	public static List<Integer> getFromLore(List<String> lore){//M�todo para obtener YDY actual y m�xima (a,m)
		String string;//Variable cadena de texto string
		if(Main.useFirstLine) {//Si se usa la primera l�nea
			string = (lore.get(0).split(" ", 2))[1];//Obtiene el prefijo en la primera l�nea Ej: "[20/50]"
		}else {//Si no se usa la primera l�nea
			string = (lore.get(lore.size() - 1).split(" ", 2))[1];//Obtiene el prefijo en la �ltima l�nea Ej: "[20/50]"
		}
		String[] durability = string.substring(1,string.length()-1).split("/",2);//Divide string en 2 cadenas de texto Ej: "20" y "50"
		List<Integer> YDY = Arrays.asList(0,0);//Lista YDY por defecto (0,0)
		YDY.set(0, Integer.valueOf(durability[0]));//Estable primer elemento de A como el valor entero del primer texto Ej: 20
		YDY.set(1, Integer.valueOf(durability[1]));//Lo mismo para el segundo elemento y texto Ej: 50
		//YDY contiene la durabilidad actual y m�xima del lore Ej: (20,50)
		return YDY;
	}
	public static ItemMeta getNewMeta(ItemStack item, List<Integer> YDY, Integer damage) {//M�todo aplicar cierto da�o a la durabilidad YDY
		ItemMeta newMeta = item.getItemMeta();//Copia y guarda un nuevo ItemMeta
		List<String> lore = newMeta.getLore();
		Short maxDurability = item.getType().getMaxDurability();
		if(Main.useFirstLine) {//Si se usa la primera l�nea
	        	lore.set(0,Main.preffix+" ["+(YDY.get(0)-damage)+"/"+YDY.get(1)+"]");//Resta durabilidad en la primera l�nea del lore
	        }else {//Si no se usa la primera l�nea
	        	lore.set((lore.size() - 1),Main.preffix+" ["+(YDY.get(0)-damage)+"/"+YDY.get(1)+"]");}//Resta durabilidad en la �ltima l�nea del lore
		newMeta.setLore(lore);
		Integer realDamage;//Nueva variable n�mero entero
		Float int1;
		int1 = ((maxDurability*(YDY.get(0)-damage)/(float)(YDY.get(1))));
	    realDamage = maxDurability - Math.round(int1);
	    //if(damage <= 0 ||YDY.get(0)
	    /*if(YDY.get(0) <= damage) ((Damageable) newMeta).setDamage(realDamage);
		else ((Damageable) newMeta).setDamage(realDamage-damage);*/
	    if(YDY.get(0) <= damage) ((Damageable) newMeta).setDamage(maxDurability); //da�o letal
		else ((Damageable) newMeta).setDamage(realDamage);
	    return newMeta;
	}
}
