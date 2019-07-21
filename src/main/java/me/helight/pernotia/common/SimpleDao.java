package me.helight.pernotia.common;

import java.util.List;

public interface SimpleDao<T> {

    List<T> getAll();

    void save(T t);

    boolean exists(T t);

    void update(T t, String field, Object value);

    void delete(T t);

}
