package org.nick.utils.customsearch.ali;

/**
 * Created by VNikolaenko on 26.06.2015.
 */
public class SearchCriteria {
    private String query;
    private int minPrice;
    private int pages4Processing;

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public SearchCriteria(String... queries) {
        final StringBuilder querySB = new StringBuilder();

        for (String query : queries) {
            if (query != null) {
                if (querySB.length() > 0) {
                    querySB.append("+");
                }
                querySB.append(query.replaceAll(" ", "+"));
            }
        }

        this.query = querySB.toString();
        this.minPrice = -1;
        this.maxPrice = -1;
        this.pages4Processing = 5;
    }

    public SearchCriteria(int minPrice, int maxPrice, int pages4Processing, String... queries) {
        final StringBuilder querySB = new StringBuilder();

        for (String query : queries) {
            if (query != null) {
                if (querySB.length() > 0) {
                    querySB.append("+");
                }
                querySB.append(query.replaceAll(" ", "+"));
            }
        }

        this.query = querySB.toString();
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.pages4Processing = pages4Processing;
    }

    public int getMaxPrice() {

        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    private int maxPrice;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getPages4Processing() {
        return pages4Processing;
    }

    public void setPages4Processing(int pages4Processing) {
        this.pages4Processing = pages4Processing;
    }
}
