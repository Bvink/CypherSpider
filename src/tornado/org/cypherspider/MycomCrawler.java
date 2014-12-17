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

public class MycomCrawler {

	ArrayList<String> productSpecs = new ArrayList<String>();
	ArrayList<String> productValues = new ArrayList<String>();

	Product product = new Product();

	public String crawl(String productNumber, ProductDatabase db) {

		String url = createUrl(productNumber);

		StringBuilder sb = new StringBuilder();

		try {
			Document doc = Jsoup.connect(url).get();

			product.setSite("www.mycom.nl");
			product.setName(getProductName(doc));
			product.setID(getProductId(doc));
			product.setPrice(getPrice(doc).replace(CSConstants.DASH,
					CSConstants.DOUBLE_ZERO));

			getProductAttributes(doc);

			db.createAlternateProductNodes(product);
			sb = productPropertiesOutput(sb, product);
		} catch (Exception e) {
			sb.append(CSConstants.RETRIEVE_ERROR);
		}

		return sb.toString();
	}

	private String getProductId(Document doc) {

		Elements es = doc
				.getElementsByClass("product_details devider bg_light_gradient content_wrapper");
		es = es.get(0).getElementsByTag("input");
		String productId = es.get(0).attr("value");

		return productId;
	}

	private String createUrl(String productNumber) {
		StringBuilder sb = new StringBuilder();
		sb.append(CSConstants.ALTERNATE_URL)
				.append(CSConstants.ALTERNATE_PRODUCT_LOCATION)
				.append(productNumber);
		return sb.toString();
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

		Elements es = doc.getElementById("specification").getElementsByClass(
				"product_specification fullwidth");
		for (Element e : es) {
			Elements data = e.getElementsByTag("td");
			productSpecs.add(data.get(data.size() - 2).text());
			productValues.add(data.get(data.size() - 1).text());
		}
	}

	private StringBuilder combineValues(List<String> productAttributes,
			List<String> productValues) {
		List<String> combined = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		if (productAttributes.size() == productValues.size()) {
			for (int i = 0; i < productAttributes.size()
					&& i < productValues.size(); i++) {
				combined.add(productAttributes.get(i) + " "
						+ productValues.get(i));
			}
		}
		for (String c : combined) {
			sb.append(c).append(CSConstants.LINE_SEPERATOR);
		}

		return sb;
	}

	public String getElementText(String elementName, Document doc)
			throws IOException {

		Element element = doc.select(
				CSConstants.ITEM_PROPERTY_OPEN + elementName
						+ CSConstants.ITEM_PROPERTY_CLOSE).first();

		return element.text();
	}

	private String getProductName(Document doc) throws Exception {

		Elements es = doc.getElementsByClass("page_header clearfix fullwidth");
		es = es.get(0).getElementsByTag("a");
		String title = es.get(0).text();
		title = title.replace(" - MyCom", "");

		return title;

	}

	private String getPrice(Document doc) throws Exception {

		Elements es = doc.getElementsByClass("product_price");
		es = es.get(0).getElementsByTag("span");
		String price = es.get(0).text();

		return price;
	}
}
