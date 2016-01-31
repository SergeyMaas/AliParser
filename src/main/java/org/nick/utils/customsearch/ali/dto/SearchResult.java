package org.nick.utils.customsearch.ali.dto;

/**
 * Created by VNikolaenko on 26.06.2015.
 */
public class SearchResult {
    private Store store = new Store();
    private Item item = new Item();

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
}
