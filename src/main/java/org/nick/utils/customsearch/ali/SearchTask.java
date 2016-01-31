package org.nick.utils.customsearch.ali;

import com.ui4j.api.browser.BrowserEngine;
import com.ui4j.api.browser.Page;
import com.ui4j.api.browser.PageConfiguration;
import com.ui4j.api.interceptor.Interceptor;
import com.ui4j.api.interceptor.Request;
import com.ui4j.api.interceptor.Response;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.conditional.ITagNodeCondition;
import org.nick.utils.customsearch.ali.dto.SearchResult;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by VNikolaenko on 29.06.2015.
 */
public class SearchTask implements Callable<SearchTask.QueryResults> {
    private BrowserEngine browser;
    private SearchCriteria criteria;

    public SearchTask(BrowserEngine browser, SearchCriteria criteria) {
        this.criteria = criteria;
        this.browser = browser;
    }

    private Optional<? extends TagNode> findByClass(TagNode node, final String... items) {
        return node.getElementList((ITagNodeCondition) tagNode -> tagNode.getAttributeByName("class") != null && Arrays.asList(tagNode.getAttributeByName("class").split(" ")).containsAll(Arrays.asList(items)), true).stream().findFirst();
    }

    @Override
    public QueryResults call() throws Exception {
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

        for (int i = 1; i < 5; i++) {
            final String url = "http://www.aliexpress.com/wholesale?shipCountry=ru&groupsort=1&isFreeShip=y&SortType=total_tranpro_desc&SearchText=" + querySB.toString() + "&page=" + i;

            String html = getUrlHtmlUI4J(browser, url);

            final TagNode node = new HtmlCleaner().clean(html);

            final List<? extends TagNode> elementList = node.getElementList(tagNode -> tagNode.getName().equals("li")
                    && tagNode.getAttributeByName("class") != null
                    && tagNode.getAttributeByName("class").toLowerCase().contains("list-item"), true);

            for (TagNode li : elementList) {
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
            }
        }

        return new QueryResults(criteria, results);
    }

    private String getUrlHtmlUI4J(BrowserEngine browser, String url) {
        Page page = browser.navigate(url, new PageConfiguration(new Interceptor() {
            @Override
            public void beforeLoad(Request request) {
                request.setCookies(Arrays.asList(new HttpCookie("aep_usuc_f", "site=glo&region=RU&b_locale=en_US&c_tp=RUB")));
            }

            @Override
            public void afterLoad(Response response) {

            }
        }));

        try {
            // navigate to blank page
            return (String) page.executeScript("document.documentElement.innerHTML");
        } finally {
            page.close();
        }
    }

    private String getUrlHtmlPhantom(String url) throws IOException {
        DesiredCapabilities desireCaps = new DesiredCapabilities();
        desireCaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, AliSearch.getPhantom().getAbsolutePath());
        WebDriver driver = new PhantomJSDriver(desireCaps);

        try {
            driver.manage().deleteAllCookies();
            driver.manage().addCookie(new Cookie("aep_usuc_f", "site=glo&region=RU&b_locale=en_US&c_tp=RUB", "www.aliexpress.com", "/", null));

            driver.get(url);

            return driver.getPageSource();
        } finally {
            driver.quit();
        }
    }

    class QueryResults {
        private SearchCriteria criteria;
        private Set<SearchResult> results;

        QueryResults(SearchCriteria criteria, Set<SearchResult> results) {
            this.criteria = criteria;
            this.results = results;
        }

        public SearchCriteria getCriteria() {
            return criteria;
        }

        public Set<SearchResult> getResults() {
            return results;
        }
    }
}
