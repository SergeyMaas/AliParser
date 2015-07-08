package org.nick.utils.customsearch.ali;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Application {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        for (StoreMatch match : AliSearch.search("mouse", "charger")) {
            final Store store = match.getStore();
            System.out.println("---------------------Store--------------------------------");
            System.out.println("----------------------------------------------------------");
            System.out.println("Title: " + store.getTitle());
            System.out.println("Link: " + store.getLink());
            System.out.println("---------------------Items--------------------------------");

            for (SearchResult result : match.getItems()) {
                final Item item = result.getItem();
                System.out.println("Title: " + item.getTitle());
                System.out.println("Link: " + item.getLink());
                System.out.println("Orders: " + item.getOrders());
            }

            System.out.println("");
            System.out.println("");
        }
    }
}
