package org.nick.utils.customsearch.ali;

/**
 * Created by VNikolaenko on 26.06.2015.
 */
public class SearchCriteria {
    private String query;
    private int minPrice;

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
    }

    public SearchCriteria(int minPrice, int maxPrice, String... queries) {
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
}
