package facemywrath.riseofempires.empires.buildings.mine;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import facemywrath.riseofempires.empires.buildings.Building;
import facemywrath.riseofempires.empires.buildings.BuildingManager;
import facemywrath.riseofempires.util.GetRandom;

public class Mine extends Building {

	private String type;
	private List<Block> ores = new ArrayList<>();
	private Long lastPlaced = System.currentTimeMillis();

	public Mine(BuildingManager bm, int id) {
		super(bm, id);
		FileConfiguration config = bm.getEmpire().getConfig();
		this.setMaxLevel(10);
		if(type != null)
		{
			switch(type)
			{
			case "Coalmine":
				this.setCost(20000);
			break;
			case "Goldmine":
				this.setCost(45000);
			break;
			case "Redstonemine":
				this.setCost(100000);
			break;
			case "Ironmine":
				this.setCost(250000);
			break;
			case "Diamondmine":
				this.setCost(750000);
			break;
			}
		}
	}

	public Mine(BuildingManager bm, UUID uuid, int direction, Location loc, String type)  {
		super(bm, uuid, direction, loc, "Mine");
		this.type = type;
		this.setMaxLevel(10);
		if(type != null)
		{
			switch(type)
			{
			case "Coalmine":
				this.setCost(20000);
			break;
			case "Goldmine":
				this.setCost(45000);
			break;
			case "Redstonemine":
				this.setCost(100000);
			break;
			case "Ironmine":
				this.setCost(250000);
			break;
			case "Diamondmine":
				this.setCost(750000);
			break;
			}
		}
	}

	public void addOre(Block block)
	{
		this.ores.add(block);
	}

	public void regenerateOres()
	{
		int delay = (int) (this.getDelayByType() * (1 - (0.1 * (-1+this.getLevel()))));
		Plugin pl = this.getBuildingManager().getEmpire().getEmpireManager().getMain();

		pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new BukkitRunnable()
		{
			public void run()
			{
				if(!emptyOres().isEmpty())
				{
					int ore = GetRandom.getRandInt(0, emptyOres().size()-1);
					emptyOres().get(ore).setType(getMaterial());
				}
				lastPlaced = System.currentTimeMillis();
				regenerateOres();
			}
		}, delay);
	}

	public void setType(String string) {
		this.type = string;

	}

	//Getters

	public List<Block> emptyOres()
	{
		List<Block> rets = new ArrayList<>();
		if(!ores.isEmpty())
			for(Block ore : ores)
			{
				if(ore.getType() != getMaterial())
					rets.add(ore);
			}
		return rets;
	}

	public int getDelayByType()
	{
		switch(getMaterial())
		{
		case COAL_ORE:
			return 12000;
		case IRON_ORE:
			return 18000;
		case GOLD_ORE:
			return 24000;
		case REDSTONE_ORE:
			return 24000;
		case DIAMOND_ORE:
			return 72000;
		}
		return 12000;
	}

	public String getTimeLeft()
	{
		int delay = (int) (this.getDelayByType() * (1 - (0.1 * (-1+this.getLevel()))));
		Long currentTime = System.currentTimeMillis();
		currentTime /= 1000;
		Long lastTime = lastPlaced / 1000;
		Long currentDelay = (long) (delay/20);
		int delayLeft = (int) Math.abs(lastTime + currentDelay - currentTime);
		int minutes = (int) (delayLeft/60.0);
		int seconds = (int) (delayLeft%60);
		String timeLeftFormatted = minutes + " minutes, " + seconds + " seconds ";
		return timeLeftFormatted;
	}

	public String getType()
	{
		return type;
	}

	public Material getMaterial()
	{
		switch(this.type)
		{
		case "Coalmine":
			return Material.COAL_ORE;
		case "Ironmine":
			return Material.IRON_ORE;
		case "Goldmine":
			return Material.GOLD_ORE;
		case "Redstonemine":
			return Material.REDSTONE_ORE;
		case "Diamondmine":
			return Material.DIAMOND_ORE;
		}
		return null;
	}

}
