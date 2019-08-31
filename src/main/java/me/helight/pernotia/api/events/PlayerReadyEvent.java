package me.helight.pernotia.api.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.helight.pernotia.LoginVerify;
import me.helight.pernotia.database.Person;

@Getter
@AllArgsConstructor
public class PlayerReadyEvent {

    private Person person;
    private LoginVerify.VerificationResult verificationResult;

}
