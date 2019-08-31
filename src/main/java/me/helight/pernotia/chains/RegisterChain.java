package me.helight.pernotia.chains;

import me.helight.ccom.collections.Pair;
import me.helight.ccom.collections.Quartet;
import me.helight.ccom.collections.Triplet;
import me.helight.ccom.collections.Tuple;
import me.helight.ccom.concurrency.Chain;
import me.helight.ccom.concurrency.chain.EnvAdrr;
import me.helight.ccom.concurrency.chain.objectives.FunctionObjective;
import me.helight.pernotia.PerNotia;
import me.helight.pernotia.api.UserAPI;
import me.helight.pernotia.database.GeneralPersonDao;
import me.helight.pernotia.database.Person;
import me.helight.pernotia.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class RegisterChain extends Chain {

    public RegisterChain() {
        supply(Log::new).export(EnvAdrr.from("log"));
        supply(() -> PerNotia.injector.getInstance(GeneralPersonDao.class)).export(EnvAdrr.from("dao"));
        consume(Log::start, Log.class).adresses(EnvAdrr.from("log"));

        consume(t -> {
            Pair<GeneralPersonDao, Person> pair = Tuple.pair((List) t, GeneralPersonDao.class, Person.class);
            pair.getA().save(pair.getB().clone());
        }, EnvAdrr.from("dao"), EnvAdrr.from("person"));

        consume(t -> {
            Pair<GeneralPersonDao, Person> pair = Tuple.pair((List) t, GeneralPersonDao.class, Person.class);
            pair.getA().update(pair.getB(), "firstLogin", System.currentTimeMillis());
        }, EnvAdrr.from("dao"), EnvAdrr.from("person"));

        consume(t -> {
            Pair<GeneralPersonDao, Person> pair = Tuple.pair((List) t, GeneralPersonDao.class, Person.class);
            pair.getA().update(pair.getB(), "token", UUID.randomUUID().toString());
        }, EnvAdrr.from("dao"), EnvAdrr.from("person"));

        function(t -> {
            Pair<GeneralPersonDao, Person> pair = Tuple.pair((List) t, GeneralPersonDao.class, Person.class);
            return pair.getA().get(pair.getB(), "firstLogin", Long.class).toString();
        }, EnvAdrr.from("dao"), EnvAdrr.from("person")).export(EnvAdrr.from("firstLogin"));

        function(t -> {
            Pair<GeneralPersonDao, Person> pair = Tuple.pair((List) t, GeneralPersonDao.class, Person.class);
            return pair.getA().get(pair.getB(), "token", String.class);
        }, EnvAdrr.from("dao"), EnvAdrr.from("person")).export(EnvAdrr.from("token"));
        consume(a -> {
            Quartet<String,String,Person,Log> quartet = Tuple.quartet((ArrayList) a, String.class, String.class, Person.class, Log.class);
            quartet.getD().print(
                    "Registering " + quartet.getC().toString(),
                    "First Login: " + quartet.getA(),
                    "Unique Token: " + quartet.getB()
            );
        }, EnvAdrr.from("firstLogin"), EnvAdrr.from("token"), EnvAdrr.from("person"), EnvAdrr.from("log"));

        consume(log -> log.print("", "Testing API..."), Log.class).adresses(EnvAdrr.from("log"));

        addObjective(new FunctionObjective(p -> UserAPI.getPerson(((Person)p).getName()), EnvAdrr.from("person")).exportNamed("ApiPerson"));

        consume(u -> log.print("ApiPerson: " + u.toString()), EnvAdrr.from("ApiPerson"));

        consume(l -> {
            Triplet<Person,Person,Log> pair = Tuple.triplet((ArrayList<Person>) l, Person.class, Person.class, Log.class);
            pair.getC().print(
                    "Valid Data: " + (pair.getA().isValid() && pair.getB().isValid()),
                    "Similar Data: " + (pair.getA().getUuid().equals(pair.getB().getUuid()) && pair.getA().getName().equals(pair.getB().getName())),
                    "_id availability: " + (!pair.getA().fromDB() && pair.getB().fromDB())
            );
        }, EnvAdrr.from("person"), EnvAdrr.from("ApiPerson"), EnvAdrr.from("log"));

        consume(Log::finish, Log.class).adresses(EnvAdrr.from("log"));
    }
}
