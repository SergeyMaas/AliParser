package org.nick.utils.customsearch.ali;

import com.ui4j.api.browser.BrowserEngine;
import com.ui4j.api.browser.BrowserFactory;
import com.ui4j.api.browser.Page;
import com.ui4j.api.browser.PageConfiguration;
import com.ui4j.api.interceptor.Interceptor;
import com.ui4j.api.interceptor.Request;
import com.ui4j.api.interceptor.Response;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.conditional.ITagNodeCondition;
import org.nick.utils.customsearch.ali.dto.QueryResults;
import org.nick.utils.customsearch.ali.dto.SearchResult;

import java.net.HttpCookie;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by VNikolaenko on 29.06.2015.
 */
class SearchTask {
    private final static BrowserEngine browser = BrowserFactory.getWebKit();

    private static Optional<? extends TagNode> findByClass(TagNode node, final String... items) {
        return node.getElementList((ITagNodeCondition) tagNode -> tagNode.getAttributeByName("class") != null && Arrays.asList(tagNode.getAttributeByName("class").split(" ")).containsAll(Arrays.asList(items)), true).stream().findFirst();
    }

    static QueryResults processCriteria(final SearchCriteria criteria) {
        final Set<SearchResult> results = new HashSet<>();

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

        IntStream.range(1, criteria.getPages4Processing() + 1).boxed().parallel().forEach(i -> {
            final String url = "http://www.aliexpress.com/wholesale?shipCountry=ru&groupsort=1&isFreeShip=y&SortType=total_tranpro_desc&SearchText=" + querySB.toString() + "&page=" + i;

            final TagNode node = new HtmlCleaner().clean(getUrlHtmlUI4J(url));

            final List<? extends TagNode> elementList = node.getElementList(tagNode -> tagNode.getName().equals("li")
                    && tagNode.getAttributeByName("class") != null
                    && tagNode.getAttributeByName("class").toLowerCase().contains("list-item"), true);

            elementList.stream().parallel().forEach(li -> {
                final SearchResult searchResult = new SearchResult();
                Optional<? extends TagNode> storeNode = findByClass(li, "store");
                if (storeNode.isPresent()) {
                    searchResult.getStore().setLink(storeNode.get().getAttributeByName("href"));
                    searchResult.getStore().setTitle(storeNode.get().getText().toString());
                }

                Optional<? extends TagNode> itemNode = findByClass(li, "history-item", "product");
                if (itemNode.isPresent()) {
                    searchResult.getItem().setLink(itemNode.get().getAttributeByName("href"));
                    searchResult.getItem().setTitle(itemNode.get().getAttributeByName("title"));
                }

                Optional<? extends TagNode> ordersNode = findByClass(li, "order-num-a");
                if (ordersNode.isPresent()) {
                    final String em = ordersNode.get().getElementListByName("em", true).get(0).getText().toString();
                    final String ordersString = em.substring(em.indexOf('(') + 1, em.indexOf(')'));
                    final int ordersCount = Integer.parseInt(ordersString);
                    if (ordersCount > 0) {
                        searchResult.getItem().setOrders(ordersCount);
                    }
                }

                if (!searchResult.getStore().getTitle().isEmpty())
                    results.add(searchResult);
            });
        });

        return new QueryResults(criteria, results);
    }

    private static String getUrlHtmlUI4J(String url) {
        try (Page page = browser.navigate(url, new PageConfiguration(new Interceptor() {
            @Override
            public void beforeLoad(Request request) {
                request.setCookies(Collections.singletonList(new HttpCookie("aep_usuc_f", "site=glo&region=RU&b_locale=en_US&c_tp=RUB")));
            }

            @Override
            public void afterLoad(Response response) {
            }
        }))) {
            // navigate to blank page
            return (String) page.executeScript("document.documentElement.innerHTML");
        }
    }
}
