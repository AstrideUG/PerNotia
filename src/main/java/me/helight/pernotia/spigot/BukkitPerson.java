package me.helight.pernotia.spigot;

import me.helight.pernotia.database.Person;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Base64;
import java.util.UUID;

public class BukkitPerson {

    public static Person fromPlayer(OfflinePlayer offlinePlayer) {
        Person person = new Person();
        person.setName(offlinePlayer.getName());
        person.setUuid(offlinePlayer.getUniqueId().toString());
        return person;
    }

    public static OfflinePlayer toPlayer(Person person) {
        return Bukkit.getOfflinePlayer(UUID.fromString(person.getUuid()));
    }

    @Nullable
    public static Player toOnlinePlayer(Person person) {
        OfflinePlayer offlinePlayer = toPlayer(person);

        if (!offlinePlayer.isOnline()) {
            return null;
        }

        return (Player) offlinePlayer;
    }

}
