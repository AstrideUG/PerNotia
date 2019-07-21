package me.helight.pernotia.bungee;

import me.helight.pernotia.database.Person;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePerson {

    public static Person fromPlayer(ProxiedPlayer player) {
        Person person = new Person();
        person.setName(player.getName());
        person.setUuid(player.getUniqueId().toString());
        return person;
    }

    public static ProxiedPlayer toPlayer(Person person) {
        ProxiedPlayer proxiedPlayer = null;

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.getUniqueId().toString().equals(person.getUuid())) {
                proxiedPlayer = player;
            }
        }

        return proxiedPlayer;
    }

}
