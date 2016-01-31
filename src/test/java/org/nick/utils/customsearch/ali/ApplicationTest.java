package org.nick.utils.customsearch.ali;

import com.ui4j.api.browser.BrowserEngine;
import com.ui4j.api.browser.BrowserFactory;
import com.ui4j.api.browser.Page;
import org.junit.Test;
import org.nick.utils.customsearch.ali.dto.Item;
import org.nick.utils.customsearch.ali.dto.SearchResult;
import org.nick.utils.customsearch.ali.dto.Store;
import org.nick.utils.customsearch.ali.dto.StoreMatch;

import java.util.Map;
import java.util.Set;

/**
 * Created by Владимир on 30.01.2016.
 */
public class ApplicationTest {
    @Test
    public void main() throws Exception {
        for (StoreMatch match : AliSearch.search("mouse", "charger")) {
            final Store store = match.getStore();
            System.out.println("---------------------Store--------------------------------");
            System.out.println("----------------------------------------------------------");
            System.out.println("Title: " + store.getTitle());
            System.out.println("Link: " + store.getLink());
            System.out.println("---------------------Items--------------------------------");

            for (Map.Entry<String, Set<SearchResult>> entry : match.getItems().entrySet()) {
                System.out.println("Title: " + entry.getKey() + " " + entry.getValue().size());

                for (SearchResult result : entry.getValue()) {
                    final Item item = result.getItem();
                    System.out.println("Title: " + item.getTitle());
                    System.out.println("Link: " + item.getLink());
                    System.out.println("Orders: " + item.getOrders());
                    System.out.println("----------------------------------------------------");
                }

                System.out.println("======================================================");
            }

            System.out.println("");
            System.out.println("");
        }
    }

    @Test
    public void dir() {
        System.setProperty("ui4j.headless", "true");

        // get the instance of the webkit
        BrowserEngine browser = BrowserFactory.getWebKit();

        // navigate to blank page
        Page page = browser.navigate("http://www.aliexpress.com/wholesale?SearchText=mouse");

        // show the browser page
        //page.show();

        String html = (String) page.executeScript("document.documentElement.innerHTML");
        System.out.println(html);
    }
}