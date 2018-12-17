package facemywrath.riseofempires.empires.buildings;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import facemywrath.riseofempires.empires.buildings.home.Home;
import facemywrath.riseofempires.empires.buildings.home.HomeCore;
import facemywrath.riseofempires.empires.buildings.townhall.TownHall;
import facemywrath.riseofempires.empires.buildings.townhall.TownHallCore;

public class BuildQueue {

	private boolean done = false;
	private Long delay = 64L;
	private int amount = 1;
	private Building building;
	private List<BlockChange> doors = new ArrayList<>();
	private List<BlockChange> blocks = new ArrayList<>();

	@SuppressWarnings("deprecation")
	public BuildQueue(Building building)
	{
		this.building = building;
		Plugin pl = this.getBuilding().getBuildingManager().getEmpire().getEmpireManager().getMain();
		pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new BukkitRunnable()
		{
			public void run()
			{
				processQueue();
			}
		}, delay);
	}

	@SuppressWarnings("deprecation")
	public void processQueue()
	{
		Bukkit.getPluginManager().getPlugin("RiseOfEmpires").getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("RiseOfEmpires"), new Runnable(){
			public void run(){
				if(!done)
				{
					for(int i = 0; i < amount; i++)
					{
						if(blocks.size()>0)
						{
							if(blocks.size() == 1)
							{
								setDone(true);
								getBuilding().getBuildingManager().getEmpire().getConfig().set("Buildings." + getBuilding().getId() + ".Generating", false);
								getBuilding().getBuildingManager().getEmpire().saveConfig();
								getBuilding().getBuildingManager().getEmpire().reloadConfig();
								placeDoors();
								building.getBuildingManager().getEmpire().broadcastToMembers(building.getName() + " finished building");
							}
							Location loc = blocks.get(0).getLocation();	
							if(blocks.get(0).getFrom().getId() != 0 && blocks.get(0).getFrom().getId() != blocks.get(0).getTo().getId() && blocks.get(0).getFrom().getData() != blocks.get(0).getTo().getData())
							{
								blocks.get(0).getLocation().getBlock().breakNaturally();
							}
							if(Material.getMaterial(blocks.get(0).getTo().getId()).toString().contains("DOOR"))
							{
								doors.add(blocks.get(0));
							}
							else
							{
								loc.getWorld().playSound(loc, Sound.BLOCK_STONE_PLACE, 1, 3);
								loc.getWorld().playEffect(loc.clone().add(new Vector(0,1,0)), Effect.TILE_BREAK, 3);
								loc.getBlock().setType(Material.getMaterial(blocks.get(0).getTo().getId()));
								loc.getBlock().setData((byte) blocks.get(0).getTo().getData());
								building.getBlocks().add(blocks.get(0).getLocation().getBlock());
							}
							blocks.remove(0);
						}
					}
					if(getBuilding() instanceof TownHall)
					{
						((TownHallCore) getBuilding().getCoreBlock()).setupMainInventory();
					}
					if(getBuilding() instanceof Home)
					{
						((HomeCore) getBuilding().getCoreBlock()).setupMainInventory();
					}
				}
				processQueue();
			}
		}, delay);
	}
	
	public void placeDoors()
	{
		for(BlockChange door : doors)
		{
			Location loc = door.getLocation();
			loc.getWorld().playSound(loc, Sound.BLOCK_STONE_PLACE, 1, 3);
			loc.getWorld().playEffect(loc.clone().add(new Vector(0,1,0)), Effect.TILE_BREAK, 3);
			loc.getBlock().setType(Material.getMaterial(door.getTo().getId()));
			loc.getBlock().setData((byte) door.getTo().getData());
			building.getBlocks().add(door.getLocation().getBlock());
		}
	}

	public void addBlockChange(BlockChange bc)
	{
		this.blocks.add(bc);
	}

	public void setDone(boolean done)
	{
		this.getBuilding().getBuildingManager().getEmpire().getConfig().set("Buildings." + this.getBuilding().getId() + ".Generating", !done);
		this.getBuilding().getBuildingManager().getEmpire().saveConfig();
		this.getBuilding().getBuildingManager().getEmpire().reloadConfig();
		this.done = done;
	}

	//Getters

	public boolean isBuilding()
	{
		return this.blocks.size() > 0;
	}

	public Long getDelay()
	{
		return this.delay;
	}

	public int getAmount()
	{
		return this.amount;
	}

	public Building getBuilding()
	{
		return this.building;
	}

	public void setDelay(int x)
	{
		this.delay = x *1L;
	}

	public void setAmount(int x)
	{
		this.amount = x;
	}

}
