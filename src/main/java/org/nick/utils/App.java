package org.nick.utils;

import com.ui4j.api.browser.BrowserEngine;
import com.ui4j.api.browser.BrowserFactory;
import com.ui4j.api.browser.Page;
import org.htmlcleaner.HtmlCleaner;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        final HtmlCleaner htmlCleaner = new HtmlCleaner();

        final String url = "http://www.aliexpress.com/wholesale?initiative_id=SB_20150617021423&site=glo&shipCountry=ru&SearchText=bluetooth+mouse+-2.4Ghz+-keyboard&page=1";
        /*final TagNode html = htmlCleaner.clean(new URL(url));
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

        //page.show();

        //page.getDocument().queryAll(".list-item").forEach(e -> System.out.println(e.getText()));
        System.out.println(page.getDocument().queryAll(".list-item").size());

        page.close();
        browser.shutdown();
    }
}
