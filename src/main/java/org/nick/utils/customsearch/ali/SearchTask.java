package org.nick.utils.customsearch.ali;

import com.ui4j.api.browser.BrowserEngine;
import com.ui4j.api.browser.Page;
import com.ui4j.api.browser.PageConfiguration;
import com.ui4j.api.dom.Element;
import com.ui4j.api.interceptor.Interceptor;
import com.ui4j.api.interceptor.Request;
import com.ui4j.api.interceptor.Response;

import java.net.HttpCookie;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by VNikolaenko on 29.06.2015.
 */
public class SearchTask implements Callable<Set<SearchResult>> {
    private BrowserEngine browser;

    private SearchCriteria criteria;

    final Set<SearchResult> results = new HashSet<>();

    public SearchTask(BrowserEngine browser, SearchCriteria criteria) {
        this.browser = browser;
        this.criteria = criteria;
    }

    @Override
    public Set<SearchResult> call() throws Exception {
        final StringBuilder querySB = new StringBuilder();

        if (criteria.getQuery() != null) {
            if (querySB.length() > 0) {
                querySB.append("+");
            }
            querySB.append(criteria.getQuery().replaceAll(" ", "+"));
        }

        if (criteria.getMinPrice() != -1) {
            querySB.append("&minPrice=").append(criteria.getMinPrice());
        }
        if (criteria.getMaxPrice() != -1) {
            querySB.append("&maxPrice=").append(criteria.getMaxPrice());
        }

        final String url = "http://www.aliexpress.com/wholesale?shipCountry=ru&page=1&groupsort=1&isFreeShip=y&SortType=total_tranpro_desc&SearchText=" + querySB.toString();

        PageConfiguration config = new PageConfiguration(new Interceptor() {

            @Override
            public void beforeLoad(Request request) {
                request.setCookies(Arrays.asList(
                                new HttpCookie("aep_usuc_f", "site=glo&region=RU&b_locale=en_US&c_tp=RUB"))
                );
            }

            @Override
            public void afterLoad(Response response) {

            }
        });

        Page page = browser.navigate(url, config);

        final List<Element> elements = page.getDocument().queryAll("ul");

        // Normally Ui4j can detect page loads,
        // but for the Ajax requests
        // we cant detect if page is ready or not
        // For such case we should wait manually until page is ready to use
        Thread.sleep(10);

        for (Element ul : elements) {
            for (Element listItem : ul.queryAll(".list-item")) {
                final SearchResult searchResult = new SearchResult();

                for (Element a : listItem.queryAll("a")) {
                    final String href = a.getAttribute("href");
                    if (href.toLowerCase().contains("/store") && !href.toLowerCase().contains("/feedback")) {
                        searchResult.getStore().setLink(href);
                        searchResult.getStore().setTitle(a.getText());
                    }
                    if ((href.toLowerCase().contains("searchResult") || href.toLowerCase().contains("item")) && href.toLowerCase().endsWith(".html")) {
                        searchResult.getItem().setLink(href);
                        searchResult.getItem().setTitle(a.getAttribute("title"));
                    }
                    if ((href.toLowerCase().contains("searchResult") || href.toLowerCase().contains("item")) && href.toLowerCase().endsWith("#thf")) {
                        final String em = a.query("em").getText();
                        final String ordersString = em.substring(em.indexOf('(') + 1, em.indexOf(')'));
                        final int ordersCount = Integer.parseInt(ordersString);
                        if (ordersCount > 0) {
                            searchResult.getItem().setOrders(ordersCount);
                        }
                    }

                    if (searchResult.isFilled()) {
                        results.add(searchResult);
                        break;
                    }
                }
            }
        }

        return results;
    }
}
