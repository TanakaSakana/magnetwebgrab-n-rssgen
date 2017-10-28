package com.mag.rss.factory;

import com.mag.rss.factory.config.Constants;
import com.mag.rss.factory.object.MagnetItem;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileOutputStream;
import java.util.List;

import static com.mag.rss.factory.BaseParser.*;

public class RSSWriter {
    public static void RSSOutput(List<MagnetItem> mlist) throws Exception {
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
        createNode(eventWriter, "title", Constants.XMLWriter_Title());
        createNode(eventWriter, "link", Constants.CSSLink());
        createNode(eventWriter, "description", Constants.XMLWriter_Description());
        createNode(eventWriter, "language", Constants.XMLWriter_Language());
        createNode(eventWriter, "pubDate", Constants.XMLWriter_PublicationDate());

        //createNode();
        for (int i = 0; i < mlist.size(); i++) {
            MagnetItem item = mlist.get(i);
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
}
