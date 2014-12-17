package tornado.org.cypherspider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tornado.org.cypherspider.constants.CSConstants;
import tornado.org.cypherspider.objects.Product;
import tornado.org.neo4j.ProductDatabase;

import java.util.ArrayList;
import java.util.List;

public class ParadigitCrawler {

	Product product = new Product();

	public String crawl(String url, ProductDatabase db) {


        StringBuilder sb = new StringBuilder();

        try {
            Document doc = Jsoup.connect(url).get();

            product.setSite(CSConstants.PARADIGIT_URL);
            product.setName(getProduct(doc));
            product.setID(getProductNumber(doc));
            product.setPrice(getPrice(doc).replace(CSConstants.DASH, CSConstants.DOUBLE_ZERO));
           getProductAttributes(doc);

            db.createAlternateProductNodes(product);
            sb = productPropertiesOutput(sb, product);
        } catch (Exception e) {
            sb.append(CSConstants.RETRIEVE_ERROR);
        }

        return sb.toString();
    }

	private String getProductNumber(Document doc) {
		String productNumber;
		Elements es = doc
				.getElementsByClass(CSConstants.ITEMDETAIL_SUMMARY_CLASS);

		productNumber = es.get(es.size() - 1).getElementsByTag(CSConstants.SPAN).get(0)
				.text();

		return productNumber;
	}

	private StringBuilder productPropertiesOutput(StringBuilder sb,
			Product product) {
		sb.append(CSConstants.PRODUCT_OUTPUT_STR)
				.append(product.getName())
				.append(CSConstants.LINE_SEPERATOR)
				.append(CSConstants.PRIJS_OUTPUT_STR)
				.append(CSConstants.EURO)
				.append(product.getPrice())
				.append(CSConstants.LINE_SEPERATOR)
				.append(combineValues(product.getAttributes(),
						product.getValues()));

		return sb;
	}

	private void getProductAttributes(Document doc) {
		List<String> productAttributes = new ArrayList<>();
		List<String> productValues = new ArrayList<>();

		Elements es = doc
				.getElementsByClass(CSConstants.ITEMDETAIL_SPECIFICATION_CLASS);

		for (Element element : es) {
			productAttributes.add(element.getElementsByTag(CSConstants.SPAN).get(0)
					.text());
			productValues.add(element.getElementsByTag(CSConstants.SPAN).get(1).text());
		}

		product.setAttributes(productAttributes);
		product.setValues(productValues);

	}

	
	private StringBuilder combineValues(List<String> productAttributes,
			List<String> productValues) {
		List<String> combined = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		if (productAttributes.size() == productValues.size()) {
			for (int i = 0; i < productAttributes.size()
					&& i < productValues.size(); i++) {
				combined.add(productAttributes.get(i) + CSConstants.SPACE
						+ productValues.get(i));
			}
		}
		for (String c : combined) {
			sb.append(c).append(CSConstants.LINE_SEPERATOR);
		}

		return sb;
	}

	private String getProduct(Document doc) throws Exception {

		Elements e = doc.getElementsByClass(CSConstants.PRODUCT_TITLE_CONTAINER);
		return e.get(0).getElementsByTag(CSConstants.SPAN).text();

	}

	private String getPrice(Document doc) throws Exception {
		
		Element e = doc.getElementById(CSConstants.PRICE_PLACEHOLDER);
		Elements es = e.getElementsByAttributeValue(CSConstants.ITEMPROP_ELEMENT, CSConstants.PRICE_ELEMENT);
		return es.get(0).attr(CSConstants.CONTENT_ELEMENT);
	}
}