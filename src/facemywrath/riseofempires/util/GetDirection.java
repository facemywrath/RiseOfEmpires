package facemywrath.riseofempires.util;

import org.bukkit.Bukkit;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.util.Direction;

public class GetDirection {

	public static Direction getDirection(float yaw)
	{
		yaw-=45;
		if(yaw<0)
			yaw = 360-yaw;
		int dirnum= (int) (yaw/90.0);
		if(dirnum < 0)
			dirnum = 3+dirnum;
		switch(dirnum)
		{
		case 0:
			return Direction.WEST;
		case 1:
			return Direction.NORTH;
		case 2:
			return Direction.EAST;
		case 3:
			return Direction.SOUTH;
		}
		return Direction.SOUTH;
	}

}
