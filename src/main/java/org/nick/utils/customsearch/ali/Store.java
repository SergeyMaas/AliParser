package org.nick.utils.customsearch.ali;

import org.apache.commons.lang.StringUtils;

import java.util.Objects;

/**
 * Created by VNikolaenko on 29.06.2015.
 */
public class Store {
    private String link = "";
    private String title = "";

    public Store() {
    }

    public Store(String link, String title) {
        this.link = link;
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return Objects.equals(link, store.link) &&
                Objects.equals(title, store.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link, title);
    }

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

    public boolean isFilled() {
        return !StringUtils.isEmpty(link) && !StringUtils.isEmpty(title);
    }
}
