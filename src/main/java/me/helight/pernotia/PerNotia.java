package me.helight.pernotia;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.sun.net.httpserver.Filter;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.helight.ccom.collections.Pair;
import me.helight.ccom.collections.Quartet;
import me.helight.ccom.collections.Triplet;
import me.helight.ccom.collections.Tuple;
import me.helight.ccom.concurrency.Chain;
import me.helight.ccom.concurrency.Environment;
import me.helight.ccom.concurrency.EventManager;
import me.helight.ccom.concurrency.chain.EnvAdrr;
import me.helight.ccom.concurrency.chain.objectives.FunctionObjective;
import me.helight.ccom.concurrency.event.Listener;
import me.helight.pernotia.api.UserAPI;
import me.helight.pernotia.api.events.PlayerReadyEvent;
import me.helight.pernotia.chains.RegisterChain;
import me.helight.pernotia.database.Person;
import me.helight.pernotia.database.GeneralPersonDao;
import me.helight.pernotia.logging.Log;
import me.helight.pernotia.modules.CommonModule;
import me.helight.pernotia.person.MessagePerson;
import me.helight.pernotia.person.data.SpecificPerson;
import me.helight.pernotia.person.message.Disconnect;
import me.helight.pernotia.person.message.Message;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

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

    @Getter
    private Map<UUID, List<Integer>> redisMessageListeners = new ConcurrentHashMap<>();

    @Getter
    private EventManager<PlayerReadyEvent> readyManager = new EventManager<>();

    private Chain registerChain = new RegisterChain();

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
        Environment env = new Environment();
        env.put("person", person);
        registerChain.run(env);
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
        POOL.execute(() -> {
            LoginVerify.VerificationResult result = loginVerify.verify(person);

            List<Integer> ids = new ArrayList<>();
            redisMessageListeners.put(UUID.fromString(person.getUuid()), ids);

            MessagePerson messagePerson = new MessagePerson(person);
            messagePerson.addListenerTemp(Disconnect.class, (channel, msg) -> SpecificPerson.get(person).disconnect(msg.getMessage()));
            messagePerson.addListenerTemp(Message.class, (channel, msg) -> SpecificPerson.get(person).sendMessage(msg.getValue()));

            readyManager.broadcast(new PlayerReadyEvent(person,result));
        });
    }

    public void handleDisconnect(Person person) {
        MessagePerson messagePerson = new MessagePerson(person);
        for (int id : redisMessageListeners.get(UUID.fromString(person.getUuid()))) {
            messagePerson.removeListener(id);
        }
        redisMessageListeners.remove(UUID.fromString(person.getUuid()));
    }

    public void handleVerifyError(Person person) {

    }


    @Deprecated
    public abstract void sendMessage(Person person, String message);

}
