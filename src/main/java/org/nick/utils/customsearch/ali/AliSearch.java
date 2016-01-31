package org.nick.utils.customsearch.ali;

import com.ui4j.api.browser.BrowserEngine;
import com.ui4j.api.browser.BrowserFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.nick.utils.customsearch.ali.dto.SearchResult;
import org.nick.utils.customsearch.ali.dto.Store;
import org.nick.utils.customsearch.ali.dto.StoreMatch;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by VNikolaenko on 08.07.2015.
 */
public class AliSearch {
    public static File getPhantom() throws IOException {
        //TODO to property
        final File appHome = getAppHome();

        final File phantom = new File(appHome, "phantomjs-2.0.0-windows/bin/phantomjs.exe");
        if (!phantom.exists()) {
            final File distFile = new File(appHome, "phantomjs-2.0.0-windows.zip");

            //Download if not exists
            if (!distFile.exists()) {
                //TODO check platform
                final String distrURL = "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.0.0-windows.zip";
                FileUtils.copyURLToFile(new URL(distrURL), distFile);
            }

            //unzip
            try (ZipFile zipFile = new ZipFile(distFile)) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    File entryDestination = new File(appHome, entry.getName());
                    if (entry.isDirectory()) {
                        entryDestination.mkdir();
                    } else {
                        entryDestination.getParentFile().mkdir();
                        InputStream in = zipFile.getInputStream(entry);
                        OutputStream out = new FileOutputStream(entryDestination);
                        IOUtils.copy(in, out);
                        IOUtils.closeQuietly(in);
                        out.close();
                    }
                }
            }
        }

        return phantom;
    }

    private static File getAppHome() {

        final File homeDir = new File(System.getProperty("user.home"), ".aliparser");

        homeDir.mkdir();

        return homeDir;
    }

    public static List<StoreMatch> search(final String... queries) throws InterruptedException, ExecutionException, IOException {
        System.setProperty("ui4j.headless", "true");

        // get the instance of the webkit
        BrowserEngine browser = BrowserFactory.getWebKit();

        final List<Callable<SearchTask.QueryResults>> criterias = new ArrayList<>(queries.length);
        for (String query : queries) {
            criterias.add(new SearchTask(browser, new SearchCriteria(query)));
        }

        int poolSize = queries.length >= 10 ? 10 : queries.length;

        final ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        final Map<Store, Map<String, Set<SearchResult>>> matchesMap = new HashMap<>();

        for (Future<SearchTask.QueryResults> queryResultsFuture : executor.invokeAll(criterias)) {
            SearchTask.QueryResults queryResults = queryResultsFuture.get();
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
        }

        executor.shutdown();

        browser.shutdown();

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
