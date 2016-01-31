package org.nick.utils.customsearch.ali.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by VNikolaenko on 29.06.2015.
 */
public class Store implements Serializable {
    private String link = "";
    private String title = "";

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Store)) return false;
        Store store = (Store) o;
        return Objects.equals(getTitle(), store.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle());
    }
}
