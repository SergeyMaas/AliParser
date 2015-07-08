package org.nick.utils.customsearch.ali;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by VNikolaenko on 29.06.2015.
 */
public class SearchTask implements Callable<Set<SearchResult>> {
    private SearchCriteria criteria;
    private File phantom;
    private File phantomScript;

    final Set<SearchResult> results = new HashSet<>();

    public SearchTask(File phantom,File phantomScript, SearchCriteria criteria) {
        this.phantom = phantom;
        this.phantomScript = phantomScript;
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

        //new HttpCookie("aep_usuc_f", "site=glo&region=RU&b_locale=en_US&c_tp=RUB");

        final File page = File.createTempFile("ali", "page");
        page.deleteOnExit();

        final Process process = Runtime.getRuntime().exec("\"" + phantom.getAbsolutePath() + "\" \""
                + phantomScript.getAbsolutePath() + "\" \""
                + page.getAbsolutePath() + "\" \""
                + url + "\"");

        process.waitFor();

        final TagNode node = new HtmlCleaner().clean(page);

        final List<? extends TagNode> elementList = node.getElementList(tagNode -> tagNode.getName().equals("li")
                && tagNode.getAttributeByName("class") != null
                && tagNode.getAttributeByName("class").toLowerCase().contains("list-item"), true);

        for (TagNode li : elementList) {
            final SearchResult searchResult = new SearchResult();

            for (TagNode a : li.getElementListByName("a", true)) {
                final String href = a.getAttributeByName("href");
                if (href.toLowerCase().contains("/store") && !href.toLowerCase().contains("/feedback")) {
                    searchResult.getStore().setLink(href);
                    searchResult.getStore().setTitle(a.getText().toString());
                }
                if ((href.toLowerCase().contains("searchResult") || href.toLowerCase().contains("item")) && href.toLowerCase().endsWith(".html")) {
                    searchResult.getItem().setLink(href);
                    searchResult.getItem().setTitle(a.getAttributeByName("title"));
                }
                if ((href.toLowerCase().contains("searchResult") || href.toLowerCase().contains("item")) && href.toLowerCase().endsWith("#thf")) {
                    final String em = a.getElementListByName("em", true).get(0).getText().toString();
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

        page.delete();

        return results;
    }
}
