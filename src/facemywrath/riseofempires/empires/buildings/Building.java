package facemywrath.riseofempires.empires.buildings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.sk89q.worldedit.world.DataException;

import facemywrath.riseofempires.empires.Empire;
import facemywrath.riseofempires.empires.buildings.buildzone.BuildZone;
import facemywrath.riseofempires.empires.buildings.buildzone.BuildZoneCore;
import facemywrath.riseofempires.empires.buildings.home.Home;
import facemywrath.riseofempires.empires.buildings.home.HomeCore;
import facemywrath.riseofempires.empires.buildings.mine.Mine;
import facemywrath.riseofempires.empires.buildings.mine.MineCore;
import facemywrath.riseofempires.empires.buildings.townhall.TownHall;
import facemywrath.riseofempires.empires.buildings.townhall.TownHallCore;
import facemywrath.riseofempires.empires.claims.ClaimManager;
import facemywrath.riseofempires.empires.members.Member;
import facemywrath.riseofempires.empires.members.MemberManager;
import facemywrath.riseofempires.enums.Rank;
import facemywrath.riseofempires.main.Main;

@SuppressWarnings("deprecation")
public class Building implements Listener {

	private BuildingManager bm;
	private byte level = 1;	
	private List<Block> blocks = new ArrayList<>();
	private BuildQueue queue;
	private CuboidClipboard clipboard;
	private CoreBlock coreblock;
	private UUID player;
	private Location loc;
	private String name;
	private int maxLevel = 1; 
	private int id;
	private int cost = 10000;
	private boolean rotated;
	private int direction;

	//Constructor
	
	public Building(Location loc, int dir, UUID player)
	{
		this.player = player;
		this.loc = loc;
		this.level = 1;
		this.name = "Town_Hall";
		this.direction = dir;
	}

	public Building(BuildingManager bm, int id)
	{
		this.rotated = false;
		this.bm = bm;
		this.id = id;
		Empire emp = bm.getEmpire();
		try{
			FileConfiguration config = emp.getConfig();
			if(config.contains("Buildings." + id))
			{
				this.level = (byte) config.getInt("Buildings." + id + ".Level");
				this.player = UUID.fromString(config.getString("Buildings." + id + ".PlacedBy"));
				int x = config.getInt("Buildings." + id + ".X");
				int y = config.getInt("Buildings." + id + ".Y");
				int z = config.getInt("Buildings." + id + ".Z");
				this.loc = new Location(Bukkit.getWorld(config.getString("World")), x, y, z);
				this.name = config.getString("Buildings." + id + ".Type");
				this.direction = config.getInt("Buildings." + id + ".Direction");
				if(this instanceof Mine && config.contains("Buildings." + id + ".MineType"))
				{
					((Mine) this).setType(config.getString("Buildings." + id + ".MineType"));
				}
			}
			else
				this.bm.getBuildings().remove(this);
		}catch(Exception e){}
		setCoreBlock();
		if(this instanceof Mine)
		{
			((Mine) this).regenerateOres();
		}
		this.bm.getEmpire().getEmpireManager().getMain().getServer().getPluginManager().registerEvents(this, this.bm.getEmpire().getEmpireManager().getMain());
	}

	public void loadBlocks()
	{
		if(!this.bm.getEmpire().getConfig().getBoolean("Buildings." + id + ".Generating"))
		{
			Main main = this.getBuildingManager().getEmpire().getEmpireManager().getMain();

			File schematicFile = new File(main.getDataFolder() + "/schematics/" + this.name + "_" + this.level + ".schematic");
			try
			{
				CuboidClipboard schematic = MCEditSchematicFormat.getFormat(schematicFile).load(schematicFile);
				if(this.clipboard == null)
					this.clipboard = schematic.loadSchematic(schematicFile);
				if(!rotated)
				{
					this.rotated = true;
					int rotation = this.direction * 90;
					clipboard.rotate2D(rotation);
				}
				if(getCoreLocation() != null)
				{
					Location coreLoc = getCoreLocation();
					List<BlockChange> blocksChanged = new ArrayList<>();
					Block coreBlock = null;
					if(queue == null)
						queue = new BuildQueue(this);
					int xdiff = clipboard.getOffset().getBlockX();
					int ydiff = clipboard.getOffset().getBlockY();
					int zdiff = clipboard.getOffset().getBlockZ();
					Location tempLoc = this.getLocation().clone().add(new Location(this.getLocation().getWorld(), xdiff, ydiff, zdiff));	
					for (int y = 0; y < clipboard.getSize().getBlockY(	); y++) {
						for (int x = 0; x < clipboard.getSize().getBlockX(); x++) {
							for (int z = 0; z < clipboard.getSize().getBlockZ(); z++)
							{
								Location temploc = tempLoc.clone().add(new Location(tempLoc.getWorld(), x, y, z));
								BaseBlock schemBlock = clipboard.getBlock(new Vector(x,y,z));
								Block b = temploc.getBlock();
								if(schemBlock.getId() != 0 && (schemBlock.getId() == b.getTypeId() || schemBlock.getData() == b.getData()))
								{
									if(this instanceof Mine && schemBlock.getId() == 19)
										((Mine) this).addOre(b);
									this.blocks.add(b);
								}
							}
						}
					}
				}
			}
			catch (ArrayIndexOutOfBoundsException | DataException | IOException e)
			{
				Bukkit.broadcastMessage("ERORR");
			}
		}
		else
			regenerate();

	}

