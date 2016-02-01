package org.nick.utils.customsearch.ali.dto;

/**
 * Created by VNikolaenko on 29.06.2015.
 */
public class Item {
    private String link = "";
    private String title = "";
    private Integer orders = 0;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOrders() {
        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }
}
