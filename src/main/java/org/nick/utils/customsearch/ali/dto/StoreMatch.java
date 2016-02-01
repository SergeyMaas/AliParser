package org.nick.utils.customsearch.ali.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by VNikolaenko on 08.07.2015.
 */
public class StoreMatch implements Serializable {
    private Store store;
    private Map<String, Set<SearchResult>> items;

    public StoreMatch(Store store, Map<String, Set<SearchResult>> items) {
        this.store = store;
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreMatch that = (StoreMatch) o;
        return Objects.equals(store, that.store);
    }

    @Override
    public int hashCode() {
        return Objects.hash(store, items);
    }

    public Store getStore() {
        return store;
    }

    public Map<String, Set<SearchResult>> getItems() {
        return items;
    }
}
