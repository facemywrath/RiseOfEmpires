package facemywrath.riseofempires.empires.buildings.townhall;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.data.DataException;

import facemywrath.riseofempires.empires.buildings.Building;
import facemywrath.riseofempires.empires.buildings.BuildingManager;

public class TownHall extends Building{

	public TownHall(BuildingManager bm, int id){
		super(bm, id);
		this.setMaxLevel(10);
	}

	public TownHall(BuildingManager bm, UUID uuid, Location loc){
		super(bm, uuid, 0, loc, "Town_Hall");
		this.setMaxLevel(10);
	}

	public TownHall(Location loc, int dir, UUID player) {
		super(loc, dir, player);
		// TODO Auto-generated constructor stub
	}
}