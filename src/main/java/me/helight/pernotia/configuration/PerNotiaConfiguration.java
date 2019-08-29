package me.helight.pernotia.configuration;

import lombok.Data;

@Data
public class PerNotiaConfiguration {


    public static final PerNotiaConfiguration DEFAULT = new PerNotiaConfiguration("pernotia_", "pernotia");

    private String redisMapPrefix;

    private String mongoCollection;

    public PerNotiaConfiguration(String redisMapPrefix, String mongoCollection) {
        this.redisMapPrefix = redisMapPrefix;
        this.mongoCollection = mongoCollection;
    }

    public PerNotiaConfiguration() { }


}
