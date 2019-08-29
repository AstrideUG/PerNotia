package me.helight.pernotia.person;

import com.google.common.collect.ForwardingObject;
import lombok.AllArgsConstructor;
import me.helight.ccom.encode.Encodings;
import me.helight.ccom.info.ThreadBlocking;
import me.helight.pernotia.database.Person;
import me.helight.pernotia.security.Hash;

import javax.annotation.Nullable;

@AllArgsConstructor
public abstract class DataAccessPerson extends ForwardingObject {

    protected Person person;

    public abstract <K> K get(String field, Class<K> clazz);

    public abstract void set(String field, @Nullable Object value);

    /**
     * Stores a hash to the specific field
     *
     * In this progress, the hash will be serialized and stored as a HexString
     */
    @ThreadBlocking
    public final void storeHash(String field, Hash hash) {
        set(field, Encodings.HEX.encodePojo(hash));
    }

    /**
     * Retrieves a hash from the specific field
     */
    @ThreadBlocking
    public final Hash getHash(String field) {
        String encoded = get(field, Object.class).toString();
        return Encodings.HEX.decodePojo(encoded, Hash.class);
    }

    public void storePojo(String field, Object value) {
        set(field, Encodings.BASE64.encodePojo(value));
    }

    public <K> K getPojo(String field, Class<K>clazz) {
        return Encodings.BASE64.decodePojo(get(field, String.class), clazz);
    }

    @Override
    public Object delegate() {
        return person;
    }
}
