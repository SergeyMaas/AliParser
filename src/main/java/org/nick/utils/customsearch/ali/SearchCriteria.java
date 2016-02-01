package org.nick.utils.customsearch.ali;

/**
 * Created by VNikolaenko on 26.06.2015.
 */
public class SearchCriteria {
    private String query;
    private int minPrice;
    private int pages4Processing;
    private int maxPrice;

    SearchCriteria(String... queries) {
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

    /*public SearchCriteria(int minPrice, int maxPrice, int pages4Processing, String... queries) {
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
    }*/

    int getMaxPrice() {
        return maxPrice;
    }

    String getQuery() {
        return query;
    }

    int getPages4Processing() {
        return pages4Processing;
    }

    int getMinPrice() {
        return minPrice;
    }
}
