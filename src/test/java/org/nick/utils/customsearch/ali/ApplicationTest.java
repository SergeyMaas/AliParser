package org.nick.utils.customsearch.ali;

import org.junit.Test;
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
        for (StoreMatch match : AliSearch.search("phone", "charger")) {
            final Store store = match.getStore();
            System.out.println("---------------------Store--------------------------------");
            System.out.println("----------------------------------------------------------");
            System.out.println("Title: " + store.getTitle());
            System.out.println("Link: " + store.getLink());
            System.out.println("---------------------Items--------------------------------");

            for (Map.Entry<String, Set<SearchResult>> entry : match.getItems().entrySet()) {
                System.out.println("Title: " + entry.getKey() + " " + entry.getValue().size());
            }

            /*for (Map.Entry<String, Set<SearchResult>> entry : match.getItems().entrySet()) {
                System.out.println("--------------Query: " + entry.getKey() + "--------------------------------");

                for (SearchResult result : entry.getValue()) {
                    final Item item = result.getItem();
                    System.out.println("Title: " + item.getTitle());
                    System.out.println("Link: " + item.getLink());
                    System.out.println("Orders: " + item.getOrders());
                }
            }*/

            System.out.println("");
            System.out.println("");
        }
    }
}