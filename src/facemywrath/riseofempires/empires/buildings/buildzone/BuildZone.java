package facemywrath.riseofempires.empires.buildings.buildzone;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Location;

import com.sk89q.worldedit.data.DataException;

import facemywrath.riseofempires.empires.buildings.Building;
import facemywrath.riseofempires.empires.buildings.BuildingManager;

public class BuildZone extends Building {

	public BuildZone(BuildingManager bm, int id) throws DataException, IOException {
		super(bm, id);
		this.setCost(15000);
	}

	public BuildZone(BuildingManager bm, UUID uuid, int direction, Location loc) throws DataException, IOException {
		super(bm, uuid, direction, loc, "BuildZone");
		this.setCost(15000);
	}

}
