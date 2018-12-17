package facemywrath.riseofempires.empires.buildings.home;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import facemywrath.riseofempires.empires.buildings.Building;
import facemywrath.riseofempires.empires.buildings.BuildingManager;
import facemywrath.riseofempires.empires.buildings.CoreBlock;
import facemywrath.riseofempires.empires.members.MemberManager;
import facemywrath.riseofempires.main.Main;
import facemywrath.riseofempires.util.FormatLore;
import facemywrath.riseofempires.util.InvItem;
import net.md_5.bungee.api.ChatColor;

public class HomeCore extends CoreBlock{

	private Inventory inv;

	public HomeCore(Building building, Location loc) {
		super(building, loc);
		Plugin pl = this.getBuilding().getBuildingManager().getEmpire().getEmpireManager().getMain();
		pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new BukkitRunnable()
		{
			public void run()
			{
				setupMainInventory();
			}
		}, 20L);
	}

	public void setupMainInventory()
	{// Set up inventory for core block
		Building building = this.getBuilding();
		Double exp = building.getBuildingManager().getEmpire().getExperience();
		int gold = (int) building.getBuildingManager().getEmpire().getGold();
		int misplacedBlocks = building.getDifferentBlocks().size();
		int upgradeGoldCost[] = {15000,30000};
		String name = building.getBuildingManager().getEmpire().getName();
		String playerName = StringUtils.capitalise(Bukkit.getOfflinePlayer(this.getBuilding().getWhoPlaced()).getName());
		if(inv == null)
			inv = Bukkit.createInventory(null, 9, playerName + "'s Home");
		inv.setItem(0, InvItem.invItem(Material.LOG, "&2" + playerName + "'s Home", FormatLore.formatLore("&6&lGold: " + gold + "::&6&lLevel: " + building.getLevel() + "::&6&lExperience: " + exp + "::&6&lMembers: " + building.getBuildingManager().getEmpire().getMemberManager().getMembers().size())));
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
		if(building.getLevel() < 3)
			inv.setItem(4, InvItem.invItem(Material.GOLD_INGOT, "&2Upgrade Building", FormatLore.formatLore("&6&lUpgrade building to level " + (building.getLevel() + 1) + "::&6&lCost: ::&5- " + upgradeGoldCost[building.getLevel()-1] + " Gold")));
		else
			inv.setItem(4, null);
		inv.setItem(7, InvItem.invItem(Material.STONE_SPADE, "&4&oPickup Building"));
		inv.setItem(8, InvItem.invItem(Material.BARRIER, "&4&oSell Building", FormatLore.formatLore("&6&lSell Price: " + 1250*Math.pow(2, this.getBuilding().getLevel()))));

	}

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
		if(!inventory.equals(inv))
			return;
		int slot = event.getSlot();
		Main main = this.getBuilding().getBuildingManager().getEmpire().getEmpireManager().getMain();
		event.setCancelled(true);
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
			double gold = main.getEconomy().getBalance(event.getWhoClicked().getName());
			int level = building.getLevel();
			int upgradeGoldCost[] = {15000,30000};
			if(level < 3 && this.getBuilding().getBuildingManager().getTownHall().getLevel() > this.getBuilding().getLevel() && gold >= upgradeGoldCost[level-1])
			{
				event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Upgraded &bHome &2to level &b" + (level+1) + " &2for &b$" + upgradeGoldCost[level-1]));
				main.getEconomy().withdrawPlayer(event.getWhoClicked().getName(), upgradeGoldCost[level-1]);
				building.setLevel(level+1);
				this.setupMainInventory();
				this.getBuilding().getCoreLocation();
				event.getWhoClicked().openInventory(this.inv);
			}
			break;
		case 7:
			if(this.getBuilding().checkClaim((Player) event.getWhoClicked()))
			{
				if(!this.getBuilding().getQueue().isBuilding())
				{
					event.getWhoClicked().getInventory().addItem(BuildingManager.getBuildingItem(this.getBuilding().getName(), this.getBuilding().getLevel()));
					event.getWhoClicked().closeInventory();
					this.getBuilding().destroy();
				}
			}
			break;
		case 8:
			if(this.getBuilding().checkClaim((Player) event.getWhoClicked()))
			{
				if(!this.getBuilding().getQueue().isBuilding())
				{
					event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&2Sold &bHome &2for &b$" + 1250*Math.pow(2, this.getBuilding().getLevel())));
					this.getBuilding().getBuildingManager().getEmpire().getEmpireManager().getMain().getEconomy().depositPlayer(event.getWhoClicked().getName(), 1250*Math.pow(2, this.getBuilding().getLevel()));
					event.getWhoClicked().closeInventory();
					this.getBuilding().destroy();
				}
			}
			break;
		}
		setupMainInventory();
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
