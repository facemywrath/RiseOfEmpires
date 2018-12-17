package facemywrath.riseofempires.util;

import java.util.Random;

public class GetRandom {
	
	public static int getRandInt(int lower, int upper)
	{

		Random random = new Random();
		return random.nextInt((upper - lower) + 1) + lower;
	
	}

}
