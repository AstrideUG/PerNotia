package me.helight.pernotia.security;

import lombok.Setter;

import java.util.stream.Stream;

public class Hash {

    private String hash;
    private HashSafety safety;

    /**
     * Checks, if the value value is equal to the hash
     */
    public boolean compare(String value) {
        return HashingFactory.compare(value, hash, safety);
    }

    /**
     * Generates a hash
     */
    public static Hash generateHash(String raw, HashSafety safety) {
        Hash hash = new Hash();
        hash.hash = HashingFactory.hash(raw, safety);
        hash.safety = safety;
        return hash;
    }
}
