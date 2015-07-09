package org.nick.utils.customsearch.ali;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.nick.utils.customsearch.ali.dto.SearchResult;
import org.nick.utils.customsearch.ali.dto.Store;
import org.nick.utils.customsearch.ali.dto.StoreMatch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
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
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by VNikolaenko on 08.07.2015.
 */
public class AliSearch {
    private static File getPhantomScript() throws IOException {
        final InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.js");

        final File script = new File(getAppHome(), "test.js");
        IOUtils.copy(resourceAsStream, new FileOutputStream(script));

        return script;
    }

    private static File getPhantom() throws IOException {
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
        final File phantom = getPhantom();

        final File phantomScript = getPhantomScript();

        final List<Callable<Set<SearchResult>>> criterias = new LinkedList<>();
        for (String query : queries) {
            criterias.add(new SearchTask(phantom,phantomScript, new SearchCriteria(query)));
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

        executor.shutdown();

        return matchesMap.entrySet().stream().map(entry -> new StoreMatch(entry.getKey(), entry.getValue())).collect(Collectors.toCollection(LinkedList::new));
    }
}
