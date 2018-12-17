package facemywrath.riseofempires.util;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;

public class PerlinNoise {

	public static int getLevel(Chunk chunk)
	{
		int x = chunk.getX();
		int z = chunk.getZ();
		World w = chunk.getWorld();
		int levels[] = {getAverage(x-1,z), getAverage(x,z-1), getAverage(x,z), getAverage(x,z+1), getAverage(x+1,z)};
		int average = (int) ((levels[0] + levels[1] + levels[2] + levels[3])/5.0);
		return average+1;
	}

	private static int getAverage( int x, int z)
	{
		int levels[] = {getLevel(x-1,z), getLevel(x,z-1), getLevel(x,z), getLevel(x,z+1), getLevel(x+1,z)};
		int average = (int) ((levels[0] + levels[1] + levels[2] + levels[3])/5.0);
		return average;
	}

	private static int getLevel( int x, int z)
	{
		Random rand = new Random();
		x = Math.abs(x);
		z = Math.abs(z);
		rand.setSeed(x+z);
		int max = x+z;
		max/=12;
		max+=1;
		if(max >= 200)
			max = 200;
		int randInt = rand.nextInt(max);
		if(randInt >= 10)
		{
			if(randInt >= max*0.8)
			{
				return (int) (max);
			}
			else
				return randInt/6;
		}
		return randInt;
	}
}
