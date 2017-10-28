package com.mag.rss.factory.app;

import com.mag.rss.factory.RSSWriter;
import com.mag.rss.factory.config.Constants;
import com.mag.rss.factory.object.MagnetItem;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SearchElement {

    // Context
    public static List mlist = new ArrayList<MagnetItem>();
    private static Document TEMPDocument;
    private static String line;
    private static List subscription = new ArrayList<String>();
    private static MagnetItem mitem;

    public static void main(String[] args) throws Exception {
        initFile();
        initNet();
        mining();
    }

    public static void initFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(Constants.SUB_LIST()));
        while ((line = reader.readLine()) != null) {
            subscription.add(line);
        }
        reader.close();
        System.out.println(subscription.size());
    }

    public static void initNet() throws IOException {
        Connection.Response response = Jsoup.connect(URLDecoder.decode(Constants.WEB()))
                .userAgent(Constants.UserAgent())
                .method(Connection.Method.GET)
                .execute();
        System.out.println(response.statusCode());
        System.out.println(response.statusMessage());
        Map<String, String> loginCookies = response.cookies();
    }

    public static void mining() throws Exception {
        ExecutorService service = Executors.newCachedThreadPool();
        // For each link from the sub list
        subscription.forEach(x -> {
            service.submit(() -> {
                try {
                    Connection conn = Jsoup.connect(x.toString())
                            .userAgent(Constants.UserAgent())
                            .method(Connection.Method.GET);
                    TEMPDocument = conn.get();
                    Elements items = TEMPDocument.getElementsByClass(Constants.CSSTableClass());
                    items.forEach(element -> {
                                mitem = new MagnetItem(element);
                                mlist.add(mitem);
                                System.out.println(mitem.toString());
                            }
                    );
                    RSSWriter.RSSOutput(mlist);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
        service.shutdown();
    }
}