	public Building(BuildingManager bm, UUID uuid, int direction, Location loc, String name)
	{
		this.rotated = false;
		this.player = uuid;
		this.bm = bm;
		this.id = BuildingManager.getHighestId(bm);
		this.direction = direction;
		this.loc = loc;
		this.name = name;
		this.level = 1;
		this.bm.getEmpire().getEmpireManager().getMain().getServer().getPluginManager().registerEvents(this, this.bm.getEmpire().getEmpireManager().getMain());
	}

	public boolean checkClaim(Player player)
	{
		if(this.getBuildingManager().getEmpire().getEmpireManager().getMain().getCommandObj().getOverrides().contains(player.getUniqueId()))
			return true;
		Member member = null;
		switch(this.getName())
		{
		case "Home":
			if(player.getUniqueId().equals(this.getWhoPlaced()))
				return true;
			if(MemberManager.getMember(player) == null)
				return false;
			if(!MemberManager.getMember(player).getEmpire().equals(this.getBuildingManager().getEmpire()))
				return false;
			if(MemberManager.getMember(player).getRank() == Rank.EMPEROR || MemberManager.getMember(player).getRank() == Rank.GENERAL)
				return true;
			return false;
		case "Town_Hall":
			if(MemberManager.getMember(player) == null)
				return false;
			member = MemberManager.getMember(player);
			if(!member.getEmpire().equals(this.getBuildingManager().getEmpire()))
				return false;
			if(member.getRank() == Rank.EMPEROR || member.getRank() == Rank.GENERAL)
				return true;
			return false;
		case "Mine":
			if(MemberManager.getMember(player) == null)
				return false;
			member = MemberManager.getMember(player);
			if(!member.getEmpire().equals(this.getBuildingManager().getEmpire()))
				return false;
			if(member.getRank() == Rank.EMPEROR || member.getRank() == Rank.GENERAL || member.getRank() == Rank.BUILDER)
				return true;
			return false;
		case "BuildZone":
			if(MemberManager.getMember(player) == null)
				return false;
			member = MemberManager.getMember(player);
			if(!member.getEmpire().equals(this.getBuildingManager().getEmpire()))
				return false;
			if(member.getRank() == Rank.EMPEROR || member.getRank() == Rank.GENERAL)
				return true;
			if(member.getUUID().equals(this.player))
				return true;
			return false;
		}
		return false;
	}

	public boolean checkClaim(Player player, Material mat)
	{
		if(this.getBuildingManager().getEmpire().getEmpireManager().getMain().getCommandObj().getOverrides().contains(player.getUniqueId()))
			return true;
		Member member = null;
		switch(this.getName())
		{
		case "Home":
			if(player.getUniqueId().equals(this.getWhoPlaced()))
				return true;
			if(MemberManager.getMember(player) == null)
				return false;
			if(!MemberManager.getMember(player).getEmpire().equals(this.getBuildingManager().getEmpire()))
				return false;
			if(MemberManager.getMember(player).getRank() == Rank.EMPEROR || MemberManager.getMember(player).getRank() == Rank.GENERAL)
				return true;
			return false;
		case "Town_Hall":
			if(MemberManager.getMember(player) == null)
				return false;
			member = MemberManager.getMember(player);
			if(!member.getEmpire().equals(this.getBuildingManager().getEmpire()))
				return false;
			if(member.getRank() == Rank.EMPEROR || member.getRank() == Rank.GENERAL)
				return true;
			return false;
		case "Mine":
			if(MemberManager.getMember(player) == null)
				return false;
			member = MemberManager.getMember(player);
			if(!member.getEmpire().equals(this.getBuildingManager().getEmpire()))
				return false;
			if(member.getRank() == Rank.EMPEROR || member.getRank() == Rank.GENERAL || member.getRank() == Rank.BUILDER)
				return true;
			if(mat.toString().toUpperCase().contains("ORE") && member.getRank() == Rank.CITIZEN)
				return true;
			return false;
		case "BuildZone":
			if(MemberManager.getMember(player) == null)
				return false;
			member = MemberManager.getMember(player);
			if(!member.getEmpire().equals(this.getBuildingManager().getEmpire()))
				return false;
			if(member.getRank() == Rank.EMPEROR || member.getRank() == Rank.GENERAL)
				return true;
			if(member.getUUID().equals(this.player))
				return true;
			return false;
		}
		return false;
	}

