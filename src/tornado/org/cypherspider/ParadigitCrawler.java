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

public class ParadigitCrawler {

	Product product = new Product();

	public String crawl(String url, ProductDatabase db) {


        StringBuilder sb = new StringBuilder();

        try {
            Document doc = Jsoup.connect(url).get();

            product.setSite("www.paradigit.nl");
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
				.getElementsByClass("itemdetail-summarytab-productnumbercontainer");

		productNumber = es.get(es.size() - 1).getElementsByTag("span").get(0)
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
				.getElementsByClass("itemdetail-specificationstab-productfeaturecontainer");

		for (int i = 0; i < es.size(); i++) {
			productAttributes.add(es.get(i).getElementsByTag("span").get(0)
					.text());
			productValues.add(es.get(i).getElementsByTag("span").get(1).text());
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

	private String getProduct(Document doc) throws Exception {

		Elements e = doc.getElementsByClass("itemdetail-producttitlecontainer");
		String title = e.get(0).getElementsByTag("span").text();
		return title;

	}

	private String getPrice(Document doc) throws Exception {
		
		Element e = doc.getElementById("ctl00_ContentPlaceHolder1_itemDetail_salespriceIncludingVATPriceLabel_pricePlaceHolder");
		Elements es = e.getElementsByAttributeValue("itemprop", "price");
		String price = es.get(0).attr("content");
				
		return price ;
	}
}
