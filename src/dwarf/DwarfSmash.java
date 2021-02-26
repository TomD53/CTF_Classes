package dwarf;

import net.minecraft.server.v1_8_R3.Block;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.Collection;

public class DwarfSmash implements Listener {
    private final JavaPlugin plugin;
    public DwarfSmash(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public class DwarfSmashChecker implements Runnable{
        private final Player player;
        public DwarfSmashChecker(Player player){
            this.player = player;
        }

        @Override
        public void run() {
            if (((Entity) player).isOnGround()){
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, (float) 1, 1);
                player.playSound(player.getLocation(), Sound.WITHER_DEATH, (float) 1, 1);
                ParticleEffect.EXPLOSION_HUGE.display(player.getLocation(), new Vector(0,0,0),
                        (float) 0.1,0, null);
                World world = player.getWorld();
                Collection<Entity> nearby_entities = world.getNearbyEntities(player.getLocation(), 10, 10, 10);
                for (Entity entity : nearby_entities) {
                    if (entity.getLocation().distance(player.getLocation()) <= 10){
                        if (entity instanceof Player){
                            Player hit_player = ((Player) entity);
                            hit_player.playSound(hit_player.getLocation(), Sound.ANVIL_LAND, (float) 1, 1);
                            hit_player.playSound(hit_player.getLocation(), Sound.WITHER_DEATH, (float) 1, 1);
                            ParticleEffect.EXPLOSION_HUGE.display(hit_player.getLocation(), new Vector(0,0,0),
                                    (float) 0.1,0, null);
                            ((LivingEntity) entity).damage(5);
                        }
                        Vector direction = entity.getLocation().toVector()
                                .subtract(player.getLocation().toVector()).normalize().add(new Vector(0,1,0));
                        entity.setVelocity(entity.getVelocity().add(direction));

                    }
                }
            } else {
                ParticleEffect.SMOKE_LARGE.display(player.getLocation(), new Vector(0,-0.1,0),
                        1,0, null);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new DwarfSmashChecker(player), 1L);
            }
        }
    }

    @EventHandler
    public void detectAnvilClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Location player_location = player.getLocation();
        if (event.getPlayer().getItemInHand().getType() == Material.ANVIL){
            player.setVelocity(player.getVelocity().add(new Vector(0,2,0)));
            player.playSound(player_location, Sound.EXPLODE, (float) 1, 1);
            Bukkit.getScheduler().runTaskLater(plugin, new DwarfSmashChecker(player), 5L);
        }
        else if (event.getPlayer().getItemInHand().getType() == Material.FIREWORK){
            player.setVelocity(player.getLocation().getDirection().normalize().add(new Vector(0,1,0)));
        }
    }


    @EventHandler
    public void cancelAnvilPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        if (event.getBlock().getType() == Material.ANVIL){
            event.setCancelled(true);
        }
    }
}