	public boolean createable()
	{
		if(this instanceof BuildZone)
		{

			Chunk chunk = this.getLocation().getChunk();
			if(MemberManager.getMember(Bukkit.getOfflinePlayer(player).getUniqueId()) == null)
			{
				if(Bukkit.getOfflinePlayer(player).isOnline())
				{
					if(MemberManager.getMember(Bukkit.getPlayer(player)) != null)
						if(MemberManager.getMember(Bukkit.getPlayer(player)).getEmpire() != null)
						{
							Member member = MemberManager.getMember(Bukkit.getPlayer(player));
							Empire emp = member.getEmpire();
							Bukkit.getPlayer(player).sendMessage(emp.getTag() + "&4You are not in an empire");
						}
				}
				return false;
			}
			if(!ClaimManager.getEmpireAt(this.loc.getChunk()).equals(this.getBuildingManager().getEmpire()))
			{
				if(Bukkit.getOfflinePlayer(player).isOnline())
				{
					if(MemberManager.getMember(Bukkit.getPlayer(player)) != null)
						if(MemberManager.getMember(Bukkit.getPlayer(player)).getEmpire() != null)
						{
							Member member = MemberManager.getMember(Bukkit.getPlayer(player));
							Empire emp = member.getEmpire();
							Bukkit.getPlayer(player).sendMessage(emp.getTag() + "&4Building can not be placed. Your empire doesn't own that land");
						}
				}
				return false;
			}
			for(Building building : this.bm.getBuildings())
			{
				if(!building.equals(this))
				{
					if(Math.abs(this.getLocation().getChunk().getX() - building.getLocation().getChunk().getX()) < 2 && Math.abs(this.getLocation().getChunk().getZ() - building.getLocation().getChunk().getZ()) < 2)
					{
						if(building.getLocation().getChunk().equals(this.getLocation().getChunk()))
						{
							if(Bukkit.getOfflinePlayer(player).isOnline())
							{
								if(MemberManager.getMember(Bukkit.getPlayer(player)) != null)
									if(MemberManager.getMember(Bukkit.getPlayer(player)).getEmpire() != null)
									{
										Member member = MemberManager.getMember(Bukkit.getPlayer(player));
										Empire emp = member.getEmpire();
										Bukkit.getPlayer(player).sendMessage(emp.getTag() + "&4Building can not be placed. There are other buildings in this chunk.");
									}
							}
							return false;

						}
						for(Block block : building.getBlocks())
						{
							if(block.getLocation().getChunk().equals(this.getLocation().getChunk()))
							{
								if(Bukkit.getOfflinePlayer(player).isOnline())
								{
									if(MemberManager.getMember(Bukkit.getPlayer(player)) != null)
										if(MemberManager.getMember(Bukkit.getPlayer(player)).getEmpire() != null)
										{
											Member member = MemberManager.getMember(Bukkit.getPlayer(player));
											Empire emp = member.getEmpire();
											Bukkit.getPlayer(player).sendMessage(emp.getTag() + "&4Building can not be placed. There are other buildings in this chunk.");
										}
								}
								return false;
							}
						}
					}
				}
			}
			return true;
		}
		else 
		{
			Main main = Main.getPlugin(Main.class);
			File schematicFile = new File(main.getDataFolder() + "/schematics/" + this.name + "_" + this.level + ".schematic");
			try
			{
				CuboidClipboard schematic = MCEditSchematicFormat.getFormat(schematicFile).load(schematicFile);
				if(!this.rotated)
				{
					this.rotated = true;
					int rotation = this.direction * 90;
					schematic.rotate2D(rotation);
				}
				if(this.clipboard == null)
				{
					this.clipboard = schematic;
				}
				if(this.bm != null && getCoreLocation() != null)
				{
					Location coreLoc = getCoreLocation();
					int xdiff = clipboard.getOffset().getBlockX();
					int ydiff = clipboard.getOffset().getBlockY();
					int zdiff = clipboard.getOffset().getBlockZ();
					Location tempLoc = this.getLocation().clone().add(new Location(this.getLocation().getWorld(), xdiff, ydiff, zdiff));

					for (int y = 0; y < clipboard.getSize().getBlockY(	); y++) {
						for (int x = 0; x < clipboard.getSize().getBlockX(); x++) {
							for (int z = 0; z < clipboard.getSize().getBlockZ(); z++)
							{
								Location temploc = tempLoc.clone().add(new Location(tempLoc.getWorld(), x, y, z));
								if(clipboard.getBlock(new Vector(x,y,z)) != null)
								{
									BaseBlock schemBlock = clipboard.getBlock(new Vector(x,y,z));
									Block b = temploc.getBlock();
									if(b.getType() != Material.AIR && b.getLocation().getBlockY() > this.loc.getBlockY() && b.getType() != Material.COMMAND)
									{
										if(Bukkit.getOfflinePlayer(player).isOnline())
										{
											if(MemberManager.getMember(Bukkit.getPlayer(player)) != null)
												if(MemberManager.getMember(Bukkit.getPlayer(player)).getEmpire() != null)
												{
													Member member = MemberManager.getMember(Bukkit.getPlayer(player));
													Empire emp = member.getEmpire();
													Bukkit.getPlayer(player).sendMessage(emp.getTag() + " &4Building can not be placed. Needs " + clipboard.getSize().getBlockX() + "x" + (clipboard.getSize().getBlockY()+clipboard.getOffset().getBlockY()) + "x" + clipboard.getSize().getBlockZ() + " of air above it.");
												}
										}
										return false;
									}
									Plugin pl = this.getBuildingManager().getEmpire().getEmpireManager().getMain();
									if((ClaimManager.getEmpireAt(temploc) != null && !ClaimManager.getEmpireAt(temploc).equals(this.bm.getEmpire())) || (ClaimManager.getEmpireAt(temploc) == null && (this.getName().equals("Outpost") || !this.getName().equals("Town_Hall"))))
									{
										return false;
									}
									if(bm.getBuildings().size() > 0)
										for(Building building : this.bm.getBuildings())
										{
											if(building instanceof BuildZone)
											{
												if(building.getLocation().getChunk().equals(b.getLocation().getChunk()))
												{

													if(Bukkit.getOfflinePlayer(player).isOnline())
													{
														if(MemberManager.getMember(Bukkit.getPlayer(player)) != null)
															if(MemberManager.getMember(Bukkit.getPlayer(player)).getEmpire() != null)
															{
																Member member = MemberManager.getMember(Bukkit.getPlayer(player));
																Empire emp = member.getEmpire();
																Bukkit.getPlayer(player).sendMessage(emp.getTag() + ChatColor.DARK_RED + " Building can not be placed. To close to another building.");
															}
													}
													return false;

												}
												continue;
											}
											if(building == this)
												continue;
											if(b.getLocation().getBlockX() < (building.getLocation().getBlockX() + building.getSchematic().getOffset().getBlockX()))
												continue;
											if(b.getLocation().getBlockX() > (building.getLocation().getBlockX() + building.getSchematic().getOffset().getBlockX() + building.getSchematic().getSize().getBlockX()))
												continue;
											if(b.getLocation().getBlockY() < (building.getLocation().getBlockY() + building.getSchematic().getOffset().getBlockY()))
												continue;
											if(b.getLocation().getBlockY() > (building.getLocation().getBlockY() + building.getSchematic().getOffset().getBlockY() + building.getSchematic().getSize().getBlockY()))
												continue;
											if(b.getLocation().getBlockZ() < (building.getLocation().getBlockZ() + building.getSchematic().getOffset().getBlockZ()))
												continue;
											if(b.getLocation().getBlockZ() > (building.getLocation().getBlockZ() + building.getSchematic().getOffset().getBlockZ() + building.getSchematic().getSize().getBlockZ()))
												continue;
											if(Bukkit.getOfflinePlayer(player).isOnline())
											{
												if(MemberManager.getMember(Bukkit.getPlayer(player)) != null)
													if(MemberManager.getMember(Bukkit.getPlayer(player)).getEmpire() != null)
													{
														Member member = MemberManager.getMember(Bukkit.getPlayer(player));
														Empire emp = member.getEmpire();
														Bukkit.getPlayer(player).sendMessage(emp.getTag() + ChatColor.DARK_RED + " Building can not be placed. To close to another building.");	
													}
											}
											return false;
										}
								}
							}
						}
					}
				}
				else if(this.bm == null)
				{
					int xdiff = clipboard.getOffset().getBlockX();
					int ydiff = clipboard.getOffset().getBlockY();
					int zdiff = clipboard.getOffset().getBlockZ();
					Location tempLoc = this.getLocation().clone().add(new Location(this.getLocation().getWorld(), xdiff, ydiff, zdiff));

					for (int y = 0; y < clipboard.getSize().getBlockY(	); y++) {
						for (int x = 0; x < clipboard.getSize().getBlockX(); x++) {
							for (int z = 0; z < clipboard.getSize().getBlockZ(); z++)
							{
								Location temploc = tempLoc.clone().add(new Location(tempLoc.getWorld(), x, y, z));
								if(clipboard.getBlock(new Vector(x,y,z)) != null)
								{
									BaseBlock schemBlock = clipboard.getBlock(new Vector(x,y,z));
									Block b = temploc.getBlock();
									if(b.getType() != Material.AIR && b.getLocation().getBlockY() > this.loc.getBlockY() && b.getType() != Material.COMMAND)
									{
										return false;
									}
								}
							}
						}
					}
				
				}
			}
			catch (ArrayIndexOutOfBoundsException | IOException | DataException e)
			{
				Bukkit.broadcastMessage("Exception on creation");
				return false;
			}
		}
		return true;
	}

