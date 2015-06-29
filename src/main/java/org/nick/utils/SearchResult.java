package org.nick.utils;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Created by VNikolaenko on 26.06.2015.
 */
public class SearchResult {
    private String storeLink;
    private String storeTItle;
    private String item;
    private Integer orders;

    public String getStoreLink() {
        return storeLink;
    }

    public void setStoreLink(String storeLink) {
        this.storeLink = storeLink;
    }

    public String getStoreTItle() {
        return storeTItle;
    }

    public void setStoreTItle(String storeTItle) {
        this.storeTItle = storeTItle;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getOrders() {

        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }

    public boolean isFilled() {
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (field.get(this) == null) {
                    return false;
                }
            }
        } catch (IllegalAccessException e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchResult that = (SearchResult) o;
        return Objects.equals(storeLink, that.storeLink) &&
                Objects.equals(storeTItle, that.storeTItle) &&
                Objects.equals(item, that.item) &&
                Objects.equals(orders, that.orders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeLink, storeTItle, item, orders);
    }
}
