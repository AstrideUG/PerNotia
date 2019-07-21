package me.helight.pernotia.spigot;

import me.helight.pernotia.PerNotia;
import me.helight.pernotia.database.Person;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class PerNotiaSpigotImpl extends JavaPlugin implements PerNotia {

    @Override
    public void onEnable() {
        hook();
    }

    @Override
    public void sendMessage(Person person, String message) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(person.getUuid()));
        if (player.isOnline()) {
            ((Player)player).sendMessage(message);
        }
    }
}
