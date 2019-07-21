package me.helight.pernotia.database;

import lombok.Getter;
import lombok.NoArgsConstructor;
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
