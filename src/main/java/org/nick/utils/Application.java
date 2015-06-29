package org.nick.utils;

import com.ui4j.api.browser.BrowserEngine;
import com.ui4j.api.browser.BrowserFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Hello world!
 */
public class Application {
    static BrowserEngine browser = BrowserFactory.getWebKit();

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        search("mouse", "usb");
    }

    public static void search(final String... queries) throws InterruptedException, ExecutionException {
        System.setProperty("ui4j.headless", "true");
        BrowserEngine webkit = BrowserFactory.getWebKit();

        List<Callable<List<SearchResult>>> criterias = new LinkedList<>();
        for (String query : queries) {
            criterias.add(new SearchTask(webkit, new SearchCriteria(query)));
        }

        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Future<List<SearchResult>>> results = executor.invokeAll(criterias);

        List<SearchResult> matches = new LinkedList<>();

        for (Future<List<SearchResult>> result : results) {
            final List<SearchResult> searchResults = result.get();

            //First element
            if (matches.isEmpty()) {
                matches.addAll(searchResults);
                continue;
            }

            //filter next results
            final List<SearchResult> filtered = new LinkedList<>();
            for (SearchResult match : matches) {
                for (SearchResult searchResult : searchResults) {
                    if (match.getStoreLink().equals(searchResult.getStoreLink())) {
                        filtered.add(match);
                        filtered.add(searchResult);
                    }
                }
            }

            matches = filtered;
        }

        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");

        for (SearchResult searchResult : matches) {
            System.out.println(searchResult.getStoreTItle());
            System.out.println(searchResult.getStoreLink());
            System.out.println(searchResult.getItem() + " " + searchResult.getOrders());
            System.out.println("--------------------------------------------------------");
        }

        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------------------------------------------");

        executor.shutdown();
        webkit.shutdown();
    }

    /*public static List<SearchResult> getStoreMap(final SearchCriteria queryParam) throws InterruptedException {
        final StringBuilder querySB = new StringBuilder();

        if (queryParam.getQuery() != null) {
            if (querySB.length() > 0) {
                querySB.append("+");
            }
            querySB.append(queryParam.getQuery().replaceAll(" ", "+"));
        }

        if (queryParam.getMinPrice() != -1) {
            querySB.append("&minPrice=").append(queryParam.getMinPrice());
        }
        if (queryParam.getMaxPrice() != -1) {
            querySB.append("&maxPrice=").append(queryParam.getMaxPrice());
        }

        final String url = "http://www.aliexpress.com/wholesale?shipCountry=ru&page=1&groupsort=1&isFreeShip=y&SortType=total_tranpro_desc&SearchText=" + querySB.toString();

        Page page = browser.navigate(url);

        final List<SearchResult> items = new LinkedList<>();
        final List<Element> elements = page.getDocument().queryAll("ul");

        // Normally Ui4j can detect page loads,
        // but for the Ajax requests
        // we cant detect if page is ready or not
        // For such case we should wait manually until page is ready to use
        Thread.sleep(10);

        for (Element ul : elements) {
            for (Element listItem : ul.queryAll(".list-item")) {
                final SearchResult item = new SearchResult();

                for (Element a : listItem.queryAll("a")) {
                    final String href = a.getAttribute("href");
                    if (href.toLowerCase().contains("/store") && !href.toLowerCase().contains("/feedback")) {
                        item.setStoreLink(href);
                        item.setStoreTItle(a.getText());
                    }
                    if (href.toLowerCase().contains("item") && href.toLowerCase().endsWith(".html")) {
                        item.setItem(href);
                    }
                    if (href.toLowerCase().contains("item") && href.toLowerCase().endsWith("#thf")) {
                        final String em = a.query("em").getText();
                        final String ordersString = em.substring(em.indexOf('(') + 1, em.indexOf(')'));
                        final int ordersCount = Integer.parseInt(ordersString);
                        if (ordersCount > 0) {
                            item.setOrders(ordersCount);
                        }
                    }

                    if (item.isFilled()) {
                        items.add(item);
                        break;
                    }
                }
            }
        }

        return items;
    }*/
}
