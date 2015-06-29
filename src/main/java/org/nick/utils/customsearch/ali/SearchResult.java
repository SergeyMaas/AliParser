package org.nick.utils.customsearch.ali;

import java.util.Objects;

/**
 * Created by VNikolaenko on 26.06.2015.
 */
public class SearchResult {
    private Store store = new Store();
    private Item item = new Item();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchResult that = (SearchResult) o;
        return Objects.equals(store, that.store) &&
                Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(store, item);
    }

    public Store getStore() {

        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean isFilled() {
        return store != null && store.isFilled() && item != null && item.isFilled();
    }
}