	public void create()
	{
		if(createable())
		{
			this.level = 1;
			queue = new BuildQueue(this);
			try
			{
				Empire emp = this.getBuildingManager().getEmpire();
				int id = BuildingManager.getHighestId(this.bm);
				if(!this.name.equals("Town_Hall"))
				{
					emp.saveConfig();
					emp.reloadConfig();
					emp.getConfig().set("Buildings." + id + ".Type", this.name);
					emp.getConfig().set("Buildings." + id + ".PlacedBy", this.player.toString());
					if(this instanceof BuildZone)
						emp.getConfig().set("Buildings." + id + ".Generating", false);
					else
						emp.getConfig().set("Buildings." + id + ".Generating", true);
					emp.getConfig().set("Buildings." + id + ".Level", 1);
					emp.getConfig().set("Buildings." + id + ".Direction", this.direction);
					emp.getConfig().set("Buildings." + id + ".X", this.loc.getBlockX());
					emp.getConfig().set("Buildings." + id + ".Y", this.loc.getBlockY());
					emp.getConfig().set("Buildings." + id + ".Z", this.loc.getBlockZ());
					if(this instanceof Mine)
						emp.getConfig().set("Buildings." + id + ".MineType", ((Mine) this).getType());
					emp.saveConfig();
					emp.reloadConfig();
					this.level = 1;
				}
				if(this instanceof BuildZone)
				{
					for(int i = 0; i < 16; i++)
					{
						for(int j = 0; j < 16; j++)
						{
							if(i == 0 || i == 15 || j == 0 || j == 15)
								this.getLocation().getChunk().getBlock(i, this.getLocation().getBlockY(), j).setType(Material.SMOOTH_BRICK);
						}
					}
					setCoreBlock();
					this.bm.addBuilding(this);
					return;
				}
				setCoreBlock();
				int xdiff = clipboard.getOffset().getBlockX();
				int ydiff = clipboard.getOffset().getBlockY();
				int zdiff = clipboard.getOffset().getBlockZ();
				Location tempLoc = this.getLocation().clone().add(new Location(this.getLocation().getWorld(), xdiff, ydiff, zdiff));
				if(this instanceof Mine)
				{
					for (int y = clipboard.getSize().getBlockY(	)-1; y >= 0 ; y--) {
						for (int x = 0; x < clipboard.getSize().getBlockX(); x++) {
							for (int z = 0; z < clipboard.getSize().getBlockZ(); z++)
							{
								Location temploc = tempLoc.clone().add(new Location(tempLoc.getWorld(), x, y, z));
								BaseBlock schemBlock = clipboard.getBlock(new Vector(x,y,z));
								Block b = temploc.getBlock();
								if(schemBlock.getId() != 0 && (schemBlock.getId() != b.getTypeId() || schemBlock.getData() != b.getData()))
								{
									if(schemBlock.getId() == 19)
									{
										((Mine) this).addOre(b);
										queue.addBlockChange(new BlockChange(temploc, new BaseBlock(((Mine) this).getMaterial().getId(), 0), new BaseBlock(b.getTypeId(), b.getData())));

									}
									else
										queue.addBlockChange(new BlockChange(temploc, schemBlock, new BaseBlock(b.getTypeId(), b.getData())));

								}
								else if(schemBlock.getId() == 0 && b.getType() != Material.AIR && this instanceof Mine)
									queue.addBlockChange(new BlockChange(temploc, schemBlock, new BaseBlock(b.getTypeId(), b.getData())));
								else if(schemBlock.getId() == b.getTypeId() && schemBlock.getData() == b.getData())
									this.blocks.add(b);
							}
						}
					}
				}
				else
				{
					for (int y = 0; y < clipboard.getSize().getBlockY(	); y++) {
						for (int x = 0; x < clipboard.getSize().getBlockX(); x++) {
							for (int z = 0; z < clipboard.getSize().getBlockZ(); z++)
							{
								Location temploc = tempLoc.clone().add(new Location(tempLoc.getWorld(), x, y, z));
								BaseBlock schemBlock = clipboard.getBlock(new Vector(x,y,z));
								Block b = temploc.getBlock();
								if(schemBlock.getId() != 0 && (schemBlock.getId() != b.getTypeId() || schemBlock.getData() != b.getData()))
								{
									queue.addBlockChange(new BlockChange(temploc, schemBlock, new BaseBlock(b.getTypeId(), b.getData())));
								}
								else if(schemBlock.getId() == 0 && b.getLocation().getY() <= this.getLocation().getY()-1)
									queue.addBlockChange(new BlockChange(temploc, schemBlock, new BaseBlock(b.getTypeId(), b.getData())));
								else if(schemBlock.getId() == b.getTypeId() && schemBlock.getData() == b.getData())
									this.blocks.add(b);
							}
						}
					}
				}
				this.bm.addBuilding(this);
				if(this instanceof Mine)
				{
					((Mine) this).regenerateOres();
				}
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				Bukkit.broadcastMessage("ERORR");
			}
		}
	}

