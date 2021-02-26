package angel;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.color.RegularColor;
import xyz.xenondevs.particle.data.texture.ItemTexture;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public class AngelBioticBow implements Listener {

    private static final int MAX_REGEN_DURATION = 5;
    private static final int MAX_REGEN_AMPLIFIER = 2;
    private static final int MIN_REGEN_DURATION = 3;
    private static final int MIN_REGEN_AMPLIFIER = 0;
    private static final double REGEN_RADIUS = 5;

    private final JavaPlugin plugin;
    private Collection<Integer> biotic_arrow_tasks;

    public AngelBioticBow(JavaPlugin plugin) {
        this.plugin = plugin;

    }

    @EventHandler
    public void detectArrowRelease(EntityShootBowEvent event) {
        Projectile projectile = (Projectile) event.getProjectile();
        ProjectileSource shooter = projectile.getShooter();
        if (!(shooter instanceof Player)) return; // If the shooter isn't a player then don't go any further
        Player player = ((Player) shooter);
        if (!player.getItemInHand().getItemMeta().getDisplayName().equals("Biotic Bow") ||
                !(projectile instanceof Arrow)) return;

        // Checks are now over, biotic bow code below:

        int task_id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                Location projectile_location = projectile.getLocation();
                //ParticleEffect.HEART.display(projectile_location, new Vector(0,0,0),
                //        (float) 0.5,5, null);
                ParticleEffect.HEART.display(projectile_location);
                if (projectile.isDead()) {
                    Bukkit.getScheduler().cancelTask(projectile.getMetadata("task_id").get(0).asInt());
                }
            }
        }, 0L, 1L);

        projectile.setMetadata("arrow_type", new FixedMetadataValue(plugin, "AngelBiotic"));
        projectile.setMetadata("force", new FixedMetadataValue(plugin, event.getForce()));
        projectile.setMetadata("task_id", new FixedMetadataValue(plugin, task_id));
    }

    @EventHandler
    public void detectArrowLand(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        ProjectileSource shooter = projectile.getShooter();
        if (!(shooter instanceof Player)) return; // If the shooter isn't a player then don't go any further

        List<MetadataValue> arrow_type_metadata = projectile.getMetadata("arrow_type");
        if (arrow_type_metadata.isEmpty()) {
            return;
        }
        int task_id = projectile.getMetadata("task_id").get(0).asInt();
        Bukkit.getScheduler().cancelTask(task_id);

        float force = projectile.getMetadata("force").get(0).asFloat();

        for (Player player : projectile.getWorld().getPlayers()) {
            player.playSound(projectile.getLocation(), Sound.GLASS, 1, 1);
        }

        float particle_speed = (float) 0.5 * force;

        if (force == 1) {
            ParticleEffect.ITEM_CRACK.display(projectile.getLocation(), 0, (float) 0.5, 0, particle_speed,
                    (int) ((int) 250 * force), new ItemTexture(new ItemStack(Material.GOLD_BLOCK)), projectile.getWorld().getPlayers());
        } else {
            ParticleEffect.ITEM_CRACK.display(projectile.getLocation(), 0, (float) 0.5, 0, particle_speed,
                    (int) ((int) 250 * force), new ItemTexture(new ItemStack(Material.IRON_BLOCK)), projectile.getWorld().getPlayers());
        }

        projectile.remove();


    }
}
