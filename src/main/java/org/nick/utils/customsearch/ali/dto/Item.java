package org.nick.utils.customsearch.ali.dto;

import org.apache.commons.lang.StringUtils;

import java.util.Objects;

/**
 * Created by VNikolaenko on 29.06.2015.
 */
public class Item extends DTO {
    private String link = "";
    private String title = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private Integer orders = 0;

    public Item() {
    }

    public Item(String title, String link, Integer orders) {
        this.link = link;
        this.orders = orders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(link, item.link) &&
                Objects.equals(title, item.title) &&
                Objects.equals(orders, item.orders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link, title, orders);
    }

    public String getLink() {

        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getOrders() {
        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }

    public boolean isFilled() {
        return !StringUtils.isEmpty(link) && !StringUtils.isEmpty(title) && orders != null && orders != 0;
    }
}
