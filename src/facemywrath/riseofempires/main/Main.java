package facemywrath.riseofempires.main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;

import facemywrath.riseofempires.commands.CommandEmpire;
import facemywrath.riseofempires.commands.CommandFinishTutorial;
import facemywrath.riseofempires.commands.CommandMap;
import facemywrath.riseofempires.empires.EmpireManager;
import facemywrath.riseofempires.empires.members.MemberManager;
import facemywrath.riseofempires.events.BlockBreak;
import facemywrath.riseofempires.events.BlockPlace;
import facemywrath.riseofempires.events.ChatEvent;
import facemywrath.riseofempires.events.EntityDamageByEntity;
import facemywrath.riseofempires.events.HeroExp;
import facemywrath.riseofempires.events.MobSpawn;
import facemywrath.riseofempires.events.PlayerJoin;
import facemywrath.riseofempires.events.PlayerMove;
import facemywrath.riseofempires.events.PlayerUse;
import facemywrath.riseofempires.managers.FileManager;
import facemywrath.riseofempires.managers.HeroesManager;
import facemywrath.riseofempires.managers.UserManager;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

	private int experienceModifier = 0; // Heroes experience multiplier
	private FileManager fm = new FileManager(this); // Object for managing the config file
	private EmpireManager em; // Object for managing the list of empires
	private Economy econ; // Object for hooking into the Vault economy system
	private BlockPlace bpe = new BlockPlace(this); // Block place listener 
	private BlockBreak bbe = new BlockBreak(this); // Block break listener
	private ChatEvent ce = new ChatEvent(this); // Player chat listener
	private PlayerUse pu = new PlayerUse(this); // Player interact listener
	private PlayerJoin pj = new PlayerJoin(this); // Player interact listener
	private PlayerMove pm = new PlayerMove(this); // Player move listener 
	private EntityDamageByEntity edbe = new EntityDamageByEntity(this); // Entity damage by entity listener
	private MobSpawn ms = new MobSpawn(this); // Mob spawn listener 
	private CommandEmpire cmde = new CommandEmpire(this); // Command class
	private CommandFinishTutorial cft = new CommandFinishTutorial(this); // Command class
	private CommandMap cmdmap = new CommandMap(this);
	private HeroesManager hm = new HeroesManager(this);
	private UserManager um = new UserManager(this);

	public void onEnable()
	{ // Function run when plugin is enabled
		if (!setupEconomy()) { // Sets up the Vault economy
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4Error: Vault not found. RiseOfEmpires has been disabled."));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		registerEvents(); // Registers event classes
		registerCommands(); // Registers commands
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new BukkitRunnable()
		{
			public void run()
			{
				em = new EmpireManager(Main.getPlugin(Main.class));
				em.loadEmpires();
			}
		}, 20L);
	}

	public void onDisable()
	{

	}

	public void registerCommands()
	{
		getCommand("empire").setExecutor(cmde);
		getCommand("map").setExecutor(cmdmap);
		getCommand("prestige").setExecutor(hm.getPrestigeListener());
		getCommand("xpgive").setExecutor(hm.getXpCommand());
		getCommand("finishtut").setExecutor(cft);
	}

	public void registerEvents()
	{
		Bukkit.getPluginManager().registerEvents(bpe, this);
		Bukkit.getPluginManager().registerEvents(bbe, this);
		Bukkit.getPluginManager().registerEvents(ce, this);
		Bukkit.getPluginManager().registerEvents(pu, this);
		Bukkit.getPluginManager().registerEvents(hm.getExpListener(), this);
		Bukkit.getPluginManager().registerEvents(pm, this);
		Bukkit.getPluginManager().registerEvents(edbe, this);
		Bukkit.getPluginManager().registerEvents(ms, this);
		Bukkit.getPluginManager().registerEvents(pj, this);
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public void addExperienceModifier()
	{
		this.experienceModifier+=1;
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] Experience Modifier increased for 20 minutes! (" + ((int) (Math.pow(1.25, this.experienceModifier)*100.0))/100.0 + "x)"));
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new BukkitRunnable()
		{
			public void run()
			{
				experienceModifier -= 1;
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &4Experience modifier ended. Current modifier: " + ((int) (Math.pow(1.25, experienceModifier)*100.0))/100.0 + "x"));
			}
		}, 24000L);
	}

	//Getters

	public int getExperienceModifier()
	{
		return this.experienceModifier;
	}

	public Economy getEconomy()
	{
		return econ;
	}

	public BlockPlace getBlockPlaceListener()
	{
		return this.bpe;
	}

	public BlockBreak getBlockBreakListener()
	{
		return this.bbe;
	}

	public ChatEvent getChatEventListener()
	{
		return this.ce;
	}

	public PlayerUse getPlayerUseListener()
	{
		return this.pu;
	}

	public EmpireManager getEmpireManager()
	{
		return this.em;
	}

	public UserManager getUserManager()
	{
		return this.um;
	}

	public FileManager getFileManager()
	{
		return this.fm;
	}

	public CommandEmpire getCommandObj()
	{
		return this.cmde;
	}

	public void reloadEM() {
		this.em = new EmpireManager(this);
		em.loadEmpires();
	}

}
