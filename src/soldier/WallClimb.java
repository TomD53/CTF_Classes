package soldier;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class WallClimb implements Listener {
    @EventHandler
    public void DetectWallCLimb(PlayerInteractEvent event) {
        if (event.getPlayer().getItemInHand().getType() == Material.IRON_SWORD &&
                event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Player player = event.getPlayer();
            event.getPlayer().setVelocity(new Vector(0,1,0));
        }
    }
}
