package me.helight.pernotia.person.data;

import com.google.common.collect.ForwardingObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.helight.pernotia.PerNotia;
import me.helight.pernotia.database.Person;
import me.helight.pernotia.person.DataAccessPerson;
import me.helight.pernotia.person.data.bungee.BungeePerson;
import me.helight.pernotia.person.data.spigot.BukkitPerson;
import me.helight.pernotia.person.impl.PersistentDataPerson;
import me.helight.pernotia.spigot.PerNotiaSpigotImpl;

/**
 * Extended Version of the {@link DataAccessPerson} which implementations deliver platform-specific functions
 */
public abstract class SpecificPerson extends ForwardingObject {

    @Getter
    private DataAccessPerson accessPerson;

    public SpecificPerson(DataAccessPerson accessPerson) {
        this.accessPerson = accessPerson;
    }

    public abstract void sendMessage(String message);
    public abstract void disconnect(String message);

    @Override
    public Object delegate() {
        return accessPerson.delegate();
    }

    /**
     * Creates a DAO for the given Person using the given {@link DataAccessPerson} Instance
     */
    public static SpecificPerson get(DataAccessPerson accessPerson) {
        switch (PerNotia.injector.getInstance(PerNotia.class).getMode()) {

            case BUNGEE:
                return new BungeePerson(accessPerson);

            case SPIGOT:
                return new BukkitPerson(accessPerson);

            default:
                throw new IllegalArgumentException("Unknown Service Mode");

        }
    }

    /**
     * Creates a persistent DAO for the given Person
     */
    public static SpecificPerson get(Person person) {
        return get(new PersistentDataPerson(person));
    }

}
