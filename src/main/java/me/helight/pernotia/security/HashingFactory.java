package me.helight.pernotia.security;

import lombok.NonNull;
import me.helight.ccom.info.NoAPI;
import org.bouncycastle.jcajce.provider.digest.MD5;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import javax.annotation.Nullable;
import java.util.Objects;

public class HashingFactory {

    private static final SHA3.DigestSHA3 SHA_DIGEST = new SHA3.Digest512();
    private static final MD5.Digest MD5_DIGEST = new MD5.Digest();

    private static String advancedHash(@NonNull String message) {
        try {
            return Hex.toHexString(SHA_DIGEST.digest(message.getBytes()));
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean verifyAdvancedHash(@Nullable String message, @Nullable String hash) {
        if (message == null || hash == null) {
            return false;
        } else {
            return Objects.equals(advancedHash(message), hash);
        }
    }

    private static String simpleHash(@NonNull String message) {
        try {
            return Hex.toHexString(MD5_DIGEST.digest(message.getBytes()));
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean verifySimpleHash(@Nullable String message, @Nullable String hash) {
        if (message == null || hash == null) {
            return false;
        } else {
            return Objects.equals(simpleHash(message), hash);
        }
    }

    @NoAPI
    public static String hash(@NonNull String value, HashSafety safety) {
        switch (safety) {
            case STRONG:
                return advancedHash(value);
            default:
                return simpleHash(value);
        }
    }

    @NoAPI
    public static boolean compare(@Nullable String value, @Nullable String stored, HashSafety safety) {
        switch (safety) {
            case STRONG:
                return verifyAdvancedHash(value, stored);
            default:
                return verifySimpleHash(value, stored);
        }
    }
}
