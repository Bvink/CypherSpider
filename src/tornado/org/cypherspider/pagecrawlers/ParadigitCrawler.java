package tornado.org.cypherspider.pagecrawlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tornado.org.core.api.utils.Combiner;
import tornado.org.cypherspider.constants.CSConstants;
import tornado.org.cypherspider.objects.Product;
import tornado.org.neo4j.ProductDatabase;

import java.util.ArrayList;
import java.util.List;

public class ParadigitCrawler {

    Product product = new Product();

    public String crawl(String url, ProductDatabase db) {

        // url weer splitten wordt anders bewaard in de database als
        // productnumber

        String uriWithProductNr = url.replace(
                CSConstants.PARADIGIT_URL.replace("https", "http"), "");

        StringBuilder sb = new StringBuilder();

        try {
            Document doc = Jsoup.connect(url).get();

            product.setSite(CSConstants.PARADIGIT_URL);
            product.setName(getProduct(doc));
            product.setType(getType(doc));
            //product.setID(getProductNumber(doc));
            product.setPrice(getPrice(doc).replace(CSConstants.DASH,
                    CSConstants.DOUBLE_ZERO));
            getProductAttributes(doc);

            sb = includeInfo(sb, uriWithProductNr);

            product.setSite(CSConstants.PARADIGIT_URL);
            product.setName(getProduct(doc));
            product.setID(uriWithProductNr);
            product.setPrice(getPrice(doc).replace(CSConstants.DASH,
                    CSConstants.DOUBLE_ZERO));
            getProductAttributes(doc);

            db.createAlternateProductNodes(product);
            sb = productPropertiesOutput(sb, product);
        } catch (Exception e) {
            sb.append(CSConstants.RETRIEVE_ERROR);
            e.printStackTrace();
        }

        return sb.toString();
    }

    private String getProductNumber(Document doc) {
        String productNumber;
        Elements es = doc
                .getElementsByClass(CSConstants.ITEMDETAIL_SUMMARY_CLASS);
        productNumber = es.get(es.size() - 1)
                .getElementsByTag(CSConstants.SPAN).get(1).text();

        return productNumber;
    }

    private StringBuilder productPropertiesOutput(StringBuilder sb,
                                                  Product product) {
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
                .append(Combiner.combineLists(product.getAttributes(),
                        product.getValues()));

        return sb;
    }

    private StringBuilder includeInfo(StringBuilder sb, String productNumber) {
        sb.append(CSConstants.WEBSITE_STR).append(CSConstants.PARADIGIT_URL)
                .append(CSConstants.LINE_SEPERATOR)
                .append(CSConstants.PRODUCT_NUMBER_STR).append(productNumber)
                .append(CSConstants.LINE_SEPERATOR);
        return sb;
    }

    private void getProductAttributes(Document doc) {
        List<String> productAttributes = new ArrayList<>();
        List<String> productValues = new ArrayList<>();

        Elements es = doc
                .getElementsByClass(CSConstants.ITEMDETAIL_SPECIFICATION_CLASS);

        for (Element element : es) {

            Elements data = element.getElementsByTag(CSConstants.SPAN);

            productAttributes.add(data.get(0).text()
                    .replace(CSConstants.DASH, CSConstants.SPACE));
            if (data.size() > 1) {
                productValues.add(data.get(1).text()
                        .replace(CSConstants.DASH, CSConstants.SPACE));
            } else {
                productValues.add(CSConstants.EMPTY);
            }

        }

        product.setAttributes(productAttributes);
        product.setValues(productValues);

    }

    private String getProduct(Document doc) throws Exception {

        Elements e = doc
                .getElementsByClass(CSConstants.PRODUCT_TITLE_CONTAINER);
        return e.get(0).getElementsByTag(CSConstants.SPAN).text().replace(CSConstants.DASH, CSConstants.SPACE);

    }

    private String getType(Document doc) throws Exception {

        String selector = CSConstants.PRODUCT_TYPE_SELECTOR_PARADIGIT;
        Elements elements = doc.select(selector);
        return elements.get(0).text();
    }

    private String getPrice(Document doc) throws Exception {

        Element e = doc.getElementById(CSConstants.PRICE_PLACEHOLDER);
        Elements es = e.getElementsByAttributeValue(
                CSConstants.ITEMPROP_ELEMENT, CSConstants.PRICE_ELEMENT);
        return es.get(0).attr(CSConstants.CONTENT_ELEMENT);
    }
}
