package me.helight.pernotia.person.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.*;

@AllArgsConstructor
@Getter
public class Message implements Serializable {

    private String value;

}
