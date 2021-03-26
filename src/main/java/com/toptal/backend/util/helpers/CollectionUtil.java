package com.toptal.backend.util.helpers;

import java.io.Serializable;
import java.util.*;

/**
 * Contains a group of collections related methods
 *
 * @author ehab
 */
public class CollectionUtil {

    /**
     * Returns true if a collection is null or empty
     */
    public static boolean isNullOrEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    /**
     * Returns false if a collection is null or empty
     */
    public static boolean isntNullNorEmpty(Collection<?> c) {
        return !isNullOrEmpty(c);
    }

    public static <T, U> List<U> transformList(List<T> objects, TTransformer<T, U> pTransformer) {
        List<U> results = new ArrayList<>(objects.size());
        for (T o : objects) {
            results.add(pTransformer.transform(o));
        }
        return results;
    }

    public interface TTransformer<T, U> extends Serializable {
        U transform(T o);
    }

}
