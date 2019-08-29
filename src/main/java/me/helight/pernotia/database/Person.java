package me.helight.pernotia.database;

import lombok.*;
import me.helight.ccom.bson.codecs.pojo.annotations.BsonId;
import me.helight.ccom.bson.codecs.pojo.annotations.BsonIgnore;
import me.helight.ccom.bson.codecs.pojo.annotations.BsonProperty;
import me.helight.ccom.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Person {

    @BsonId
    private ObjectId id;

    @BsonProperty
    private String uuid;

    @BsonProperty
    private String name;

    @BsonIgnore
    public boolean isValid() {
        return uuid != null && name != null;
    }

    @BsonIgnore
    public boolean fromDB() {
        return isValid() && id != null;
    }

    @Override
    @SneakyThrows
    @BsonIgnore
    public Person clone() {
        Person person = new Person();
        person.setUuid(uuid);
        person.setName(name);
        person.setId(id);
        return person;
    }
}

