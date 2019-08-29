package me.helight.pernotia.database;

import me.helight.ccom.bson.conversions.Bson;
import me.helight.ccom.config.Config;
import me.helight.ccom.config.ConfigBuilder;
import me.helight.ccom.config.defaults.SimpleMongoConfig;
import me.helight.ccom.database.MongoConnector;
import me.helight.ccom.info.ThreadBlocking;
import me.helight.ccom.mongo.client.MongoCollection;
import me.helight.ccom.mongo.client.MongoIterable;
import me.helight.ccom.mongo.client.model.Filters;
import me.helight.ccom.mongo.client.model.Updates;
import me.helight.pernotia.PerNotia;
import me.helight.pernotia.common.SimpleDao;
import me.helight.pernotia.configuration.PerNotiaConfiguration;
import me.helight.pernotia.security.Hash;
import me.helight.pernotia.security.HashSafety;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GeneralPersonDao implements SimpleDao<Person> {

    private MongoCollection<Person> collection;
    private MongoConnector mongoConnector;

    public GeneralPersonDao() {
        PerNotia.POOL.execute(this::init);
    }

    @SuppressWarnings("unchecked")
    public void init() {
        Config<PerNotiaConfiguration> configurationConfig = new ConfigBuilder()
                .setClass(PerNotiaConfiguration.class)
                .setDefault(PerNotiaConfiguration.DEFAULT)
                .setSubfolder("plugins/PerNotia")
                .setFilename("pernotia.json")
                .build();
        PerNotiaConfiguration configuration = configurationConfig.read();

        Config<SimpleMongoConfig> config = new ConfigBuilder<SimpleMongoConfig>()
                .setClass(SimpleMongoConfig.class)
                .setSubfolder("plugins/PerNotia/")
                .setFilename("mongoCredentials.json")
                .setDefault(SimpleMongoConfig.DEFAULT)
                .build();
        SimpleMongoConfig mongoConfig = config.read();
        mongoConnector = MongoConnector.dispense(mongoConfig);

        if (mongoConnector == null) {
            throw new IllegalStateException("MongoConnector could not be instantiated");
        }

        if (mongoConnector.getMongoDatabase() == null) {
            throw new IllegalStateException("MongoDatabase could not be loaded");
        }

        MongoIterable<String> strings = mongoConnector.getMongoDatabase().listCollectionNames();
        if (strings == null) {
            System.out.println("Result returned is null");
            return;
        }
        List<String> collections = strings.into(new ArrayList<>());

        if (!collections.contains(configuration.getMongoCollection())) {
            mongoConnector.getMongoDatabase().createCollection(configuration.getMongoCollection());
        }

        collection = mongoConnector.getMongoDatabase().getCollection(configuration.getMongoCollection(), Person.class);
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
        collection.updateMany(Filters.eq("name", name), Updates.set("name","#"+ Hash.generateHash(ThreadLocalRandom.current().nextLong()+"", HashSafety.WEAK).getHash()));
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

    @ThreadBlocking
    public Person getPersonByName(String name) {
        return collection.find(Filters.eq("name", name)).first();
    }

    @ThreadBlocking
    public Person getPersonByUUID(String uuid) {
        return collection.find(Filters.eq("uuid", uuid)).first();
    }

}
