package facemywrath.riseofempires.empires.buildings.home;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.data.DataException;

import facemywrath.riseofempires.empires.buildings.Building;
import facemywrath.riseofempires.empires.buildings.BuildingManager;
import facemywrath.riseofempires.empires.buildings.townhall.TownHall;
import facemywrath.riseofempires.empires.buildings.townhall.TownHallCore;

public class Home extends Building {

	public Home(BuildingManager bm, int id) throws DataException, IOException {
		super(bm, id);
		this.setMaxLevel(5);
		this.setCost(10000);
	}

	public Home(BuildingManager bm, UUID uuid, int direction, Location loc) throws DataException, IOException {
		super(bm, uuid, direction, loc, "Home");
		this.setMaxLevel(5);
		this.setCost(10000);
	}

}
