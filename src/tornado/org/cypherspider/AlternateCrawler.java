package tornado.org.cypherspider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tornado.org.cypherspider.constants.CSConstants;
import tornado.org.neo4j.ProductDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlternateCrawler {

    public String crawl(String productNumber, ProductDatabase db) {

        String crawlpage = CSConstants.ALTERNATE_URL + CSConstants.ALTERNATE_PRODUCT_LOCATION + productNumber;
        StringBuilder sb = new StringBuilder();
        try {
            Document doc = Jsoup.connect(crawlpage).get();
            sb.append(CSConstants.PRODUCT_OUTPUT_STR);
            String name = getProduct(doc);
            sb.append(name);
            sb.append(CSConstants.LINE_SEPERATOR);
            sb.append(CSConstants.PRIJS_OUTPUT_STR);
            sb.append(CSConstants.EURO);
            String price = getPrice(doc);
            price = price.replace(CSConstants.DASH, CSConstants.DOUBLE_ZERO);
            sb.append(price);
            sb.append(System.getProperty(CSConstants.LINE_SEPERATOR));
            List<String> productAttributes = getProductAttributes(doc);
            List<String> productValues = getProductValues(doc);
            sb.append(combineValues(sb, productAttributes, productValues));

            Product product = new Product();
            product.setSite(CSConstants.ALTERNATE_URL);
            product.setName(name);
            product.setID(productNumber);
            product.setPrice(price);
            product.setAttributes(productAttributes);
            product.setValues(productValues);

            db.createAlternateProductNodes(product);
        } catch (Exception e) {
            System.out.println(CSConstants.RETRIEVE_ERROR);
        }

        return sb.toString();
    }

    private List<String> getProductAttributes(Document doc) {
        Elements firstRow = doc.getElementsByClass(CSConstants.COLUMN_ONE);
        List<String> productAttributes = new ArrayList<>();
        for (Element element : firstRow) {
            productAttributes.add(element.text());
        }
        return productAttributes;
    }

    private List<String> getProductValues(Document doc) {
        Elements secondRow = doc.getElementsByClass(CSConstants.COLUMN_TWO);
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
            sb.append(CSConstants.LINE_SEPERATOR);
        }

        return sb;
    }

    private String formatPrice(String p) {
        return p.substring(2, p.length() - 1).replace(CSConstants.COMMA, CSConstants.PERIOD);
    }

    public String getElementText(String elementName, Document doc) throws IOException {

        Element element = doc.select(CSConstants.ITEM_PROPERTY_OPEN + elementName + CSConstants.ITEM_PROPERTY_CLOSE).first();

        return element.text();
    }

    private String getProduct(Document doc) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(getElementText(CSConstants.BRAND_ELEMENT, doc));
        sb.append(CSConstants.SPACE);
        sb.append(doc.select(CSConstants.META_ELEMENT).get(CSConstants.ALTERNATE_META_INDEX).attr(CSConstants.CONTENT_ELEMENT));
        return sb.toString();
    }

    private String getPrice(Document doc) throws Exception {
        return formatPrice(getElementText(CSConstants.PRICE_ELEMENT, doc));
    }
}
