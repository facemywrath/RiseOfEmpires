package facemywrath.riseofempires.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Monster;

import facemywrath.riseofempires.empires.Empire;
import facemywrath.riseofempires.empires.EmpireType;
import facemywrath.riseofempires.empires.claims.ClaimManager;
import facemywrath.riseofempires.main.Main;
import facemywrath.riseofempires.util.PerlinNoise;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.AbstractPlayer;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import me.libraryaddict.disguise.DisguiseAPI;

public class MobSpawn implements Listener {

	private Main main;

	public MobSpawn(Main main)
	{
		this.main = main;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void CreatureSpawn(CreatureSpawnEvent e)
	{
		if(ClaimManager.getEmpireAt(e.getLocation().getChunk()) != null)
		{
			Empire emp = ClaimManager.getEmpireAt(e.getLocation().getChunk());
			if(emp.getType() != EmpireType.EMPIRE)
			{
				e.setCancelled(true);
			}
		}
		int level = PerlinNoise.getLevel(e.getLocation().getChunk());
		if(main.getServer().getPluginManager().getPlugin("MythicMobs") != null)
		{
			main.getServer().getScheduler().scheduleSyncDelayedTask(main, new BukkitRunnable()
			{
				public void run()
				{
					if(e.getEntity() != null && e.getEntity() instanceof org.bukkit.entity.Monster && !MythicMobs.inst().getAPIHelper().isMythicMob(e.getEntity()) && e.getSpawnReason() == SpawnReason.DEFAULT)
					{ 
						e.getEntity().remove();
					}
				}
			}, 10L);
		}
		if(e.getEntity().getMaxHealth() > 2040)
		{
			//	e.getEntity().setCustomName(ChatColor.translateAlternateColorCodes('&', "&9[" + level + "]&2" + StringUtils.capitalise(e.getEntity().getType().toString().toLowerCase().replace('_', ' '))));
			Heroes.getInstance().getCharacterManager().getMonster(e.getEntity()).setMaxHealth(((Damageable) e.getEntity()).getHealth() * (1+level*0.1));
			e.getEntity().setMaxHealth(((Damageable) e.getEntity()).getHealth() * (1+level*0.1));
			Heroes.getInstance().getCharacterManager().getMonster(e.getEntity()).setExperience((int) e.getEntity().getMaxHealth());
		}
	}

//	@EventHandler
	public void EntityDeath(EntityDeathEvent e)
	{	
		if(e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Creature && MythicMobs.inst().getMobManager().isActiveMob(e.getEntity().getKiller().getUniqueId()))
		{
			ActiveMob killer = MythicMobs.inst().getMobManager().getMythicMobInstance(e.getEntity().getKiller());
			if(killer.getFaction() != null && killer.getFaction().equals("Undead"))
			{
				
				//if(ent.getFaction() == null || !ent.getFaction().equals("Undead"))
				//{
					MythicMobs.inst().getMobManager().spawnMob("UndeadScout", e.getEntity().getLocation());
				//}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void MythicMobSpawn(MythicMobSpawnEvent e)
	{
		int level = PerlinNoise.getLevel(e.getLocation().getChunk());
		//e.getEntity().remove();
		ActiveMob mob = MythicMobs.inst().getAPIHelper().getMythicMobInstance(e.getEntity());
		//ActiveMob mob = e.getMobType().spawn(new AbstractLocation(new BukkitWorld(e.getLocation().getWorld()), e.getLocation().getX(), e.getLocation().getY(), e.getLocation().getZ()), 1);
		e.setMobLevel(level);
		main.getServer().getScheduler().scheduleSyncDelayedTask(main, new BukkitRunnable()
		{
			public void run()
			{
				if(e.getLivingEntity() != null && e.getEntity() instanceof LivingEntity)
				{
					Monster monster = Heroes.getInstance().getCharacterManager().getMonster((LivingEntity) e.getEntity());
					monster.setExperience(0);
				}
				if(e.getLivingEntity() != null && MythicMobs.inst().getCompatibility().getHolograms().isPresent() && DisguiseAPI.getDisguise(e.getEntity()) != null)
				{
					MythicMobs.inst().getCompatibility().getHolograms().get().addNameplate(mob);
				}
			}
		}, 10L);
	}
}
