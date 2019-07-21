package me.helight.pernotia.database;

import com.google.common.collect.ForwardingObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Person {

    /**
     * UUID of the corresponding Player
     */
    private String uuid;

    /**
     * Name of the corresponding Player
     */
    private String name;

}
