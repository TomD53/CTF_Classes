import angel.AngelBioticBow;
import angel.AngelGrapplingHook;
import dwarf.DwarfSmash;
import elf.ProjectileReflect;
import elf.Wind;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import soldier.WallClimb;

import java.util.EventListener;

public class CTFClasses extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("CTF Classes Plugin initialised");
        BukkitScheduler scheduler = getServer().getScheduler();
        getServer().getPluginManager().registerEvents(new Wind(), this);
        getServer().getPluginManager().registerEvents(new WallClimb(), this);
        getServer().getPluginManager().registerEvents(new AngelBioticBow(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileReflect(this, scheduler), this);
        getServer().getPluginManager().registerEvents(new AngelGrapplingHook(this), this);
        // getServer().getPluginManager().registerEvents(new DwarfSmash(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling CTF classes");
    }
}
