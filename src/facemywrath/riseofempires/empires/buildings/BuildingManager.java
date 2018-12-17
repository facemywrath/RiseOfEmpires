package facemywrath.riseofempires.empires.buildings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sk89q.worldedit.data.DataException;

import facemywrath.riseofempires.empires.Empire;
import facemywrath.riseofempires.empires.buildings.brewery.Brewery;
import facemywrath.riseofempires.empires.buildings.buildzone.BuildZone;
import facemywrath.riseofempires.empires.buildings.home.Home;
import facemywrath.riseofempires.empires.buildings.library.Library;
import facemywrath.riseofempires.empires.buildings.mine.Mine;
import facemywrath.riseofempires.empires.buildings.outpost.Outpost;
import facemywrath.riseofempires.empires.buildings.shop.BuildingBlocks;
import facemywrath.riseofempires.empires.buildings.shop.Materials;
import facemywrath.riseofempires.empires.buildings.shop.Restaurant;
import facemywrath.riseofempires.empires.buildings.shop.ToolShop;
import facemywrath.riseofempires.empires.buildings.townhall.TownHall;
import facemywrath.riseofempires.main.Main;
import facemywrath.riseofempires.util.InvItem;
import net.md_5.bungee.api.ChatColor;

public class BuildingManager {

	private Empire emp;

	private List<Building> buildings = new ArrayList<>();

	public BuildingManager(Empire emp) {
		this.emp = emp;
		loadBuildings();
	}

	public void addBuilding(Building build)
	{
		buildings.add(build);
		this.getEmpire().broadcastToMembers("Building " + build.getName() + " placed.");
	}

	public void removeBuilding(Building build)
	{
		buildings.remove(build);
	}

