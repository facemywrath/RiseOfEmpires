package facemywrath.riseofempires.empires.buildings;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;

import facemywrath.riseofempires.empires.buildings.townhall.TownHall;

public class CoreBlock implements Listener {

	private Building building;
	private Location loc;
	private Inventory inv;

	public CoreBlock(Building building, Location loc)
	{
		this.loc = loc;
		this.building = building;
		this.building.getBuildingManager().getEmpire().getEmpireManager().getMain().getServer().getPluginManager().registerEvents(this, building.getBuildingManager().getEmpire().getEmpireManager().getMain());
	}

	//Getters

	public Building getBuilding()
	{
		return this.building;
	}

	public Location getLocation()
	{
		return this.loc;
	}

	public Inventory getInventory()
	{
		return this.inv;
	}

}
