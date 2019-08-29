package me.helight.pernotia;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.sun.net.httpserver.Filter;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.helight.ccom.concurrency.Chain;
import me.helight.ccom.concurrency.chain.EnvAdrr;
import me.helight.ccom.concurrency.chain.objectives.FunctionObjective;
import me.helight.pernotia.api.UserAPI;
import me.helight.pernotia.database.Person;
import me.helight.pernotia.database.GeneralPersonDao;
import me.helight.pernotia.logging.Log;
import me.helight.pernotia.modules.CommonModule;
import me.helight.pernotia.person.MessagePerson;
import me.helight.pernotia.person.data.SpecificPerson;
import me.helight.pernotia.person.message.Disconnect;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public abstract class PerNotia {

    public static final ExecutorService POOL = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    public static final String HEADER = "===============PerNotia===============";
    public static final String FOOTER = "===============PerNotia===============";

    public static Injector injector;

    @Getter
    @NonNull
    private ServiceMode mode;

    @Inject
    private LoginVerify loginVerify;

    @Inject
    private GeneralPersonDao generalPersonDao;

    private Map<UUID, List<Integer>> redisMessageListeners = new ConcurrentHashMap<>();

    public final void hook() {
        injector = Guice.createInjector(new CommonModule(this));
        injector.injectMembers(this);
        injector.injectMembers(generalPersonDao);
        generalPersonDao.init();
        injector.injectMembers(loginVerify);
    }

    public void handleInvalidUuid(Person person) {
        Log log = new Log();
        log.start();
        log.print("Unregistered person tried to log in with a name, that already exists."," The old entry will be archived, because its most likely outdated");
        log.finish();
    }

    public final void handleRegister(Person person) {
        Log log = new Log();
        POOL.execute(() -> {
            Chain chain = Chain.create()
                    .env("person", person)
                    .runnable(log::start)
                    .consume(p -> generalPersonDao.save(((Person) p).clone()), EnvAdrr.from("person"))
                    .consume(p -> generalPersonDao.update((Person) p, "firstLogin", System.currentTimeMillis()), EnvAdrr.from("person"))
                    .consume(p  -> generalPersonDao.update((Person) p, "token", UUID.randomUUID().toString()), EnvAdrr.from("person"))
                    .function(p -> generalPersonDao.get((Person) p, "firstLogin", Long.class).toString(), EnvAdrr.from("person"))
                    .function(p -> generalPersonDao.get((Person) p, "token", String.class), EnvAdrr.from("person"))
                    .consume(a -> {
                        ArrayList list = (ArrayList) a;
                        log.print(
                                "Registering " + ((Person)list.get(2)).toString(),
                                "First Login: " + list.get(0).toString(),
                                "Unique Token: " + list.get(1).toString()
                        );
                    }, EnvAdrr.from(4), EnvAdrr.from(5), EnvAdrr.from("person"))
                    .runnable(() -> log.print("", "Testing API..."))
                    .addObjective(new FunctionObjective(p -> UserAPI.getPerson(((Person)p).getName()), EnvAdrr.from("person")).exportNamed("ApiPerson"))
                    .consume(u -> log.print("ApiPerson: " + u.toString()), EnvAdrr.from("ApiPerson"))
                    .consume(l -> {
                        ArrayList<Person> list = (ArrayList<Person>) l;
                        log.print(
                                "Valid Data: " + (list.get(0).isValid() && list.get(1).isValid()),
                                "Similar Data: " + (list.get(0).getUuid().equals(list.get(1).getUuid()) && list.get(0).getName().equals(list.get(1).getName())),
                                "_id availability: " + (!list.get(0).fromDB() && list.get(1).fromDB())
                        );
                    }, EnvAdrr.from("person"), EnvAdrr.from("ApiPerson"))
                    .runnable(log::finish);
            chain.runAsync();
        });



    }

    public void nameChanged(Person person) {
        Log log = new Log();
        log.start();
        log.print("Namechange for " + person.toString() + "detected");
        log.finish();
    }

    public void successfulLogin(Person person) {

    }

    public final void handleConnect(Person person) {
        loginVerify.verify(person);

        List<Integer> ids = new ArrayList<>();
        redisMessageListeners.put(UUID.fromString(person.getUuid()), ids);

        POOL.execute(() -> {
            MessagePerson messagePerson = new MessagePerson(person);
            messagePerson.addListener(Disconnect.class, new MessageListener<Disconnect>() {
                @Override
                public void onMessage(CharSequence channel, Disconnect msg) {
                    SpecificPerson.get(person).disconnect(msg.getMessage());
                }
            });

            messagePerson.addListener(Disconnect.class, new MessageListener<Disconnect>() {
                @Override
                public void onMessage(CharSequence channel, Disconnect msg) {
                    SpecificPerson.get(person).disconnect(msg.getMessage());
                }
            });
        });
    }

    public void handleDisconnect(Person person) {

    }

    public void handleVerifyError(Person person) {

    }


    public abstract void sendMessage(Person person, String message);

}
