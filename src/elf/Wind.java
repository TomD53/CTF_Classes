package elf;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;

public class Wind implements Listener {
    @EventHandler
    public void elfWind(PlayerInteractEvent event){
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
            if (event.getPlayer().getInventory().getItemInHand().getType().equals(Material.BOW)){
                Player player = event.getPlayer();
                Vector current_velocity = player.getVelocity();
                Vector player_heading = player.getLocation().getDirection();
                Vector wind_direction = player_heading.clone().multiply(-0.8);
                player.setVelocity(wind_direction);
                ParticleEffect.CLOUD.display(player.getLocation(), player_heading.clone().multiply(0.2), 1, 0, null);
            }
        }
    }
}
