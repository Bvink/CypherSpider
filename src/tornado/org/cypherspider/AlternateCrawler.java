package tornado.org.cypherspider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tornado.org.cypherspider.constants.CSConstants;
import tornado.org.cypherspider.objects.Product;
import tornado.org.neo4j.ProductDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlternateCrawler {

    public String crawl(String productNumber, ProductDatabase db) {

        String url = createUrl(productNumber);

        StringBuilder sb = new StringBuilder();

        try {
            Document doc = Jsoup.connect(url).get();

            sb = includeInfo(sb, productNumber);

            Product product = new Product();
            product.setSite(CSConstants.ALTERNATE_URL);
            product.setName(getProduct(doc));
            product.setType(getType(doc));
            product.setID(productNumber);
            product.setPrice(getPrice(doc).replace(CSConstants.DASH, CSConstants.DOUBLE_ZERO));
            product.setAttributes(getProductAttributes(doc));
            product.setValues(getProductValues(doc));

            db.createAlternateProductNodes(product);
            sb = productPropertiesOutput(sb, product);
        } catch (Exception e) {
            sb.append(CSConstants.RETRIEVE_ERROR);
        }

        return sb.toString();
    }

    private String createUrl(String productNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append(CSConstants.ALTERNATE_URL)
                .append(CSConstants.ALTERNATE_PRODUCT_LOCATION)
                .append(productNumber);
        return sb.toString();
    }

    private StringBuilder productPropertiesOutput(StringBuilder sb, Product product) {
        sb.append(CSConstants.PRODUCT_OUTPUT_STR)
                .append(product.getName())
                .append(CSConstants.LINE_SEPERATOR)
                .append(CSConstants.PRODUCT_TYPE_OUTPUT_STR)
                .append(product.getType())
                .append(CSConstants.LINE_SEPERATOR)
                .append(CSConstants.PRIJS_OUTPUT_STR)
                .append(CSConstants.EURO)
                .append(product.getPrice())
                .append(CSConstants.LINE_SEPERATOR)
                .append(combineValues(product.getAttributes(), product.getValues()));

        return sb;
    }

    private StringBuilder includeInfo(StringBuilder sb, String productNumber) {
        sb.append(CSConstants.WEBSITE_STR)
                .append(CSConstants.PARADIGIT_URL)
                .append(CSConstants.LINE_SEPERATOR)
                .append(CSConstants.PRODUCT_NUMBER_STR)
                .append(productNumber)
                .append(CSConstants.LINE_SEPERATOR);
        return sb;
    }

    private List<String> getProductAttributes(Document doc) {
        Elements firstRow = doc.getElementsByClass(CSConstants.COLUMN_ONE);
        List<String> productAttributes = new ArrayList<>();
        if (!firstRow.isEmpty()) {
            for (Element element : firstRow) {
                productAttributes.add(element.text());
            }
        } else {
            Elements rows = doc.getElementsByClass(CSConstants.MOTHERBOARD_TABLE_NAME);
            for (Element row : rows.select(CSConstants.TD)) {
                Elements col = row.getElementsByClass(CSConstants.TCOL_ONE);

                if (!col.text().equals(CSConstants.EMPTY)) {
                    productAttributes.add(col.text());
                }
            }
        }

        return productAttributes;
    }

    private List<String> getProductValues(Document doc) {
        Elements secondRow = doc.getElementsByClass(CSConstants.COLUMN_TWO);
        List<String> productValues = new ArrayList<>();
        if (!secondRow.isEmpty()) {
            for (Element element : secondRow) {
                productValues.add(element.text());
            }
        } else {
            Elements rows = doc.getElementsByClass(CSConstants.MOTHERBOARD_TABLE_NAME);
            int i = 0;
            for (Element row : rows.select(CSConstants.TD)) {
                StringBuilder subsb = new StringBuilder();
                Elements colOne = row.getElementsByClass(CSConstants.TCOL_ONE);
                Elements colTwo = row.getElementsByClass(CSConstants.TCOL_TWO);
                Elements colThree = row.getElementsByClass(CSConstants.TCOL_THREE);
                Elements colFour = row.getElementsByClass(CSConstants.TCOL_FOUR);
                if (colOne.text().equals(CSConstants.EMPTY) && i > 0) {
                    subsb.append(productValues.get(i - 1))
                            .append(CSConstants.SPACE)
                            .append(colTwo.text())
                            .append(CSConstants.SPACE)
                            .append(colThree.text())
                            .append(CSConstants.SPACE)
                            .append(colFour.text());
                    productValues.set(i - 1, subsb.toString());

                } else {
                    subsb.append(colTwo.text())
                            .append(CSConstants.SPACE)
                            .append(colThree.text())
                            .append(CSConstants.SPACE)
                            .append(colFour.text());
                    productValues.add(subsb.toString());
                    i++;
                }
            }
        }
        return productValues;
    }

    private StringBuilder combineValues(List<String> productAttributes, List<String> productValues) {
        List<String> combined = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        if (productAttributes.size() == productValues.size()) {
            for (int i = 0; i < productAttributes.size() && i < productValues.size(); i++) {
                combined.add(productAttributes.get(i) + CSConstants.SPACE + productValues.get(i));
            }
        }
        for (String s : combined) {
            sb.append(s);
            sb.append(CSConstants.LINE_SEPERATOR);
        }
        return sb;
    }

    private String formatPrice(String p) {
        return p.substring(2, p.length() - 1).replace(CSConstants.COMMA, CSConstants.PERIOD);
    }

    private String getElementText(String elementName, Document doc) throws IOException {

        Element element = doc.select(CSConstants.ITEM_PROPERTY_OPEN + elementName + CSConstants.ITEM_PROPERTY_CLOSE).first();

        return element.text();
    }

    private String getType(Document doc) throws  Exception {
        String selector = CSConstants.PRODUCT_TYPE_SELECTOR_ALTERNATE;
        Elements elements = doc.select( selector );
        return elements.get(0).text();
    }

    private String getProduct(Document doc) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(getElementText(CSConstants.BRAND_ELEMENT, doc))
                .append(CSConstants.SPACE)
                .append(doc.select(CSConstants.META_ELEMENT).get(CSConstants.ALTERNATE_META_INDEX).attr(CSConstants.CONTENT_ELEMENT));
        return sb.toString();
    }

    private String getPrice(Document doc) throws Exception {
        return formatPrice(getElementText(CSConstants.PRICE_ELEMENT, doc));
    }
}
