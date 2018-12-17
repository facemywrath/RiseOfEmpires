package facemywrath.riseofempires.empires.buildings.outpost;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Location;

import com.sk89q.worldedit.data.DataException;

import facemywrath.riseofempires.empires.buildings.Building;
import facemywrath.riseofempires.empires.buildings.BuildingManager;

public class Outpost extends Building {
	

	public Outpost(BuildingManager bm, int id) throws DataException, IOException {
		super(bm, id);
	}

	public Outpost(BuildingManager bm, UUID uuid, int direction, Location loc) throws DataException, IOException {
		super(bm, uuid, direction, loc, "Outpost");
	}

}
