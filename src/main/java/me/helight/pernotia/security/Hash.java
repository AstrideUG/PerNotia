package me.helight.pernotia.security;

import lombok.Getter;

@Getter
public class Hash {

    private String hash;
    private HashSafety safety;

    public boolean compare(String value) {
        return HashingFactory.compare(value, hash, safety);
    }

    public static Hash generateHash(String raw, HashSafety safety) {
        Hash hash = new Hash();
        hash.hash = HashingFactory.hash(raw, safety);
        hash.safety = safety;
        return hash;
    }
}