	public void destroy()
	{
		if(!(this instanceof BuildZone))
			for(Block b : this.blocks)
			{
				b.setType(Material.AIR);
			}
		else
		{
			for(int i = 0; i < 16; i++)
			{
				for(int j = 0; j < 16; j++)
				{
					if(i == 0 || i == 15 || j == 0 || j == 15)
						if(this.getLocation().getChunk().getBlock(i, this.getLocation().getBlockY(), j).getType() == Material.SMOOTH_BRICK)
						this.getLocation().getChunk().getBlock(i, this.getLocation().getBlockY(), j).setType(Material.AIR);
				}
			}
		}
		this.getCoreLocation().getBlock().setType(Material.AIR);
		this.getBuildingManager().removeBuilding(this);
		FileConfiguration config = this.getBuildingManager().getEmpire().getConfig();
		for(String s : config.getConfigurationSection("Buildings." + this.id).getKeys(false))
		{
			config.set("Buildings." + this.id + "." + s, null);
		}
		config.set("Buildings." + this.id, null);
		this.getBuildingManager().getEmpire().saveConfig();
		this.getBuildingManager().getEmpire().reloadConfig();
	}

	public List<BlockChange> getDifferentBlocks()
	{
		List<BlockChange> blocksChanged = new ArrayList<>();
		Main main = this.getBuildingManager().getEmpire().getEmpireManager().getMain();
		try
		{
			Block coreBlock = null;
			int xdiff = clipboard.getOffset().getBlockX();
			int ydiff = clipboard.getOffset().getBlockY();
			int zdiff = clipboard.getOffset().getBlockZ();
			Location tempLoc = this.getLocation().clone().add(new Location(this.getLocation().getWorld(), xdiff, ydiff, zdiff));	
			for (int y = 0; y < clipboard.getSize().getBlockY(	); y++) {
				for (int x = 0; x < clipboard.getSize().getBlockX(); x++) {
					for (int z = 0; z < clipboard.getSize().getBlockZ(); z++)
					{
						Location temploc = tempLoc.clone().add(new Location(tempLoc.getWorld(), x, y, z));
						BaseBlock schemBlock = clipboard.getBlock(new Vector(x,y,z));
						Block b = temploc.getBlock();
						if(schemBlock.getId() != 0 && (schemBlock.getId() != b.getTypeId() || schemBlock.getData() != b.getData()))
						{
							if(schemBlock.getId() == 19)
							{
								((Mine) this).addOre(b);
								blocksChanged.add(new BlockChange(temploc, new BaseBlock(((Mine) this).getMaterial().getId(), 0), new BaseBlock(b.getTypeId(), b.getData())));

							}
							else
								blocksChanged.add(new BlockChange(temploc, schemBlock, new BaseBlock(b.getTypeId(), b.getData())));
						}
						else if(schemBlock.getId() == 0 && b.getType() != Material.AIR && this instanceof Mine)
							blocksChanged.add(new BlockChange(temploc, schemBlock, new BaseBlock(b.getTypeId(), b.getData())));
					}
				}
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			Bukkit.broadcastMessage("ERORR");
		}
		return blocksChanged;

	}
	public void regenerate()
	{
		if(queue == null)
			queue = new BuildQueue(this);
		queue.setDone(false);
		queue.setDelay(64);
		queue.setAmount(1);
		int xdiff = clipboard.getOffset().getBlockX();
		int ydiff = clipboard.getOffset().getBlockY();
		int zdiff = clipboard.getOffset().getBlockZ();
		Location tempLoc = this.getLocation().clone().add(new Location(this.getLocation().getWorld(), xdiff, ydiff, zdiff));
		if(!rotated)
		{
			this.rotated = true;
			int rotation = this.direction * 90;
			Bukkit.broadcastMessage(rotation + "");
			clipboard.rotate2D(rotation);
		}
		if(this instanceof Mine)
		{
			for (int y = clipboard.getSize().getBlockY()-1; y >= 0; y--) {
				for (int x = 0; x < clipboard.getSize().getBlockX(); x++) {
					for (int z = 0; z < clipboard.getSize().getBlockZ(); z++)
					{
						Location temploc = tempLoc.clone().add(new Location(tempLoc.getWorld(), x, y, z));
						BaseBlock schemBlock = clipboard.getBlock(new Vector(x,y,z));
						Block b = temploc.getBlock();
						if(schemBlock.getId() != 0 && (schemBlock.getId() != b.getTypeId() || schemBlock.getData() != b.getData()))
						{
							if(schemBlock.getId() == 19)
							{
								((Mine) this).addOre(b);
								queue.addBlockChange(new BlockChange(temploc, new BaseBlock(((Mine) this).getMaterial().getId(), 0), new BaseBlock(b.getTypeId(), b.getData())));

							}
							else
								queue.addBlockChange(new BlockChange(temploc, schemBlock, new BaseBlock(b.getTypeId(), b.getData())));
						}
						else if(schemBlock.getId() == 0 && b.getType() != Material.AIR)
							queue.addBlockChange(new BlockChange(temploc, schemBlock, new BaseBlock(b.getTypeId(), b.getData())));
						else if(schemBlock.getId() == b.getTypeId() && schemBlock.getData() == b.getData() && !this.blocks.contains(b))
							this.blocks.add(b);
					}
				}
			}
		}
		else
		{

			for (int y = 0; y < clipboard.getSize().getBlockY(); y++) {
				for (int x = 0; x < clipboard.getSize().getBlockX(); x++) {
					for (int z = 0; z < clipboard.getSize().getBlockZ(); z++)
					{
						Location temploc = tempLoc.clone().add(new Location(tempLoc.getWorld(), x, y, z));
						BaseBlock schemBlock = clipboard.getBlock(new Vector(x,y,z));
						Block b = temploc.getBlock();
						if(schemBlock.getId() != 0 && (schemBlock.getId() != b.getTypeId() || schemBlock.getData() != b.getData()))
						{
							queue.addBlockChange(new BlockChange(temploc, schemBlock, new BaseBlock(b.getTypeId(), b.getData())));
						}
						else if(schemBlock.getId() == b.getTypeId() && schemBlock.getData() == b.getData())
							this.blocks.add(b);
					}
				}
			}
		}
	}

	//Events

	@EventHandler
	public void blockBreak(BlockBreakEvent event)
	{
		if(this instanceof BuildZone)
		{
			if(event.getBlock().getLocation().getChunk().equals(this.getLocation().getChunk()))
			{
				if(MemberManager.getMember(event.getPlayer()) == null)
				{
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', bm.getEmpire().getTag() + "&4That doesn't belong to you!"));

					event.setCancelled(true);
				}
				else
				{
					if(ClaimManager.checkClaim(event.getBlock().getLocation(), event.getPlayer()))
					{
						if(this.getCoreLocation().getBlock().getType() != Material.COMMAND)
							this.getCoreLocation().getBlock().setType(Material.COMMAND);
						Member member = MemberManager.getMember(event.getPlayer());
						if(this instanceof Mine)
						{
							if(!this.checkClaim(event.getPlayer(), event.getBlock().getType()))
							{
								event.setCancelled(true);
								event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', bm.getEmpire().getTag() + "&4That doesn't belong to you!"));

							}
						}
						else if(!this.checkClaim(event.getPlayer()))
						{
							event.setCancelled(true);
							event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', bm.getEmpire().getTag() + "&4That doesn't belong to you!"));

						}
					}
				}

			}
		} 
		else if(blocks.size() > 0 && blocks.contains(event.getBlock()))
		{
			if(MemberManager.getMember(event.getPlayer()) == null)
			{
				event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', bm.getEmpire().getTag() + "&4That doesn't belong to you!"));

				event.setCancelled(true);
			}
			else
			{
				if(ClaimManager.checkClaim(event.getBlock().getLocation(), event.getPlayer()))
				{
					if(this.getCoreLocation().getBlock().getType() != Material.COMMAND)
						this.getCoreLocation().getBlock().setType(Material.COMMAND);
					Member member = MemberManager.getMember(event.getPlayer());
					if(!this.checkClaim(event.getPlayer()))
					{
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', bm.getEmpire().getTag() + "&4That doesn't belong to you!"));

					}
				}
			}
		}
	}

	@EventHandler
	public void blockPlace(BlockPlaceEvent event)
	{

		if(this instanceof BuildZone)
		{
			if(event.getBlock().getLocation().getChunk().equals(this.getLocation().getChunk()))
			{
				if(MemberManager.getMember(event.getPlayer()) == null)
				{
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', bm.getEmpire().getTag() + "&4That doesn't belong to you!"));

					event.setCancelled(true);
				}
				else
				{
					if(ClaimManager.checkClaim(event.getBlock().getLocation(), event.getPlayer()))
					{
						if(this.getCoreLocation().getBlock().getType() != Material.COMMAND)
							this.getCoreLocation().getBlock().setType(Material.COMMAND);
						Member member = MemberManager.getMember(event.getPlayer());
						if(!this.checkClaim(event.getPlayer()))
						{
							event.setCancelled(true);
							event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', bm.getEmpire().getTag() + "&4That doesn't belong to you!"));

						}
					}
				}

			}
		} 
		else if(blocks.size() > 0 && blocks.contains(event.getBlock()))
		{
			if(MemberManager.getMember(event.getPlayer()) == null)
			{
				event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', bm.getEmpire().getTag() + "&4That doesn't belong to you!"));

				event.setCancelled(true);
			}
			else
			{
				if(ClaimManager.checkClaim(event.getBlock().getLocation(), event.getPlayer()))
				{
					if(this.getCoreLocation().getBlock().getType() != Material.COMMAND)
						this.getCoreLocation().getBlock().setType(Material.COMMAND);
					Member member = MemberManager.getMember(event.getPlayer());
					if(!this.checkClaim(event.getPlayer()))
					{
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', bm.getEmpire().getTag() + "&4That doesn't belong to you!"));

					}
				}
			}
		}

	}

	@EventHandler
	public void blockUse(PlayerInteractEvent event)
	{
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		if(this instanceof BuildZone)
		{
			if(event.getClickedBlock().getLocation().getChunk().equals(this.getLocation().getChunk()))
			{
				if(MemberManager.getMember(event.getPlayer()) == null)
				{
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', bm.getEmpire().getTag() + "&4That doesn't belong to you!"));

					event.setCancelled(true);
				}
				else
				{
					if(ClaimManager.checkClaim(event.getClickedBlock().getLocation(), event.getPlayer()))
					{
						if(this.getCoreLocation().getBlock().getType() != Material.COMMAND)
							this.getCoreLocation().getBlock().setType(Material.COMMAND);
						Member member = MemberManager.getMember(event.getPlayer());
						if(!this.checkClaim(event.getPlayer()))
						{
							event.setCancelled(true);
							event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', bm.getEmpire().getTag() + "&4That doesn't belong to you!"));

						}
					}
				}

			}
		} 
		else if(this instanceof TownHall && this.blocks.size() > 0 && this.blocks.contains(event.getClickedBlock()))
		{
			if(MemberManager.getMember(event.getPlayer()) == null)
			{
				event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', bm.getEmpire().getTag() + "&4That doesn't belong to you!"));

				event.setCancelled(true);
			}
			else
			{
				if(!ClaimManager.checkClaim(event.getClickedBlock().getLocation(), event.getPlayer()))
				{
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', bm.getEmpire().getTag() + "&4That doesn't belong to you!"));
				}
			}
		}
		else if(blocks.size() > 0 && blocks.contains(event.getClickedBlock()))
		{
			if(MemberManager.getMember(event.getPlayer()) == null)
			{
				event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', bm.getEmpire().getTag() + "&4That doesn't belong to you!"));

				event.setCancelled(true);
			}
			else
			{
				if(ClaimManager.checkClaim(event.getClickedBlock().getLocation(), event.getPlayer()))
				{
					if(this.getCoreLocation().getBlock().getType() != Material.COMMAND)
						this.getCoreLocation().getBlock().setType(Material.COMMAND);
					Member member = MemberManager.getMember(event.getPlayer());
					if(!this.checkClaim(event.getPlayer()))
					{
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', bm.getEmpire().getTag() + "&4That doesn't belong to you!"));

					}
				}
			}

		}

	}

	//Setters
	
	public void setCost(int i)
	{
		this.cost = i;
	}

	public void setMaxLevel(int i)
	{
		this.maxLevel = i;
	}

	public void setCoreBlock()
	{
		if(this instanceof TownHall)
		{
			this.coreblock = new TownHallCore(this, this.getCoreLocation());
			getCoreLocation().getBlock().setType(Material.COMMAND);
		}
		else if(this instanceof Home)
		{
			this.coreblock = new HomeCore(this, this.getCoreLocation());
			this.coreblock.getLocation().getBlock().setType(Material.COMMAND);
		}
		else if(this instanceof Mine)
		{
			this.coreblock = new MineCore((Mine) this, this.getCoreLocation());
			this.coreblock.getLocation().getBlock().setType(Material.COMMAND);
		}
		else if(this instanceof BuildZone)
		{
			this.coreblock = new BuildZoneCore(this, getCoreLocation());
			this.coreblock.getLocation().getBlock().setType(Material.COMMAND);
		}
	}

	public void setLevel(int i)
	{
		if(this instanceof TownHall)
		{
			this.getBuildingManager().getEmpire().broadcastToMembers(ChatColor.translateAlternateColorCodes('&', "&2Empire has levelled up! Now level " + i));
			this.getBuildingManager().getEmpire().getConfig().set("Buildings.0.Level", i);
			this.getBuildingManager().getEmpire().saveConfig();
			this.getBuildingManager().getEmpire().reloadConfig();
		}
		else
		{
			this.getBuildingManager().getEmpire().getConfig().set("Buildings." + id + ".Level", i);
			this.getBuildingManager().getEmpire().saveConfig();
			this.getBuildingManager().getEmpire().reloadConfig();
		}
		this.level = (byte) i;
		if(!(this instanceof BuildZone))
		{
			
		}
	}
	//Getters
	
	public int getCost()
	{
		return this.cost;
	}

	public Location getCoreLocation()
	{
		if(this instanceof BuildZone)
		{
			return this.getLocation().getChunk().getBlock(8, this.getLocation().getBlockY(), 8).getLocation();
		}
		Main main = this.getBuildingManager().getEmpire().getEmpireManager().getMain();
		File schematic = new File(main.getDataFolder() + "/schematics/" + this.name + "_" + this.level + ".schematic");
		Location coreBlock = null;
		try
		{
			CuboidClipboard schem = MCEditSchematicFormat.getFormat(schematic).load(schematic);
			if(this.clipboard == null)
			{
				this.clipboard = schem;
			}
			if(!this.rotated)
			{
				this.rotated = true;
				int rotation = this.direction * 90;
				this.clipboard.rotate2D(rotation);
			}
			int xdiff = clipboard.getOffset().getBlockX();
			int ydiff = clipboard.getOffset().getBlockY();
			int zdiff = clipboard.getOffset().getBlockZ();
			Location tempLoc = this.getLocation().clone().add(new Location(this.getLocation().getWorld(), xdiff, ydiff, zdiff));	
			for (int y = 0; y < clipboard.getSize().getBlockY(); y++) {
				for (int x = 0; x < clipboard.getSize().getBlockX(); x++) {
					for (int z = 0; z < clipboard.getSize().getBlockZ(); z++)
					{
						Location temploc = tempLoc.clone().add(new Location(tempLoc.getWorld(), x, y, z));
						BaseBlock schemBlock = clipboard.getBlock(new Vector(x,y,z));
						if(schemBlock.getId() == 137)
						{
							coreBlock = temploc;
						}
					}
				}
			}
		}
		catch (ArrayIndexOutOfBoundsException | com.sk89q.worldedit.data.DataException | IOException e)
		{
			Bukkit.broadcastMessage("ERORR");
		}
		return coreBlock;
	}

	public UUID getWhoPlaced()
	{
		return this.player;
	}

	public CoreBlock getCoreBlock()
	{
		return this.coreblock;
	}

	public List<Block> getBlocks()
	{
		return this.blocks;
	}

	public Location getLocation()
	{
		return this.loc;
	}

	public BuildQueue getQueue()
	{
		return this.queue;
	}

	public CuboidClipboard getSchematic()
	{
		return this.clipboard;
	}

	public byte getLevel()
	{
		return this.level;
	}

	public String getName()
	{
		return this.name;
	}

	public BuildingManager getBuildingManager()
	{
		return this.bm;
	}

	public int getId() {
		return this.id;
	}
}
