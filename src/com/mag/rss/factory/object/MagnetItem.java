package com.mag.rss.factory.object;

import com.mag.rss.factory.config.Constants;
import org.jsoup.nodes.Element;

public class MagnetItem {
    static String CSS_Name = Constants.CSSName();
    static String CSS_Time = Constants.CSSTime();
    static String CSS_Link = Constants.CSSLink();
    String name;
    String time;
    String link;

    // Set the path of the element here
    public MagnetItem(Element element) {
        name = element.select(CSS_Name).text();
        time = element.select(CSS_Time).text();
        link = element.select(CSS_Link).attr("href").toString();
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return " name='" + name + '\'' +
                ", time='" + time;
    }
}