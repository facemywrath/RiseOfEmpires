package facemywrath.riseofempires.util;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

public class FormatLore {
	
	public static List<String> formatLore(String str)
	{
		List<String> lore = new ArrayList<>();
		for(String str2 : str.split("::"))
		{
			lore.add(ChatColor.translateAlternateColorCodes('&', str2));
		}
		return lore;
	}

}
