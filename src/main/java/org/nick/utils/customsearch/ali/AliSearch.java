package org.nick.utils.customsearch.ali;

import org.nick.utils.customsearch.ali.dto.SearchResult;
import org.nick.utils.customsearch.ali.dto.Store;
import org.nick.utils.customsearch.ali.dto.StoreMatch;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by VNikolaenko on 08.07.2015.
 */
class AliSearch {
    static List<StoreMatch> search(final String... queries) {
        System.setProperty("ui4j.headless", "true");

        final List<SearchCriteria> criterias = new ArrayList<>(queries.length);
        for (String query : queries) {
            criterias.add(new SearchCriteria(query));
        }

        final Map<Store, Map<String, Set<SearchResult>>> matchesMap = new HashMap<>();

        criterias.stream().parallel().map(SearchTask::processCriteria)
                .forEach(queryResults -> {
                    for (SearchResult result : queryResults.getResults()) {
                        if (!matchesMap.containsKey(result.getStore())) {
                            Map<String, Set<SearchResult>> map = new HashMap<>();
                            for (String query : queries) {
                                map.put(query, new HashSet<>());
                            }

                            matchesMap.put(result.getStore(), map);
                        }

                        matchesMap.get(result.getStore()).get(queryResults.getCriteria().getQuery()).add(result);
                    }
                });

        return getStoreMatches(matchesMap);
    }

    private static List<StoreMatch> getStoreMatches(Map<Store, Map<String, Set<SearchResult>>> matchesMap) {
        List<StoreMatch> result = new ArrayList<>();
        for (Map.Entry<Store, Map<String, Set<SearchResult>>> storeEntry : matchesMap.entrySet()) {
            boolean filled = true;

            for (Map.Entry<String, Set<SearchResult>> queryEntry : storeEntry.getValue().entrySet()) {
                if (queryEntry.getValue().isEmpty()) {
                    filled = false;
                    break;
                }
            }

            if (filled) {
                result.add(new StoreMatch(storeEntry.getKey(), storeEntry.getValue()));
            }
        }
        return result;
    }
}
