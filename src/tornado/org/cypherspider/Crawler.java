package tornado.org.cypherspider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tornado.org.neo4j.Database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {

    final String site = "https://www.alternate.nl";
    final String productLocation = "/html/product/";
    final String CURRENCY_SYMBOL = "€";
    private static final int META_INDEX = 7;

    public String crawl(String input, Database db) {


        String productNumber = input;
        String crawlpage = site + productLocation + productNumber;
        StringBuilder s = new StringBuilder();
        try {
            Document doc = getDoc(crawlpage);
            s.append("Product: ");
            String productName = getProduct(doc);
            s.append(productName);
            s.append(System.getProperty("line.separator"));
            s.append("Prijs: ");
            s.append(CURRENCY_SYMBOL);
            String price = getPrice(doc);
            price =     price.replace("-", "00");
            s.append(price);
            s.append(System.getProperty("line.separator"));
            List<String> productAttributes = getProductAttributes(doc);
            List<String> productValues = getProductValues(doc);
            s.append(combineValues(s, productAttributes, productValues));
            createProductNodes(db, site, productName, price, productNumber, productAttributes, productValues);
        } catch (Exception e) {

        }

        return s.toString();
    }

    private Document getDoc(String site) throws Exception {
        return Jsoup.connect(site).get();
    }

    private static List<String> getProductAttributes(Document doc) {
        Elements firstRow = doc.getElementsByClass("techDataCol1");
        List<String> productAttributes = new ArrayList();
        for (Element e : firstRow) {
            productAttributes.add(e.text());
        }
        return productAttributes;
    }

    private static List<String> getProductValues(Document doc) {
        Elements secondRow = doc.getElementsByClass("techDataCol2");
        List<String> productValues = new ArrayList();
        for (Element e : secondRow) {
            productValues.add(e.text());
        }
        return productValues;
    }

    private static StringBuilder combineValues(StringBuilder s, List<String> productAttributes, List<String> productValues) {
        List<String> combined = new ArrayList();
        if (productAttributes.size() == productValues.size()) {
            for (int i = 0; i < productAttributes.size() && i < productValues.size(); i++) {
                combined.add(productAttributes.get(i) + " " + productValues.get(i));
            }
        }
        for (int i = 0; i < combined.size(); i++) {
            s.append(combined.get(i));
            s.append(System.getProperty("line.separator"));
        }

        return s;
    }

    private static String formatPrice(String p) {
        String price = p.substring(2,p.length()-1).replace("," , ".");
        return price;
    }

    public static String getElementText(String element, Document doc) throws IOException {

        Element e = doc.select("[itemprop=" + element + "]").first();

        return e.text();
    }

    private static String getProduct(Document doc) throws Exception {
        return getElementText("brand", doc) + " " + doc.select("meta").get(META_INDEX).attr("content");
    }

    private static String getPrice(Document doc) throws Exception {
        return formatPrice(getElementText("price", doc));
    }

    private static void createProductNodes(Database db, String site, String productName, String Price, String productNumber, List<String> productAttributes, List<String> productValues) {
        db.createProductNodes(site, productName, Price, productNumber, productAttributes, productValues);
    }
}
