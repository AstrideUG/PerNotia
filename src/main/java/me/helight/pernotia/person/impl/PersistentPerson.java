package me.helight.pernotia.person.impl;

import com.google.inject.Inject;
import me.helight.ccom.info.ThreadBlocking;
import me.helight.pernotia.PerNotia;
import me.helight.pernotia.database.Person;
import me.helight.pernotia.database.PersonDao;
import me.helight.pernotia.person.DataAccessPerson;

import javax.annotation.Nullable;

public class PersistentPerson extends DataAccessPerson {

    public PersistentPerson(Person person) {
        super(person);
        PerNotia.injector.injectMembers(this);
    }

    @Inject
    private PersonDao personDao;

    /**
     * Fetches a value from the persistent datasource
     *
     * @param field The field which shall be fetched
     * @param clazz The expected class of the value
     * @return The value of the field
     */
    @ThreadBlocking
    public <K> K get(String field, Class<K> clazz) {
        return personDao.get(person, field, clazz);
    }

    /**
     * Sets/Updates a field in the persistent datasource
     *
     * @param field The field which shall be updated
     * @param value The new value
     */
    @ThreadBlocking
    public void set(String field, @Nullable Object value) {
        personDao.update(person, field, value);
    }
}
