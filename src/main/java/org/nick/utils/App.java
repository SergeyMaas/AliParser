package org.nick.utils;

import com.ui4j.api.browser.BrowserEngine;
import com.ui4j.api.browser.BrowserFactory;
import com.ui4j.api.browser.Page;
import com.ui4j.api.dom.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {


        final String url = "http://www.aliexpress.com/wholesale?initiative_id=SB_20150617021423&site=glo&shipCountry=ru&SearchText=bluetooth+mouse+-2.4Ghz+-keyboard&page=1";
        /*
        final HtmlCleaner htmlCleaner = new HtmlCleaner();
        final TagNode html = htmlCleaner.clean(new URL(url));
        final TagNode hslist = html.getElementsByAttValue("id", "hs-list-items", true, true)[0];
        final TagNode blist = html.getElementsByAttValue("id", "hs-below-list-items", true, true)[0];

        for (TagNode li : hslist.getElementsByName("li", false)) {
            System.out.println(li);
        }

        for (TagNode li : blist.getElementsByName("li", true)) {
            System.out.println(li);
        }*/

        //WebView view = new WebView();


        // get the instance of the webkit
        BrowserEngine browser = BrowserFactory.getWebKit();

        final Page page = browser.navigate(url);

        final Map<String, String> links = new HashMap<>();
        for (Element ul : page.getDocument().queryAll("ul")) {
            for (Element listItem : ul.queryAll(".list-item")) {
                String link = null;
                String store = null;

                for (Element a : listItem.queryAll("a")) {
                    final String href = a.getAttribute("href");
                    if (href.toLowerCase().contains("/store") && !href.toLowerCase().contains("/feedback")) {
                        store = href;
                    }
                    if (href.toLowerCase().contains("item") && href.toLowerCase().endsWith(".html")) {
                        link = href;
                    }


                    if (link != null && store != null) {
                        links.put(store, link);
                        break;
                    }
                }
            }
        }

        page.close();
        browser.shutdown();
    }
}
