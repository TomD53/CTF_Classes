package elf;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.Collection;

public class ProjectileReflect implements Listener {
    private final JavaPlugin plugin;

    public ProjectileReflect(JavaPlugin plugin, BukkitScheduler scheduler) {
        this.plugin = plugin;
        scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                // Check all players and add shield if necessary
                Collection<? extends Player> player_list = Bukkit.getOnlinePlayers();
                for (Player player : player_list) {
                    if (player.isSneaking() && player.isBlocking()) {
                        new PlayerShieldCheckTask(player, plugin);
                    }
                }
            }
        }, 1L, 5L);
    }

    @EventHandler
    public void playerSneakListener(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            new PlayerShieldCheckTask(event.getPlayer(), plugin);
        }
    }

    public static void shieldTick(Player player) {
        // Time to reflect
        // Check for projectile entities in a radius and then invert their velocity
        // and change their shooter.
        World world = player.getWorld();
        Collection<Entity> nearby_entities = world.getNearbyEntities(player.getLocation(), 2, 2, 2);
        for (Entity entity : nearby_entities) {
            if (entity instanceof Projectile && player.getItemInHand().getType() == Material.WOOD_SWORD) {
                if (((Projectile) entity).getShooter() != player) {
                    ((Projectile) entity).setShooter(player);
                    Location reflect_location = entity.getLocation();
                    double magnitude = entity.getVelocity().length();
                    Vector new_entity_velocity = player.getLocation().getDirection().normalize().multiply(magnitude);
                    entity.teleport(player.getLocation().add(0,1,0));
                    entity.setVelocity(new_entity_velocity);
                    ParticleEffect.SMOKE_LARGE.display(reflect_location, new_entity_velocity.multiply(0.1),
                            1,0, null);
                    player.playSound(reflect_location, Sound.LEVEL_UP, (float) 0.4, 1);
                }
            }
        }
    }

    static class PlayerShieldCheckTask extends BukkitRunnable {
        private final Player player;

        public PlayerShieldCheckTask(Player player, JavaPlugin ctf) {
            // This runs when the object is created
            this.player = player;
            runTaskTimer(ctf, 0L, 1L);
        }

        @Override
        public void run() {
            if (player.isSneaking() && player.isBlocking()) {
                shieldTick(player);
            } else {
                cancel();
            }
        }

    }
    }

