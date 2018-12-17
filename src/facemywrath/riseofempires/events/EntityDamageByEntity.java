package facemywrath.riseofempires.events;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import facemywrath.riseofempires.empires.Empire;
import facemywrath.riseofempires.empires.EmpireType;
import facemywrath.riseofempires.empires.claims.ClaimManager;
import facemywrath.riseofempires.empires.members.Member;
import facemywrath.riseofempires.empires.members.MemberManager;
import facemywrath.riseofempires.main.Main;
import facemywrath.riseofempires.util.GetRandom;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;

public class EntityDamageByEntity implements Listener {

	private Main main;

	public EntityDamageByEntity(Main main)
	{
		this.main = main;
	}
	
	public void spawnZombie(Location loc)
	{
		String name = "UndeadScout";
		int i = GetRandom.getRandInt(1, 100);
		if(i < 4)
		{
			name = "Darkwarrior";
		}
		else if(i < 10)
		{
			name = "UndeadWarrior";
		}
		else if(i < 20)
		{
			name = "UndeadArcher";
		}
		String mobname = "Undead Scout";
		switch(name)
		{
		case "Darkwarrior":
			mobname = "Dark Warrior";
			break;
		case "UndeadWarrior":
			mobname = "Undead Warrior";
			break;
		case "UndeadArcher":
		 mobname = "Undead Archer";
		 break;
		case "UndeadScout":
			mobname = "Undead Scout";
		}
		for(Player p : loc.getWorld().getPlayers())
		{
			if(p.getLocation().distance(loc) < 4)
			{
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7A(n) " + mobname + " has risen from a corpse."));
			}
		}
		MythicMobs.inst().getMobManager().spawnMob(name, loc);
	}

	@EventHandler
	public void eventMethod(EntityDamageByEntityEvent e)
	{ 
		if(e.getDamager() != null && e.getDamager() instanceof Creature && MythicMobs.inst().getMobManager().isActiveMob(e.getDamager().getUniqueId()))
		{
			if(((Damageable) e.getEntity()).getHealth() - e.getFinalDamage() <= 0)
			{
				ActiveMob killer = MythicMobs.inst().getMobManager().getMythicMobInstance(e.getDamager());
				if(killer.getFaction() != null && killer.getFaction().equals("Undead") && MythicMobs.inst().getMobManager().isActiveMob(e.getEntity().getUniqueId()))
				{
					ActiveMob ent = MythicMobs.inst().getMobManager().getMythicMobInstance(e.getEntity());
					if(ent.getFaction() == null || !ent.getFaction().equals("Undead"))
					{
						main.getServer().getScheduler().scheduleSyncDelayedTask(main, new BukkitRunnable()
						{
							public void run()
							{
								if(e.getEntity().isDead())
								{
									spawnZombie(e.getEntity().getLocation());
								}
							}
						}, 5L);
					}
				}
			}
		}
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player)
		{
			if(MemberManager.getMember((Player) e.getDamager()) != null && MemberManager.getMember((Player) e.getEntity()) != null)
			{
				Member member = MemberManager.getMember((Player) e.getDamager());
				Member member2 = MemberManager.getMember((Player) e.getEntity());
				if(member.getEmpire().equals(member2.getEmpire()))
				{
					e.setCancelled(true);
					return;
				}
			}
			if(ClaimManager.getEmpireAt(e.getEntity().getLocation()) != null)
			{
				Empire emp = ClaimManager.getEmpireAt(e.getEntity().getLocation());
				if(emp.getType() == EmpireType.SANCTUARY)
				{
					e.setCancelled(true);
					e.getDamager().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bSanctuary&9] &4You can't do that in a sanctuary."));
				}
				else if(emp.getType() == EmpireType.EMPIRE && MemberManager.getMember((Player) e.getEntity()) != null && ClaimManager.getEmpireAt(e.getEntity().getLocation()).equals(MemberManager.getMember((Player) e.getEntity()).getEmpire()))
				{
					e.setCancelled(true);
					e.getDamager().sendMessage(ChatColor.translateAlternateColorCodes('&', MemberManager.getMember((Player) e.getEntity()).getEmpire().getTag() + "&4You can't hurt a player in their own territory."));
				}
			}
		}
		if(e.getDamager() instanceof Player && !e.isCancelled() && e.getDamage() > 0)
		{
			if(MythicMobs.inst().getAPIHelper().isMythicMob(e.getEntity()))
			{
				ActiveMob mob = MythicMobs.inst().getAPIHelper().getMythicMobInstance(e.getEntity());
				ActionBarAPI.sendActionBar((Player) e.getDamager(), ChatColor.translateAlternateColorCodes('&', "&4Did " + (int) e.getDamage() + " damage to " + mob.getType().getDisplayName().replace("<mob.level>", mob.getLevel() + "") + " &4Health: " + (int) mob.getEntity().getHealth() + "/" + (int) mob.getEntity().getMaxHealth()));
			}
		}
	}
}