	public void loadBuildings()
	{
		emp.getEmpireManager().getMain().getFileManager().checkFiles();
		if(emp.getConfig() != null)
		{
			FileConfiguration config = emp.getConfig();
			if(config.contains("Buildings") && config.getConfigurationSection("Buildings").getKeys(false).size() > 0)
			{
				for(String s : config.getConfigurationSection("Buildings").getKeys(false))
				{
					if(config.getConfigurationSection("Buildings." + s).getKeys(false).size() <1)
						continue;
					String name = config.getString("Buildings." + s + ".Type");
					switch(name)
					{
					case "Mine":
						try {
							Mine home = new Mine(this, Integer.parseInt(s));
							buildings.add(home);
							home.loadBlocks();
						} catch (NumberFormatException e3) {
							// TODO Auto-generated catch block
							e3.printStackTrace();
						}
						break;
					case "Town_Hall":
						try {
							if(this.getTownHall() == null)
							{
								TownHall home = new TownHall(this, 0);
								buildings.add(home);
								home.loadBlocks();
							}
						} catch (NumberFormatException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						break;
					case "Home":
						try {
							Home home = new Home(this, Integer.parseInt(s));
							buildings.add(home);
							home.loadBlocks();
						} catch (NumberFormatException | DataException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						break;
					case "BuildZone":
						try {
							BuildZone buildzone = new BuildZone(this, Integer.parseInt(s));
							buildings.add(buildzone);
						} catch (NumberFormatException | DataException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						break;
					case "Outpost":
						try {
							Outpost home = new Outpost(this, Integer.parseInt(s));
							buildings.add(home);
							home.loadBlocks();
						} catch (NumberFormatException | DataException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					}
				}
			}
		}
	}

	//Static Functions

	public static int getHighestId(BuildingManager bm)
	{
		FileConfiguration config = bm.getEmpire().getConfig();
		if(config.contains("Buildings"))
		{
			return config.getConfigurationSection("Buildings").getKeys(false).size();
		}
		return 0;
	}

	public static String getBuildingType(ItemStack item)
	{
		Main pl = (Main) Bukkit.getPluginManager().getPlugin("RiseOfEmpires");
		FileConfiguration config = pl.getFileManager().getSchematicsFile();
		for(String s : config.getKeys(false))
		{
			ArrayList<String> lore = new ArrayList<>();
			for(String s2 : config.getStringList(s + ".Item.Lore"))
			{
				lore.add(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', s2)));
			}
			ItemStack item2 = InvItem.invItem(Material.valueOf(config.getString(s + ".Item.Type")), ChatColor.translateAlternateColorCodes('&', config.getString(s + ".Item.Display")), lore);
			item = item.clone();
			item.setAmount(1);
			ItemMeta meta = item.getItemMeta();
			lore.clear();
			for(String s2 : meta.getLore())
			{
				lore.add(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', s2)));
			}
			if(lore.get(0).contains("Level"))
				lore.remove(0);
			meta.setLore(lore);
			item.setItemMeta(meta);
			if(item.equals(item2))
			{
				return s;
			}					
		}
		return null;
	}

	public static int getBuildingLevel(ItemStack item)
	{
		if(getBuildingType(item) != null)
		{
			String s = ChatColor.stripColor(item.getItemMeta().getLore().get(0));
			int i = 1;
			if(s.startsWith("Level"))
			{
				try{
					i = Integer.parseInt(s.substring(7, s.length()));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			return i;
		}
		else
		{
			return -1;
		}
	}

	public static ItemStack getBuildingItem(String str, int level)
	{
		Main pl = (Main) Bukkit.getPluginManager().getPlugin("RiseOfEmpires");
		FileConfiguration config = pl.getFileManager().getSchematicsFile();
		if(config.contains(str))
		{
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.translateAlternateColorCodes('&', "&5Level: " + level));
			for(String s : config.getStringList(str + ".Item.Lore"))
			{
				lore.add(ChatColor.translateAlternateColorCodes('&', s));
			}
			return InvItem.invItem(Material.getMaterial(config.getString(str + ".Item.Type")), config.getString(str + ".Item.Display"), lore);
		}
		return null;

	}


	public static ItemStack getBuildingItem(String str)
	{
		Main pl = (Main) Bukkit.getPluginManager().getPlugin("RiseOfEmpires");
		FileConfiguration config = pl.getFileManager().getSchematicsFile();
		if(config.contains(str))
		{
			List<String> lore = new ArrayList<String>();
			for(String s : config.getStringList(str + ".Item.Lore"))
			{
				lore.add(ChatColor.translateAlternateColorCodes('&', s));
			}
			return InvItem.invItem(Material.getMaterial(config.getString(str + ".Item.Type")), config.getString(str + ".Item.Display"), lore);
		}
		return null;

	}

	//Getters

	public TownHall getTownHall()
	{
		for(Building building : buildings)
		{
			if(building instanceof TownHall)
				return (TownHall) building;
		}
		return null;
	}

	public List<Mine> getMines()
	{
		List<Mine> goldmines = new ArrayList<>();
		for(Building building : buildings)
		{
			if(building instanceof Mine)
				goldmines.add((Mine) building);
		}
		return goldmines;
	}

	public List<Outpost> getOutposts()
	{
		List<Outpost> outposts = new ArrayList<>();
		for(Building building : buildings)
		{
			if(building instanceof Outpost)
				outposts.add((Outpost) building);
		}
		return outposts;
	}

	public List<Home> getHomes()
	{
		List<Home> homes = new ArrayList<>();
		for(Building building : buildings)
		{
			if(building instanceof Home)
				homes.add((Home) building);
		}
		return homes;
	}

	public List<Library> getLibraries()
	{
		List<Library> libraries = new ArrayList<>();
		for(Building building : buildings)
		{
			if(building instanceof Library)
				libraries.add((Library) building);
		}
		return libraries;
	}

	public List<Brewery> getBreweries()
	{
		List<Brewery> breweries = new ArrayList<>();
		for(Building building : buildings)
		{
			if(building instanceof Brewery)
				breweries.add((Brewery) building);
		}
		return breweries;
	}
	
	public List<Chunk> getBuildZoneChunks()
	{
		List<Chunk> chunks = new ArrayList<>();
		for(Building building : buildings)
		{
			if(building instanceof BuildZone)
				chunks.add(building.getLocation().getChunk());
		}
		return chunks;
	}
	
	public List<BuildZone> getBuildZones()
	{
		List<BuildZone> zones = new ArrayList<>();
		for(Building building : buildings)
		{
			if(building instanceof BuildZone)
				zones.add((BuildZone) building);
		}
		return zones;
	}
	
	public int getShopAmount()
	{
		int amt= 0;
		for(Building building : buildings)
		{
			if(building instanceof ToolShop || building instanceof Materials || building instanceof BuildingBlocks || building instanceof Restaurant)
				amt++;
		}
		return amt;
	}
	
	public BuildZone getBuildZoneByChunk(Chunk chunk)
	{
		for(Building building : buildings)
		{
			if(building instanceof BuildZone)
				if(building.getLocation().getChunk().equals(chunk))
					return (BuildZone) building;
		}
		return null;
	}

	public List<Building> getBuildings()
	{
		return this.buildings;
	}

	public Empire getEmpire()
	{
		return emp;
	}

}
