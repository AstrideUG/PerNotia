package me.helight.pernotia.person.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.*;

@AllArgsConstructor
@Getter
public class Disconnect implements Serializable {

    private String message;

}
