package facemywrath.riseofempires.empires.claims;

public class Claim {
	
	private ClaimManager cm;
	
	private int x;
	private int z;
	
	public Claim(ClaimManager cm, int x, int z)
	{
		this.x = x;
		this.z = z;
	}
	
	//Getters
	
	public int getX()
	{
		return x;
	}
	
	public int getZ()
	{
		return z;
	}
	
	public ClaimManager getClaimManager()
	{
		return cm;
	}

}
