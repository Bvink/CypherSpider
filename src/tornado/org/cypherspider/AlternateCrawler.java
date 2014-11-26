package tornado.org.cypherspider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tornado.org.neo4j.ProductDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlternateCrawler {

    private final String SITE = "https://www.alternate.nl";
    private final String PRODUCT_LOCATION = "/html/product/";
    private final String CURRENCY_SYMBOL = "€";
    private final int META_INDEX = 7;

    public String crawl(String productNumber, ProductDatabase db) {

        String crawlpage = SITE + PRODUCT_LOCATION + productNumber;
        StringBuilder sb = new StringBuilder();
        try {
            Document doc = Jsoup.connect(crawlpage).get();
            sb.append("Product: ");
            String name = getProduct(doc);
            sb.append(name);
            sb.append(System.getProperty("line.separator"));
            sb.append("Prijs: ");
            sb.append(CURRENCY_SYMBOL);
            String price = getPrice(doc);
            price = price.replace("-", "00");
            sb.append(price);
            sb.append(System.getProperty("line.separator"));
            List<String> productAttributes = getProductAttributes(doc);
            List<String> productValues = getProductValues(doc);
            sb.append(combineValues(sb, productAttributes, productValues));

            Product product = new Product();
            product.setSite(SITE);
            product.setName(name);
            product.setID(productNumber);
            product.setPrice(price);
            product.setAttributes(productAttributes);
            product.setValues(productValues);

            db.createProductNodes(product);
        } catch (Exception e) {
            sb.append("The crawler has failed retrieving data");
        }

        return sb.toString();
    }

    private List<String> getProductAttributes(Document doc) {
        Elements firstRow = doc.getElementsByClass("techDataCol1");
        List<String> productAttributes = new ArrayList<>();
        for (Element element : firstRow) {
            productAttributes.add(element.text());
        }
        return productAttributes;
    }

    private List<String> getProductValues(Document doc) {
        Elements secondRow = doc.getElementsByClass("techDataCol2");
        List<String> productValues = new ArrayList<>();
        for (Element element : secondRow) {
            productValues.add(element.text());
        }
        return productValues;
    }

    private StringBuilder combineValues(StringBuilder sb, List<String> productAttributes, List<String> productValues) {
        List<String> combined = new ArrayList<>();
        if (productAttributes.size() == productValues.size()) {
            for (int i = 0; i < productAttributes.size() && i < productValues.size(); i++) {
                combined.add(productAttributes.get(i) + " " + productValues.get(i));
            }
        }
        for (String c : combined) {
            sb.append(c);
            sb.append(System.getProperty("line.separator"));
        }

        return sb;
    }

    private String formatPrice(String p) {
        return p.substring(2, p.length() - 1).replace(",", ".");
    }

    public String getElementText(String elementName, Document doc) throws IOException {

        Element element = doc.select("[itemprop=" + elementName + "]").first();

        return element.text();
    }

    private String getProduct(Document doc) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(getElementText("brand", doc));
        sb.append(" ");
        sb.append(doc.select("meta").get(META_INDEX).attr("content"));
        return sb.toString();
    }

    private String getPrice(Document doc) throws Exception {
        return formatPrice(getElementText("price", doc));
    }
}
