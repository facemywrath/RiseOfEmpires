package facemywrath.riseofempires.empires.buildings.mine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import facemywrath.riseofempires.empires.buildings.Building;
import facemywrath.riseofempires.empires.buildings.CoreBlock;
import facemywrath.riseofempires.empires.members.MemberManager;
import facemywrath.riseofempires.util.FormatLore;
import facemywrath.riseofempires.util.InvItem;
import net.md_5.bungee.api.ChatColor;

public class MineCore extends CoreBlock{

	private Inventory inv;
	private String mineType;
	private Mine mine;

	public MineCore(Mine mine, Location loc) {
		super(mine, loc);
		this.mine = mine;
		mineType = mine.getType();
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
		Plugin pl = this.getBuilding().getBuildingManager().getEmpire().getEmpireManager().getMain();
		Mine mine = (Mine) getBuilding();
		Building building = getBuilding();
		Double exp = building.getBuildingManager().getEmpire().getExperience();
		int gold = (int) building.getBuildingManager().getEmpire().getGold();
		int misplacedBlocks = building.getDifferentBlocks().size();
		int upgradeGoldCost[] = {10000,20000,80000,160000,320000,640000,1280000,2560000,5120000};
		int upgradeExperienceMin[] = {5000,12500,31250,78125,195312,488281,1220703,3051757,7629394};
		Material mat = mine.getMaterial();
		String name = building.getBuildingManager().getEmpire().getName();
		String playerName = StringUtils.capitalise(Bukkit.getOfflinePlayer(getBuilding().getWhoPlaced()).getName());
		if(inv == null)
			inv = Bukkit.createInventory(null, 9, mineType);
		inv.setItem(0, InvItem.invItem(mat, "&2" + mineType, FormatLore.formatLore("&6&lGold: " + gold + "::&6&lLevel: " + building.getLevel() + "::&6&lExperience: " + exp + "::&6&lMembers: " + building.getBuildingManager().getEmpire().getMemberManager().getMembers().size() + "::Next Regeneration: " + mine.getTimeLeft())));
		if(getBuilding().getQueue() != null && getBuilding().getQueue().isBuilding())
		{
			int amount = getBuilding().getQueue().getAmount();
			int timeLeftSeconds = (int) ((getBuilding().getQueue().getDelay() * misplacedBlocks / 20.0)/amount);
			int minutes = (int) (timeLeftSeconds/60.0);
			int seconds = (int) (timeLeftSeconds%60);
			double cost = misplacedBlocks * (32.0/getBuilding().getQueue().getDelay())*10*amount;
			String timeLeftFormatted = minutes + " minutes, " + seconds + " seconds ";
			inv.setItem(2, InvItem.invItem(Material.WOOD, "&2Speed up 2x (" + amount*(64/getBuilding().getQueue().getDelay()) + "x)", FormatLore.formatLore("&5Blocks left: " + misplacedBlocks + "::Time left: " + timeLeftFormatted + "::&6&lCost: " + cost + " Gold")));
		}else
			inv.setItem(2, InvItem.invItem(Material.WOOD, "&2Regenerate Building", FormatLore.formatLore("&5Blocks misplaced: " + misplacedBlocks + "::&6&lCost: " + building.getDifferentBlocks().size()*10 + " Gold.")));
		if(building.getLevel() < 10)
			inv.setItem(4, InvItem.invItem(Material.GOLD_INGOT, "&2Upgrade Building", FormatLore.formatLore("&6&lUpgrade building to level " + (building.getLevel() + 1) + "::&6&lCost: ::&5- " + upgradeGoldCost[building.getLevel()-1] + " Gold::&5- " + upgradeExperienceMin[getBuilding().getLevel()-1] + " Experience Minimum::")));
		inv.setItem(8, InvItem.invItem(Material.BARRIER, "&4&oClose Menu"));
	}

	@EventHandler
	public void structureBreak(BlockBreakEvent event)
	{
		if(!event.getBlock().getLocation().equals(this.getLocation()))
			return;
		this.getBuilding().destroy();
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
			int gold = (int) building.getBuildingManager().getEmpire().getGold();
			int level = building.getLevel();
			List<Integer> upgradeGoldCost = new ArrayList<>();
			switch(mineType){
			case "Coalmine":
				upgradeGoldCost = Arrays.asList(37250,50000,100000,150000,250000,500000,750000,1000000,1000000,1000000);
				break;
			case "Ironmine":
				upgradeGoldCost = Arrays.asList(62750,75000,125000,165000,275000,500000,750000,1125000,1300000,1500000);
				break;
			case "Goldmine":
				upgradeGoldCost = Arrays.asList(125000,150000,200000,250000,400000,600000,800000,1000000,1500000,2000000);
				break;
			case "Redstonemine":
				upgradeGoldCost = Arrays.asList(250000,300000,400000,500000,700000,850000,1000000,1000000,1000000,1500000);
				break;
			case "Diamondmine":
				upgradeGoldCost = Arrays.asList(1000000,1000000,2000000,2000000,4000000,4000000,4000000,4000000,6000000,6000000);
				break;
			}
			if(level < 10 && this.getBuilding().getBuildingManager().getTownHall().getLevel() > this.getBuilding().getLevel() && gold >= upgradeGoldCost.get(level-1))
			{
				building.getBuildingManager().getEmpire().subtractGold(upgradeGoldCost.get(level-1));
				building.setLevel(level+1);
				event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getBuilding().getBuildingManager().getEmpire().getTag() + "&2Now regenerate to update the schematic."));
				this.setupMainInventory();
				event.getWhoClicked().openInventory(this.inv);
			}
			break;
		case 8:
			event.getWhoClicked().closeInventory();
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
