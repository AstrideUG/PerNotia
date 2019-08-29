package me.helight.pernotia.person.data.bungee;

import me.helight.pernotia.database.Person;
import me.helight.pernotia.person.DataAccessPerson;
import me.helight.pernotia.person.data.SpecificPerson;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePerson extends SpecificPerson {

    public BungeePerson(DataAccessPerson accessPerson) {
        super(accessPerson);
    }

    @Override
    public void sendMessage(String message) {
        ProxiedPlayer proxiedPlayer = toPlayer((Person) delegate());
        if (proxiedPlayer != null) {
            proxiedPlayer.sendMessage(TextComponent.fromLegacyText(message));
        }
    }

    @Override
    public void disconnect(String message) {
        ProxiedPlayer proxiedPlayer = toPlayer((Person) delegate());
        if (proxiedPlayer != null) {
            proxiedPlayer.disconnect(TextComponent.fromLegacyText(message));
        }
    }

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
