package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRefresher {

    public enum Type {
        PRODUCT,
        CONTACT,
        TRANSACTION
    }

    private static Map<DataRefresher.Type, List<DataRefreshable>> refreshables = new HashMap<>();

    public static void addListener(DataRefresher.Type type, DataRefreshable listener) {
        refreshables.computeIfAbsent(type, k -> new ArrayList<>());
        refreshables.get(type).add(listener);
    }

    public static void removeListener(DataRefresher.Type type, DataRefreshable listener) {
        refreshables.computeIfPresent(type, (type1, dataRefreshables) -> {
            dataRefreshables.remove(listener);
            return dataRefreshables;
        });
    }

    public static void fireEvent(DataRefresher.Type type) {
        if (refreshables.get(type) != null) {
            refreshables.get(type).forEach(DataRefreshable::refresh);
        }
    }


}
