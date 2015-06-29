package org.nick.utils.customsearch.ali;

import com.ui4j.api.browser.BrowserEngine;
import com.ui4j.api.browser.BrowserFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Application {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        search("mouse", "2.4Ghz");
    }

    public static void search(final String... queries) throws InterruptedException, ExecutionException {
        //System.setProperty("ui4j.headless", "true");
        final BrowserEngine webkit = BrowserFactory.getWebKit();

        final List<Callable<Set<SearchResult>>> criterias = new LinkedList<>();
        for (String query : queries) {
            criterias.add(new SearchTask(webkit, new SearchCriteria(query)));
        }

        int poolSize = queries.length >= 10 ? 10 : queries.length;

        final ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        final Map<Store, List<SearchResult>> matchesMap = new HashMap<>();

        final List<Future<Set<SearchResult>>> futureList = executor.invokeAll(criterias);
        for (Future<Set<SearchResult>> result : futureList) {
            //Compare
            final Map<Store, List<SearchResult>> resultMatches = new HashMap<>();
            result.get().stream().filter(searchResult -> matchesMap.isEmpty() || matchesMap.containsKey(searchResult.getStore())).forEach(searchResult -> {
                if (resultMatches.containsKey(searchResult.getStore())) {
                    resultMatches.get(searchResult.getStore()).add(searchResult);
                } else {
                    final List<SearchResult> match = new LinkedList<>();
                    match.add(searchResult);
                    resultMatches.put(searchResult.getStore(), match);
                }
            });

            //filter
            for (Iterator<Map.Entry<Store, List<SearchResult>>> it = matchesMap.entrySet().iterator(); it.hasNext(); ) {
                final Map.Entry<Store, List<SearchResult>> next = it.next();
                if (!resultMatches.containsKey(next.getKey())) {
                    it.remove();
                }
            }

            //Add new matches
            for (Map.Entry<Store, List<SearchResult>> entry : resultMatches.entrySet()) {
                if (matchesMap.containsKey(entry.getKey())) {
                    matchesMap.get(entry.getKey()).addAll(entry.getValue());
                } else {
                    matchesMap.put(entry.getKey(), entry.getValue());
                }
            }

        }

        for (Map.Entry<Store, List<SearchResult>> entry : matchesMap.entrySet()) {
            final Store store = entry.getKey();
            System.out.println("---------------------Store--------------------------------");
            System.out.println("----------------------------------------------------------");
            System.out.println("Title: " + store.getTitle());
            System.out.println("Link: " + store.getLink());
            System.out.println("---------------------Items--------------------------------");

            for (SearchResult result : entry.getValue()) {
                final Item item = result.getItem();
                System.out.println("Title: " + item.getTitle());
                System.out.println("Link: " + item.getLink());
                System.out.println("Orders: " + item.getOrders());
            }

            System.out.println("");
            System.out.println("");
        }

        executor.shutdown();
        webkit.shutdown();
    }
}
