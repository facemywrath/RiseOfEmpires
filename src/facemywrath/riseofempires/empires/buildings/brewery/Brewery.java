package facemywrath.riseofempires.empires.buildings.brewery;

import java.util.UUID;

import org.bukkit.Location;

import facemywrath.riseofempires.empires.buildings.Building;
import facemywrath.riseofempires.empires.buildings.BuildingManager;

public class Brewery extends Building{

	public Brewery(BuildingManager bm, UUID uuid, int direction, Location loc, String name) {
		super(bm, uuid, direction, loc, name);
		// TODO Auto-generated constructor stub
	}

	public Brewery(BuildingManager bm, int id) {
		super(bm, id);
		// TODO Auto-generated constructor stub
	}
}
