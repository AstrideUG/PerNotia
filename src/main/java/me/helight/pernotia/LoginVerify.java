package me.helight.pernotia;

import me.helight.ccom.info.NoAPI;
import me.helight.ccom.info.ThreadBlocking;
import me.helight.pernotia.database.Person;

public class LoginVerify {

    @ThreadBlocking
    @NoAPI
    private static VerificationResult check(Person person) {
        if (PerNotia.personDao.exists(person)) return VerificationResult.PASS;
        boolean existsName = PerNotia.personDao.existsName(person);
        boolean existsUuid = PerNotia.personDao.existsUuid(person);

        if (!existsName && !existsUuid) {
            return VerificationResult.NOT_REGISTERED;
        } else if (!existsName) {
            return VerificationResult.CHANGED_NAME;
        } else {
            return VerificationResult.INVALID_UUID;
        }
    }

    @ThreadBlocking
    public static void verify(Person person) {
        PerNotia.POOL.execute(() -> {
            switch (check(person)) {
                case NOT_REGISTERED:
                    PerNotia.getPerNotia().handleRegister(person);
                    break;
                case INVALID_UUID:
                    PerNotia.getPerNotia().handleInvalidUuid(person);
                    break;
                case CHANGED_NAME:
                    PerNotia.personDao.changeName(person.getUuid(), person.getName());
                    break;
                default:
                    break;
            }
        });
    }

    public enum VerificationResult {
        PASS,
        NOT_REGISTERED,
        CHANGED_NAME,
        INVALID_UUID
    }

}
