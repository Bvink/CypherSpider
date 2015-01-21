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

public class MycomCrawler {

	ArrayList<String> productSpecs = new ArrayList<>();
	ArrayList<String> productValues = new ArrayList<>();

	Product product = new Product();

	public String crawl(String url, ProductDatabase db) {

		StringBuilder sb = new StringBuilder();

		try {
			Document doc = Jsoup.connect(url).get();

            String productNumber = getProductNumber(doc);

            sb = includeInfo(sb, productNumber);

			product.setSite(CSConstants.MYCOM_URL);
			product.setName(getProductName(doc));
			product.setID(productNumber);
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

	private String getProductNumber(Document doc) {

		Elements es = doc
				.getElementsByClass(CSConstants.DETAILS_DEVIDER);
		es = es.get(0).getElementsByTag(CSConstants.INPUT_ELEMENT);

		return es.get(0).attr(CSConstants.VALUE_ELEMENT);
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
				.append(Combiner.combineLists(product.getAttributes(),
                        product.getValues()));

		return sb;
	}

    private StringBuilder includeInfo(StringBuilder sb, String productNumber) {
        sb.append(CSConstants.WEBSITE_STR)
                .append(CSConstants.MYCOM_URL)
                .append(CSConstants.LINE_SEPERATOR)
                .append(CSConstants.PRODUCT_NUMBER_STR)
                .append(productNumber)
                .append(CSConstants.LINE_SEPERATOR);
        return sb;
    }

	private void getProductAttributes(Document doc) {

		Elements es = doc.getElementById(CSConstants.SPECIFICATION_ELEMENT).getElementsByClass(
                CSConstants.SPEFICIATION_CLASS);
		for (Element e : es) {
			Elements data = e.getElementsByTag(CSConstants.TD);
			productSpecs.add(data.get(data.size() - 2).text());
			productValues.add(data.get(data.size() - 1).text());
		}
	}

	private String getProductName(Document doc) throws Exception {

		Elements es = doc.getElementsByClass(CSConstants.PAGE_HEADER);
		es = es.get(0).getElementsByTag(CSConstants.A);
		String title = es.get(0).text();
		title = title.replace(CSConstants.MYCOM_REPLACEMENT_STRING, CSConstants.EMPTY);

		return title;

	}

	private String getPrice(Document doc) throws Exception {

		Elements es = doc.getElementsByClass(CSConstants.PRODUCT_PRICE_ELEMENT);
		es = es.get(0).getElementsByTag(CSConstants.SPAN);

		return es.get(0).text();
	}
}
