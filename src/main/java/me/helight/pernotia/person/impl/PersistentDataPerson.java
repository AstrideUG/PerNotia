package me.helight.pernotia.person.impl;

import com.google.inject.Inject;
import me.helight.ccom.info.ThreadBlocking;
import me.helight.pernotia.PerNotia;
import me.helight.pernotia.database.Person;
import me.helight.pernotia.database.GeneralPersonDao;
import me.helight.pernotia.person.DataAccessPerson;

import javax.annotation.Nullable;

public class PersistentDataPerson extends DataAccessPerson {

    public PersistentDataPerson(Person person) {
        super(person);
        PerNotia.injector.injectMembers(this);
    }

    @Inject
    private GeneralPersonDao generalPersonDao;

    /**
     * Fetches a value from the persistent datasource
     *
     * @param field The field which shall be fetched
     * @param clazz The expected class of the value
     * @return The value of the field
     */
    @ThreadBlocking
    public <K> K get(String field, Class<K> clazz) {
        return generalPersonDao.get(person, field, clazz);
    }

    /**
     * Sets/Updates a field in the persistent datasource
     *
     * @param field The field which shall be updated
     * @param value The new value
     */
    @ThreadBlocking
    public void set(String field, @Nullable Object value) {
        generalPersonDao.update(person, field, value);
    }
}
