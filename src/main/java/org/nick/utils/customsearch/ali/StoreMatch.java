package org.nick.utils.customsearch.ali;

import java.util.List;
import java.util.Objects;

/**
 * Created by VNikolaenko on 08.07.2015.
 */
public class StoreMatch {
    private Store store;
    private List<SearchResult> items;

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

    public StoreMatch(Store store, List<SearchResult> items) {
        this.store = store;
        this.items = items;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public List<SearchResult> getItems() {
        return items;
    }

    public void setItems(List<SearchResult> items) {
        this.items = items;
    }
}
