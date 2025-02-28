package common;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple key-value store that allows storing, retrieving, and deleting key-value pairs.
 */
public class KeyValueStore {
    private Map<String, String> store;

    public KeyValueStore() {
        store = new HashMap<>();
    }

    public String put(String key, String value) {
        return store.put(key, value);
    }

    public String get(String key) {
        return store.get(key);
    }

    public String delete(String key) {
        return store.remove(key);
    }
}