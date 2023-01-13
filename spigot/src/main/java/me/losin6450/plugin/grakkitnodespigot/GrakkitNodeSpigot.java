package me.losin6450.plugin.grakkitnodespigot;

import com.caoccao.javet.exceptions.JavetException;
import me.losin6450.core.Loader;
import me.losin6450.core.ScriptManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.DriverManager;

/**
 * The type Grakkit node spigot.
 */
public class GrakkitNodeSpigot extends JavaPlugin {

    /**
     * The constant commandMap.
     */
    public static CommandMap commandMap;

    @Override
    public void onLoad() {
        DriverManager.getDrivers();
        try {
            ScriptManager.onLoad();
            ScriptManager.patch(new Loader(getClass().getClassLoader()));
            Field internal = this.getServer().getClass().getDeclaredField("commandMap");
            internal.setAccessible(true);
            commandMap = (CommandMap) internal.get(this.getServer());
        } catch (JavetException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        try {
            ScriptManager.setAdapter(new Adapter());
            ScriptManager.open(this.getDataFolder().getPath());
            Bukkit.getScheduler().runTaskTimer(this, ScriptManager::tick, 0L, 1L);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        try {
            ScriptManager.close();
        } catch (JavetException e) {
            e.printStackTrace();
        }
    }
}
