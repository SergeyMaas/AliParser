package org.nick.utils.customsearch.ali;

import org.junit.Assert;
import org.junit.Test;
import org.nick.utils.customsearch.ali.dto.Item;
import org.nick.utils.customsearch.ali.dto.SearchResult;
import org.nick.utils.customsearch.ali.dto.Store;
import org.nick.utils.customsearch.ali.dto.StoreMatch;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Владимир on 30.01.2016.
 */
public class ApplicationTest {
    @Test
    public void main() throws Exception {
        List<StoreMatch> search = AliSearch.search("mouse", "charger");

        Assert.assertTrue(!search.isEmpty());

        for (StoreMatch match : search) {
            final Store store = match.getStore();
            System.out.println("---------------------Store--------------------------------");
            System.out.println("----------------------------------------------------------");
            System.out.println("Title: " + store.getTitle());
            System.out.println("Link: " + store.getLink());
            for (Map.Entry<String, Set<SearchResult>> entry : match.getItems().entrySet()) {
                System.out.println("Item: " + entry.getKey() + " " + entry.getValue().size());
            }
            System.out.println("---------------------Items--------------------------------");

            for (Map.Entry<String, Set<SearchResult>> entry : match.getItems().entrySet()) {
                System.out.println("======================= " + entry.getKey() + " ===============================");

                for (SearchResult result : entry.getValue()) {
                    final Item item = result.getItem();
                    System.out.println("Title: " + item.getTitle());
                    System.out.println("Link: " + item.getLink());
                    System.out.println("Orders: " + item.getOrders());
                    System.out.println("----------------------------------------------------");
                }
            }

            System.out.println("");
            System.out.println("");
        }


    }
}