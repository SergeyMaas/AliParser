package org.nick.utils.customsearch.ali;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.conditional.ITagNodeCondition;
import org.nick.utils.customsearch.ali.dto.SearchResult;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by VNikolaenko on 29.06.2015.
 */
public class SearchTask implements Callable<SearchTask.QueryResults> {
    private Set<SearchResult> results = new HashSet<>();
    private SearchCriteria criteria;

    public SearchTask(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    private Optional<? extends TagNode> findByClass(TagNode node, final String... items) {
        return node.getElementList((ITagNodeCondition) tagNode -> tagNode.getAttributeByName("class") != null && Arrays.asList(tagNode.getAttributeByName("class").split(" ")).containsAll(Arrays.asList(items)), true).stream().findFirst();
    }

    @Override
    public QueryResults call() throws Exception {
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

        DesiredCapabilities desireCaps = new DesiredCapabilities();
        desireCaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, AliSearch.getPhantom().getAbsolutePath());
        WebDriver driver = new PhantomJSDriver(desireCaps);

        driver.manage().deleteAllCookies();
        driver.manage().addCookie(new Cookie("aep_usuc_f", "site=glo&region=RU&b_locale=en_US&c_tp=RUB", "www.aliexpress.com", "/", null));

        driver.get(url);

        final TagNode node = new HtmlCleaner().clean(driver.getPageSource());

        driver.quit();

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

            results.add(searchResult);
        }

        return new QueryResults(criteria, results);
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
