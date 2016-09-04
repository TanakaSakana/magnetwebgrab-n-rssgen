import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.*;
import java.util.Map;

public class SearchElement {
    static Document TEMPDocument;
    static String line;
    static List subscription = new ArrayList<String>();
    static List mlist = new ArrayList<MagnetItem>();
    static MagnetItem mitem;

    public static void main(String[] args) throws Exception {
        initFile();
        initNet();
        mining();
    }
    public static void initFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("src/SubscribeList.txt"));
        while ((line = reader.readLine()) != null) {
            subscription.add(line);
        }
        reader.close();
        System.out.println(subscription.size());
    }
    public static void initNet() throws IOException {
		@todo
		//connection website
		//only need when 403 error
        Connection.Response response = Jsoup.connect(URLDecoder.decode(--website--))
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .method(Connection.Method.GET)
                .execute();
        System.out.println(response.statusCode());
        System.out.println(response.statusMessage());
        Map<String, String> loginCookies = response.cookies();
    }
    public static void mining() throws Exception {
        for (int i = 0; i < subscription.size(); i++) {
            TEMPDocument = Jsoup.connect(subscription.get(i).toString())
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .timeout(5000)
                    .get();
            Elements items = TEMPDocument.getElementsByClass("item");
            items.forEach(element -> {
                        mitem = new MagnetItem(element);
                        mlist.add(mitem);
                        System.out.println(mitem.toString());
                    }
            );
            RSSOutput();
        }
    }
    public static void RSSOutput() throws Exception {
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLEventWriter eventWriter = outputFactory
                .createXMLEventWriter(new FileOutputStream("C:/xampp/htdocs/rss.xml"));
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        StartDocument startDocument = eventFactory.createStartDocument();
        eventWriter.add(startDocument);
        eventWriter.add(end);
        StartElement rssStart = eventFactory.createStartElement("", "", "rss");
        eventWriter.add(rssStart);
        eventWriter.add(eventFactory.createAttribute("version", "2.0"));
        eventWriter.add(eventFactory.createAttribute("xmlns:content", "http://purl.org/rss/1.0/modules/content/"));
        eventWriter.add(eventFactory.createAttribute("xmlns:wfw", "http://wellformedweb.org/CommentAPI/"));
        eventWriter.add(end);
        eventWriter.add(eventFactory.createStartElement("", "", "channel"));
        eventWriter.add(end);
        createNode(eventWriter, "title", "--title--");
        createNode(eventWriter, "link","--website--");
        createNode(eventWriter, "description", "--description--");
        createNode(eventWriter, "language", "zh-cn");
        createNode(eventWriter, "pubDate", new Date().getTime());

        //createNode();
        for (int i = 0; i < mlist.size(); i++) {
            MagnetItem item = (MagnetItem) mlist.get(i);
            eventWriter.add(eventFactory.createStartElement("", "", "item"));
            eventWriter.add(end);
            createNode(eventWriter, "title", item.getName());
            createNode(eventWriter, "pubDate", item.getTime());
            createAttrNode(eventWriter, "enclosure", item.getLink());
            createAttrNode2(eventWriter, "guid", item.getLink());
            eventWriter.add(end);
            eventWriter.add(eventFactory.createEndElement("", "", "item"));
            eventWriter.add(end);
        }
        eventWriter.add(end);
        eventWriter.add(eventFactory.createEndElement("", "", "channel"));
        eventWriter.add(end);
        eventWriter.add(eventFactory.createEndElement("", "", "rss"));
        eventWriter.add(end);
        eventWriter.add(eventFactory.createEndDocument());
        eventWriter.close();
    }
    private static void createNode(XMLEventWriter eventWriter, String name, String value) throws XMLStreamException {
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");
        // create Start node
        StartElement sElement = eventFactory.createStartElement("", "", name);
        eventWriter.add(tab);
        eventWriter.add(sElement);
        // create Content
        Characters characters = eventFactory.createCharacters(value);
        eventWriter.add(characters);
        // create End node
        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(end);
    }
    private static void createAttrNode(XMLEventWriter eventWriter, String name, String value) throws XMLStreamException {
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");

        // create Start node
        XMLEvent sElement;
        sElement = eventFactory.createStartElement("", "", name);
        eventWriter.add(tab);
        eventWriter.add(sElement);
        sElement = eventFactory.createAttribute("url",value);
        eventWriter.add(sElement);
        sElement = eventFactory.createAttribute("length","1");
        eventWriter.add(sElement);
        sElement = eventFactory.createAttribute("type","application/x-bittorrent");
        eventWriter.add(sElement);

        // create End node
        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(end);
    }
    private static void createAttrNode2(XMLEventWriter eventWriter, String name, String value) throws XMLStreamException {
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");

        // create Start node
        XMLEvent sElement;
        sElement = eventFactory.createStartElement("", "", name);
        eventWriter.add(tab);
        eventWriter.add(sElement);
        sElement = eventFactory.createAttribute("isPermaLink","true");
        eventWriter.add(sElement);
        // create Content
        Characters characters = eventFactory.createCharacters(value);
        eventWriter.add(characters);
        // create End node
        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(end);
    }
}
class MagnetItem {
    String name;
    String time;
    String link;

    public MagnetItem(Element element) {
		@todo
		//Add search selector
        name = element.select("@@").text();
        time = element.select("@@").text();
        link = element.select("@@").attr("href").toString();
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