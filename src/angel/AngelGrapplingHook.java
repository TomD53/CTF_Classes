package angel;

import net.minecraft.server.v1_8_R3.EntityFishingHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFish;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;

/**
 * @author TomD53
 * @version 2021-08-09
 */
public class AngelGrapplingHook implements Listener {

    private final JavaPlugin plugin;

    public AngelGrapplingHook(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getMetadata("grapple_location").isEmpty()) {
                    continue;
                }
                Location location = (Location) player.getMetadata("grapple_location").get(0).value();
                Vector direction = location.toVector().subtract(player.getLocation().toVector());
                if (direction.length() < 3 || direction.length() > 20) {
                    player.removeMetadata("grapple_location", plugin);
                }
                Vector player_heading = player.getLocation().getDirection().normalize();
                Vector grapple_direction = player_heading.add(direction.multiply(0.02)).normalize().multiply(0.2);
                ParticleEffect.EXPLOSION_LARGE.display(location);
                ParticleEffect.CLOUD.display(player.getLocation(), grapple_direction.clone().multiply(-0.2), 1, 0, null);
                player.setVelocity(player.getVelocity().add(grapple_direction).add(new Vector(0, 0.01, 0)));
            }
        }, 0L, 1L);
    }

    @EventHandler
    public void onGrappleLand(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile.getMetadata("grapple").isEmpty()) return;
        if (!projectile.getMetadata("grapple").get(0).asBoolean()) return;
        ProjectileSource shooter = projectile.getShooter();
        if (!(shooter instanceof Player)) return;
        Player player = (Player) shooter;
        Location projectile_location = projectile.getLocation();
        Bukkit.getScheduler().cancelTask(projectile.getMetadata("task_id").get(0).asInt());
        ParticleEffect.EXPLOSION_LARGE.display(projectile_location);
        player.setMetadata("grapple_location", new FixedMetadataValue(plugin, projectile_location));
        projectile.remove();
        // player.setVelocity(player.getVelocity().add(projectile_location.getDirection()));
    }

    @EventHandler
    public void cancelGrapple(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (player.getMetadata("grapple_location").isEmpty()) return;
        player.removeMetadata("grapple_location", plugin);
    }

    @EventHandler
    public void onGrappleShoot(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        ProjectileSource shooter = projectile.getShooter();
        if (!(shooter instanceof Player)) return; // If the shooter isn't a player then don't go any further
        Player player = ((Player) shooter);
        String displayName = player.getItemInHand().getItemMeta().getDisplayName();
        if (displayName != null) {
            if (!displayName.equals("Grappling Hook") ||
                    !(projectile instanceof CraftFish)) return;
        } else return;
        projectile.setVelocity(projectile.getVelocity().multiply(2));

        int task_id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            ParticleEffect.SPELL.display(projectile.getLocation());
            if (projectile.isDead() || projectile.isOnGround()) {
                Bukkit.getScheduler().cancelTask(projectile.getMetadata("task_id").get(0).asInt());
            }
        }, 2L, 2L);

        projectile.setMetadata("grapple", new FixedMetadataValue(plugin, true));
        projectile.setMetadata("task_id", new FixedMetadataValue(plugin, task_id));
    }
}