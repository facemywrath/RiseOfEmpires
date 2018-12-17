package facemywrath.riseofempires.empires.buildings;

import org.bukkit.Location;

import com.sk89q.worldedit.blocks.BaseBlock;

public class BlockChange {
	
	private Location loc;
	private BaseBlock schemBlock;
	private BaseBlock currentBlock;
	
	public BlockChange(Location loc, BaseBlock schemBlock, BaseBlock currentBlock)
	{
		this.loc = loc;
		this.schemBlock = schemBlock;
		this.currentBlock = currentBlock;
	}
	
	//Getters
	
	public Location getLocation()
	{
		return this.loc;
	}
	
	public BaseBlock getTo()
	{
		return this.schemBlock;
	}
	
	public BaseBlock getFrom()
	{
		return this.currentBlock;
	}

}
