package me.helight.pernotia.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import me.helight.ccom.config.Config;
import me.helight.ccom.config.ConfigBuilder;
import me.helight.ccom.config.defaults.SimpleMongoConfig;
import me.helight.ccom.database.MongoConnector;
import me.helight.ccom.info.NoAPI;
import me.helight.ccom.info.ThreadBlocking;
import me.helight.pernotia.common.SimpleDao;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class PersonDao implements SimpleDao<Person> {

    private MongoCollection<Person> collection;
    private MongoConnector mongoConnector;

    public PersonDao() {
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        Config<SimpleMongoConfig> config = new ConfigBuilder<SimpleMongoConfig>()
                .setClass(SimpleMongoConfig.class)
                .setSubfolder("plugins/PetNotia/")
                .setFilename("mongoCredentials.json")
                .setDefault(SimpleMongoConfig.DEFAULT)
                .build();
        SimpleMongoConfig mongoConfig = config.read();
        mongoConnector = MongoConnector.dispense(mongoConfig);
        collection = mongoConnector.getMongoDatabase().getCollection("pernotia", Person.class);
    }

    public void reconnect() {
        mongoConnector.getMongoClient().close();
        init();
    }

    @Override
    @ThreadBlocking
    public List<Person> getAll() {
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public void save(Person person) {
        collection.insertOne(person);
    }

    @Override
    @ThreadBlocking
    public boolean exists(Person person) {
        return collection.countDocuments(toBson(person)) != 0;
    }

    @ThreadBlocking
    public boolean existsName(Person person) {
        return collection.countDocuments(Filters.eq("name", person.getName())) != 0;
    }

    @ThreadBlocking
    public boolean existsUuid(Person person) {
        return collection.countDocuments(Filters.eq("uuid", person.getUuid())) != 0;
    }

    @ThreadBlocking
    public void changeName(String uuid, String name) {
        collection.updateMany(Filters.eq("uuid", uuid), Updates.set("name", name));
    }

    @ThreadBlocking
    public <K> K get(Person person, String key, Class<K> type) {
        if (mongoConnector.docExists(collection, toBson(person), key)) {
            return type.cast(mongoConnector.docGet(collection, toBson(person), key));
        } else {
            return null;
        }
    }

    @Override
    @ThreadBlocking
    public void update(Person person, String field, Object value) {
        if (value == null) {
            mongoConnector.docRem(collection, toBson(person), field);

        } else {
            mongoConnector.docSet(collection, toBson(person), field, value);
        }
    }

    @Override
    @ThreadBlocking
    public void delete(Person person) {
        collection.deleteMany(toBson(person));
    }

    private Bson toBson(Person person) {
        return Filters.and(Filters.eq("uuid",person.getUuid()), Filters.eq("name", person.getName()));
    }

}
