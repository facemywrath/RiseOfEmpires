package facemywrath.riseofempires.empires.buildings.townhall;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import facemywrath.riseofempires.empires.buildings.Building;
import facemywrath.riseofempires.empires.buildings.BuildingManager;
import facemywrath.riseofempires.empires.buildings.CoreBlock;
import facemywrath.riseofempires.empires.claims.Claim;
import facemywrath.riseofempires.empires.claims.ClaimManager;
import facemywrath.riseofempires.empires.members.MemberManager;
import facemywrath.riseofempires.util.FormatLore;
import facemywrath.riseofempires.util.InvItem;
import net.md_5.bungee.api.ChatColor;

public class TownHallCore extends CoreBlock {

	private Inventory inv;
	private Inventory buildInv;

	public TownHallCore(Building building, Location loc)
	{
		super(building, loc);
		Plugin pl = this.getBuilding().getBuildingManager().getEmpire().getEmpireManager().getMain();
		pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new BukkitRunnable()
		{
			public void run()
			{
				setupMainInventory();
				setupBuildingInventory();
			}
		}, 20L);
	}

	public void setupMainInventory()
	{// Set up inventory for core block
		Building building = this.getBuilding();
		Double exp = building.getBuildingManager().getEmpire().getExperience();
		int gold = (int) building.getBuildingManager().getEmpire().getGold();
		int misplacedBlocks = building.getDifferentBlocks().size();
		int upgradeGoldCost[] = {25000,55000,125000,350000,1024000,2048000,4096000,8192000,16384000};
		int upgradeExperienceMin[] = {500000,1000000,2000000,4000000,8000000,16000000,32000000,64000000,128000000};
		String name = building.getBuildingManager().getEmpire().getName();
		if(inv == null)
			inv = Bukkit.createInventory(null, 9, "Town Hall");
		inv.setItem(0, InvItem.invItem(Material.LOG, "&2" + name + " Town Hall", FormatLore.formatLore("&6&lGold: " + gold + "::&6&lLevel: " + building.getLevel() + "::&6&lExperience: " + exp + "::&6&lMembers: " + building.getBuildingManager().getEmpire().getMemberManager().getMembers().size())));
		if(this.getBuilding().getQueue() != null && this.getBuilding().getQueue().isBuilding())
		{
			int amount = this.getBuilding().getQueue().getAmount();
			int timeLeftSeconds = (int) ((this.getBuilding().getQueue().getDelay() * misplacedBlocks / 20.0)/amount);
			int minutes = (int) (timeLeftSeconds/60.0);
			int seconds = (int) (timeLeftSeconds%60);
			double cost = misplacedBlocks * (32.0/this.getBuilding().getQueue().getDelay())*10*amount;
			String timeLeftFormatted = minutes + " minutes, " + seconds + " seconds ";
			inv.setItem(2, InvItem.invItem(Material.WOOD, "&2Speed up 2x (" + amount*(64/this.getBuilding().getQueue().getDelay()) + "x)", FormatLore.formatLore("&5Blocks left: " + misplacedBlocks + "::Time left: " + timeLeftFormatted + "::&6&lCost: " + cost + " Gold")));
		}else
			inv.setItem(2, InvItem.invItem(Material.WOOD, "&2Regenerate Building", FormatLore.formatLore("&5Blocks misplaced: " + misplacedBlocks + "::&6&lCost: " + building.getDifferentBlocks().size()*10 + " Gold.")));
		if(building.getLevel() < 10)
			inv.setItem(4, InvItem.invItem(Material.GOLD_INGOT, "&2Upgrade Building", FormatLore.formatLore("&6&lUpgrade building to level " + (building.getLevel() + 1) + "::&6&lCost: ::&5- " + upgradeGoldCost[building.getLevel()-1] + " Gold::&5- " + upgradeExperienceMin[this.getBuilding().getLevel()-1] + " Experience Minimum::")));
		ItemStack woodaxe = InvItem.invItem(Material.WOOD_AXE, "&2Buildings Menu", FormatLore.formatLore("&6&lOpen the buildings menu.::&6&lPurchase other pre-made buildings."));
		net.minecraft.server.v1_11_R1.ItemStack nmsitem = CraftItemStack.asNMSCopy(woodaxe);
		nmsitem.getTag().setByte("hideFlags", (byte) 2);
		woodaxe = CraftItemStack.asBukkitCopy(nmsitem);
		inv.setItem(6, woodaxe);
		inv.setItem(8, InvItem.invItem(Material.BARRIER, "&4&oClose Menu"));

	}

	public void setupBuildingInventory()
	{// Set up inventory for core block
		Building building = this.getBuilding();
		int gold = (int) building.getBuildingManager().getEmpire().getGold();
		int exp = (int) building.getBuildingManager().getEmpire().getExperience();
		int level = building.getLevel();
		int buildings = building.getBuildingManager().getBuildings().size();
		int goldmines = building.getBuildingManager().getMines().size();
		int homes = building.getBuildingManager().getHomes().size();
		int outposts = building.getBuildingManager().getOutposts().size();
		String name = building.getBuildingManager().getEmpire().getName();
		int buildingamts[] = {building.getBuildingManager().getHomes().size(), building.getBuildingManager().getBuildZones().size(), building.getBuildingManager().getShopAmount(), building.getBuildingManager().getOutposts().size(), building.getBuildingManager().getBreweries().size(), building.getBuildingManager().getMines().size(), building.getBuildingManager().getLibraries().size()};
		int buildingMaxes[] = {10*level, 10*level, 4*level, level, level, 5*level, level};
		if(buildInv == null)
			buildInv = Bukkit.createInventory(null, 27, "Buildings");
		buildInv.setItem(9, InvItem.invItem(Material.LOG, "&2" + name + " Stats", FormatLore.formatLore("&6&lGold: " + gold + "::&6&lLevel: " + level + "::&6&l::Experience: " + exp + "::&6&lBuildings: " + building.getBuildingManager().getBuildings().size() + "::&5&l-Homes: " + homes + "::&5&l-Goldmines: " + goldmines + "::&5&l-Outposts: " + outposts)));
		switch(level)
		{
		case 10:
			buildInv.setItem(23, InvItem.invItem(Material.DIAMOND_ORE, "Diamond Mine (" + buildingamts[5] + "/" + buildingMaxes[5] + ")", FormatLore.formatLore("&6&lPurchase a mine that::&6&lgenerates Diamond Ore::&5&lCost: 1,000,000 Gold")));
		case 9:
			buildInv.setItem(22, InvItem.invItem(Material.GOLD_ORE, "Goldmine (" + buildingamts[5] + "/" + buildingMaxes[5] + ")", FormatLore.formatLore("&6&lPurchase a mine that::&6&lgenerates Gold Ore::&5&lCost: 250,000 Gold")));
		case 8:
			buildInv.setItem(21, InvItem.invItem(Material.REDSTONE_ORE, "Redstone Mine (" + buildingamts[5] + "/" + buildingMaxes[5] + ")", FormatLore.formatLore("&6&lPurchase a mine that::&6&lgenerates Redstone Ore::&5&lCost: 125,000 Gold")));
		case 7:
			buildInv.setItem(20, InvItem.invItem(Material.IRON_ORE, "Iron Mine (" + buildingamts[5] + "/" + buildingMaxes[5] + ")", FormatLore.formatLore("&6&lPurchase a mine that::&6&lgenerates Iron Ore::&5&lCost: 62,500 Gold")));
			buildInv.setItem(15, InvItem.invItem(Material.BOOK, "Library (" + buildingamts[6] + "/" + buildingMaxes[6] + ")", FormatLore.formatLore("&4&oDOES NOT WORK::&6&lPurchase a library for::&6&lenchanting and scribing::&5&lCost: 100,000 Gold")));
		case 6:
			buildInv.setItem(14, InvItem.invItem(Material.COAL_ORE, "Coal Mine (" + buildingamts[5] + "/" + buildingMaxes[5] + ")", FormatLore.formatLore("&6&lPurchase a mine that::&6&lgenerates Coal Ore::&5&lCost: 37,250 Gold")));
		case 5:
			buildInv.setItem(13, InvItem.invItem(Material.POTION, "Brewery (" + buildingamts[4] + "/" + buildingMaxes[4] + ")", FormatLore.formatLore("&4&oDOES NOT WORK::&6&lPurchase a brewery for::&6&lcreating potions::&5&lCost: 62,500 Gold")));
		case 4:
			buildInv.setItem(12, InvItem.invItem(Material.BEDROCK, "Outpost (" + buildingamts[3] + "/" + buildingMaxes[3] + ")", FormatLore.formatLore("&4&oDOES NOT WORK::&6&lPurchase an outpost::&6&lto claim land outside your territory::&5&lCost: 100,000 Gold")));
		case 3:
			buildInv.setItem(11, InvItem.invItem(Material.IRON_INGOT, "Resource Shop (" + buildingamts[2] + "/" + buildingMaxes[2] + ")", FormatLore.formatLore("&4&oDOES NOT WORK::&6&lPurchase a shop::&6&lfor selling or buying resources::&5&lCost: 75,000 Gold")));
			buildInv.setItem(6, InvItem.invItem(Material.SMOOTH_BRICK, "Building Block Shop (" + buildingamts[2] + "/" + buildingMaxes[2] + ")", FormatLore.formatLore("&4&oDOES NOT WORK::&6&lPurchase a shop::&6&lfor selling or buying::&6&lbuilding materials::&5&lCost: 75,000 Gold")));
			buildInv.setItem(5, InvItem.invItem(Material.STONE_PICKAXE, "Tool Shop (" + buildingamts[2] + "/" + buildingMaxes[2] + ")", FormatLore.formatLore("&4&oDOES NOT WORK::&6&lPurchase a shop::&6&lfor selling or buying tools::&5&lCost: 75,000 Gold")));
			buildInv.setItem(4, InvItem.invItem(Material.COOKED_BEEF, "Restaurant (" + buildingamts[2] + "/" + buildingMaxes[2] + ")", FormatLore.formatLore("&4&oDOES NOT WORK::&6&lPurchase a shop::&6&lfor selling or buying food::&5&lCost: 75,000 Gold")));
		case 2:
			buildInv.setItem(3, InvItem.invItem(Material.WORKBENCH, "BuildZone (" + buildingamts[1] + "/" + buildingMaxes[1] + ")", FormatLore.formatLore("&6&lPurchase a single chunk plot::&6&lfor claiming areas personally::&5&lCost: 30,000 Gold")));
		case 1:
			buildInv.setItem(2, InvItem.invItem(Material.LOG, "Home (" + buildingamts[0] + "/" + buildingMaxes[0] + ")", FormatLore.formatLore("&6&lPurchase a small home::&6&lfor personal storage::&5&lCost: 5,000 Gold")));
		}
		buildInv.setItem(17, InvItem.invItem(Material.BARRIER, "&4&oClose Menu"));
	}

	//Events

	@EventHandler
	public void structureBreak(BlockBreakEvent event)
	{
		if(!event.getBlock().getLocation().equals(this.getLocation()))
			return;
		if(MemberManager.getMember(event.getPlayer()) != null && MemberManager.getMember(event.getPlayer()).getEmpire().equals(this.getBuilding().getBuildingManager().getEmpire()))
		if(event.getPlayer().getUniqueId().equals(this.getBuilding().getWhoPlaced()) || MemberManager.toInt(MemberManager.getMember(event.getPlayer()).getRank()) > 2)
		{
			this.getBuilding().destroy();
		}
	}

	@EventHandler
	public void inventoryClick(InventoryClickEvent event)
	{ // Function for controlling the town hall's inventory
		Building building = this.getBuilding();
		if(event.getClickedInventory() == null)
			return;
		Inventory inventory = event.getClickedInventory();
		if(!inventory.equals(inv) && !inventory.equals(buildInv))
			return;
		int slot = event.getSlot();
		event.setCancelled(true);
		if(inventory.getTitle().equals("Town Hall"))
			switch(slot)
			{
			case 2:
				if(this.getBuilding().getQueue() != null && this.getBuilding().getQueue().isBuilding())
				{
					if(this.getBuilding().getQueue().getDelay() != 1L)
					{
						Long delay = this.getBuilding().getQueue().getDelay();
						int cost = (int) (32/delay*10);
						if(this.getBuilding().getBuildingManager().getEmpire().getEmpireManager().getMain().getEconomy().getBalance(event.getWhoClicked().getName()) >= cost)
						{
							this.getBuilding().getBuildingManager().getEmpire().getEmpireManager().getMain().getEconomy().withdrawPlayer(event.getWhoClicked().getName(), cost);
							event.getWhoClicked().sendMessage(ChatColor.DARK_GREEN + "Construction speed doubled");
							this.getBuilding().getQueue().setDelay((int) (this.getBuilding().getQueue().getDelay()/2.0));
						}
					}else{
						Long delay = this.getBuilding().getQueue().getDelay();
						int amount = this.getBuilding().getQueue().getAmount();
						int cost = (int) (amount*(32/delay)*10);
						if(this.getBuilding().getBuildingManager().getEmpire().getEmpireManager().getMain().getEconomy().getBalance(event.getWhoClicked().getName()) >= cost)
						{
							this.getBuilding().getBuildingManager().getEmpire().getEmpireManager().getMain().getEconomy().withdrawPlayer(event.getWhoClicked().getName(), cost);
							event.getWhoClicked().sendMessage(ChatColor.DARK_GREEN + "Construction speed doubled");
							this.getBuilding().getQueue().setAmount((int) (this.getBuilding().getQueue().getAmount()*2.0));
						}
					}
				}else{
					building.regenerate();
				}
				break;
			case 4:
				int gold = (int) building.getBuildingManager().getEmpire().getGold();
				int exp = (int) building.getBuildingManager().getEmpire().getExperience();
				int level = building.getLevel();
				int upgradeGoldCost[] = {25000,55000,125000,350000,1024000,2048000,4096000,8192000,16384000};
				int upgradeExperienceMin[] = {500000,1000000,2000000,4000000,8000000,16000000,32000000,64000000,128000000};
				if(gold >= upgradeGoldCost[level-1] && exp > upgradeExperienceMin[level-1])
				{
					building.getBuildingManager().getEmpire().addMoney(-1*upgradeGoldCost[level-1]);
					building.setLevel(level+1);
					int oldClaimSize = level-1;
					level+=1;
					this.getBuilding().getCoreLocation();					
					int newClaimSize = level;
					for(int x = (0-newClaimSize); x <= newClaimSize; x++)
					{
						for(int z = (0-newClaimSize); z <= newClaimSize; z++)
						{
							Location loc = this.getBuilding().getBuildingManager().getEmpire().getLocation();
							Chunk chunk = loc.getChunk();
							int newx = x+chunk.getX();
							int newz = z+chunk.getZ();
							ClaimManager cm = this.getBuilding().getBuildingManager().getEmpire().getClaimManager();
							if(ClaimManager.getEmpireAt(loc.getWorld().getChunkAt(newx, newz)) == null)
								cm.addClaim(new Claim(cm, newx, newz));
						}
					}
					this.setupMainInventory();
					event.getWhoClicked().openInventory(this.inv);
				}
				break;
			case 6:
				this.setupBuildingInventory();
				event.getWhoClicked().openInventory(this.buildInv);
				break;
			case 8:
				event.getWhoClicked().closeInventory();
				break;
			}
		if(inventory.getTitle().equals("Buildings"))
			switch(slot)
			{
			case 2:
				if(this.getBuilding().getBuildingManager().getEmpire().getEmpireManager().getMain().getEconomy().getBalance(event.getWhoClicked().getName()) >= 5000)
				{
					this.getBuilding().getBuildingManager().getEmpire().getEmpireManager().getMain().getEconomy().withdrawPlayer(event.getWhoClicked().getName(), 5000);
					event.getWhoClicked().getInventory().addItem(BuildingManager.getBuildingItem("Home"));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&2Purchased a &bHome &2for &b5,000 Gold."));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&7New Personal Balance: " +  this.getBuilding().getBuildingManager().getEmpire().getEmpireManager().getMain().getEconomy().getBalance(event.getWhoClicked().getName())));
				}
				break;
			case 3:
				if(this.getBuilding().getLevel() >= 2 && this.getBuilding().getBuildingManager().getEmpire().getEmpireManager().getMain().getEconomy().getBalance(event.getWhoClicked().getName()) >= 30000)
				{
					this.getBuilding().getBuildingManager().getEmpire().getEmpireManager().getMain().getEconomy().withdrawPlayer(event.getWhoClicked().getName(), 30000);
					event.getWhoClicked().getInventory().addItem(BuildingManager.getBuildingItem("BuildZone"));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&2Purchased a &bBuildZone &2for &b30,000 Gold."));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&7New Personal Balance: " +  this.getBuilding().getBuildingManager().getEmpire().getEmpireManager().getMain().getEconomy().getBalance(event.getWhoClicked().getName())));
				}
				break;
			case 4:
				if(this.getBuilding().getLevel() >= 3 && this.getBuilding().getBuildingManager().getEmpire().getGold() >= 75000)
				{
					this.getBuilding().getBuildingManager().getEmpire().subtractGold(75000);
					event.getWhoClicked().getInventory().addItem(BuildingManager.getBuildingItem("Restaurant"));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&2Purchased a &bRestaurant &2for &b75,000 Gold."));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&7New Empire Balance: " +  this.getBuilding().getBuildingManager().getEmpire().getGold()));
				}
				break;
			case 5:
				if(this.getBuilding().getLevel() >= 3 && this.getBuilding().getBuildingManager().getEmpire().getGold() >= 75000)
				{
					this.getBuilding().getBuildingManager().getEmpire().subtractGold(75000);
					event.getWhoClicked().getInventory().addItem(BuildingManager.getBuildingItem("ToolShop"));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&2Purchased a &bTool Shop &2for &b75,000 Gold."));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&7New Empire Balance: " +  this.getBuilding().getBuildingManager().getEmpire().getGold()));
				}
				break;
			case 6:
				if(this.getBuilding().getLevel() >= 3 && this.getBuilding().getBuildingManager().getEmpire().getGold() >= 75000)
				{
					this.getBuilding().getBuildingManager().getEmpire().subtractGold(75000);
					event.getWhoClicked().getInventory().addItem(BuildingManager.getBuildingItem("BuildingBlockShop"));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&2Purchased a &bBuilding Block Shop &2for &b75,000 Gold."));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&7New Empire Balance: " +  this.getBuilding().getBuildingManager().getEmpire().getGold()));
				}
				break;
			case 11:
				if(this.getBuilding().getLevel() >= 3 && this.getBuilding().getBuildingManager().getEmpire().getGold() >= 75000)
				{
					this.getBuilding().getBuildingManager().getEmpire().subtractGold(75000);
					event.getWhoClicked().getInventory().addItem(BuildingManager.getBuildingItem("ResourceShop"));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&2Purchased a &bResource Shop &2for &b75,000 Gold."));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&7New Empire Balance: " +  this.getBuilding().getBuildingManager().getEmpire().getGold()));
				}
				break;
			case 12:
				if(this.getBuilding().getLevel() >= 4 && this.getBuilding().getBuildingManager().getEmpire().getGold() >= 100000)
				{
					this.getBuilding().getBuildingManager().getEmpire().subtractGold(100000);
					event.getWhoClicked().getInventory().addItem(BuildingManager.getBuildingItem("Outpost"));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&2Purchased a &bOutpost &2for &b100,000 Gold."));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&7New Empire Balance: " +  this.getBuilding().getBuildingManager().getEmpire().getGold()));
				}
				break;
			case 13:
				if(this.getBuilding().getLevel() >= 5 && this.getBuilding().getBuildingManager().getEmpire().getGold() >= 62500)
				{
					this.getBuilding().getBuildingManager().getEmpire().subtractGold(62500);
					event.getWhoClicked().getInventory().addItem(BuildingManager.getBuildingItem("Brewery"));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&2Purchased a &bBrewery &2for &b62,500 Gold."));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&7New Empire Balance: " +  this.getBuilding().getBuildingManager().getEmpire().getGold()));
				}
				break;
			case 14:
				if(this.getBuilding().getLevel() >= 6 && this.getBuilding().getBuildingManager().getEmpire().getGold() >= 37250)
				{
					this.getBuilding().getBuildingManager().getEmpire().subtractGold(37250);
					event.getWhoClicked().getInventory().addItem(BuildingManager.getBuildingItem("Coalmine"));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&2Purchased a &bCoalmine &2for &b37,250 Gold."));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&7New Empire Balance: " +  this.getBuilding().getBuildingManager().getEmpire().getGold()));
				}
				break;
			case 15:
				if(this.getBuilding().getLevel() >= 7 && this.getBuilding().getBuildingManager().getEmpire().getGold() >= 100000)
				{
					this.getBuilding().getBuildingManager().getEmpire().subtractGold(100000);
					event.getWhoClicked().getInventory().addItem(BuildingManager.getBuildingItem("Library"));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&2Purchased a &bLibrary &2for &b100,000 Gold."));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&7New Empire Balance: " +  this.getBuilding().getBuildingManager().getEmpire().getGold()));
				}
				break;
			case 20:
				if(this.getBuilding().getLevel() >= 7 && this.getBuilding().getBuildingManager().getEmpire().getGold() >= 62500)
				{
					this.getBuilding().getBuildingManager().getEmpire().subtractGold(62500);
					event.getWhoClicked().getInventory().addItem(BuildingManager.getBuildingItem("Ironmine"));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&2Purchased a &bIronmine &2for &b62,500 Gold."));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&7New Empire Balance: " +  this.getBuilding().getBuildingManager().getEmpire().getGold()));
				}
				break;
			case 21:
				if(this.getBuilding().getLevel() >= 8 && this.getBuilding().getBuildingManager().getEmpire().getGold() >= 125000)
				{
					this.getBuilding().getBuildingManager().getEmpire().subtractGold(125000);
					event.getWhoClicked().getInventory().addItem(BuildingManager.getBuildingItem("Redstonemine"));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&2Purchased a &bRedstonemine &2for &b125,000 Gold."));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&7New Empire Balance: " +  this.getBuilding().getBuildingManager().getEmpire().getGold()));
				}
				break;
			case 22:
				if(this.getBuilding().getLevel() >= 9 && this.getBuilding().getBuildingManager().getEmpire().getGold() >= 250000)
				{
					this.getBuilding().getBuildingManager().getEmpire().subtractGold(250000);
					event.getWhoClicked().getInventory().addItem(BuildingManager.getBuildingItem("Goldmine"));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&2Purchased a &bGoldmine &2for &b250,000 Gold."));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&7New Empire Balance: " +  this.getBuilding().getBuildingManager().getEmpire().getGold()));
				}
				break;
			case 23:
				if(this.getBuilding().getLevel() >= 10 && this.getBuilding().getBuildingManager().getEmpire().getGold() >= 1000000)
				{
					this.getBuilding().getBuildingManager().getEmpire().subtractGold(1000000);
					event.getWhoClicked().getInventory().addItem(BuildingManager.getBuildingItem("Diamondmine"));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&2Purchased a &bDiamondmine &2for &b1,000,000 Gold."));
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&7New Empire Balance: " +  this.getBuilding().getBuildingManager().getEmpire().getGold()));
				}
				break;
			case 17:
				event.getWhoClicked().closeInventory();
				break;
			}
		setupMainInventory();
		setupBuildingInventory();
	}

	@EventHandler
	public void openInventory(PlayerInteractEvent event)
	{ // Open the inventory when rightclicked
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		if(!event.getClickedBlock().getLocation().equals(this.getLocation()))
			return;
		if(event.getClickedBlock().getType() != Material.COMMAND)
			return;
		if(MemberManager.getMember(event.getPlayer()) != null && MemberManager.getMember(event.getPlayer()).getEmpire().equals(this.getBuilding().getBuildingManager().getEmpire()))
		{
			this.setupMainInventory();
			inv.getViewers().add(event.getPlayer());
			event.getPlayer().openInventory(inv);
		}
	}
}
