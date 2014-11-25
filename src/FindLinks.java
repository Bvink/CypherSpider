import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import tornado.org.cypherspider.Crawler;
import tornado.org.neo4j.Database;

public class FindLinks {
	static ArrayList<String> links = new ArrayList<>();
	static ArrayList<String> productlinks = new ArrayList<>();
	static ArrayList<String> productnr = new ArrayList<>();

	static String productListingUrlModifier = "&size=500#listingResult";
	private static int sizeProductNr = 7;

	public final static void main(String[] args) throws IOException {
		String url = "http://www.alternate.nl/html/highlights/page.html?hgid=205&tgid=944&tk=7&lk=9276";

		org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
		Elements e = doc.getElementById("navTree").getElementsByTag("a");
		// System.out.print(e.toString());

		parselinks(e);

		for (int i = 0; i < links.size(); i++) {
			url = "http://www.alternate.nl" + links.get(i);
			System.out.println("\n-------   " + url);

			try {
				doc = Jsoup.connect(url).get();
				e = doc.getElementById("navTree").getElementsByTag("a");
				parselinks(e);

			} catch (Exception exp) {
			}

		}

		System.out.print(links.toString());

		for (int i = 0; i < links.size(); i++) {

			String listinglink = links.get(i);

			if (listinglink.contains("/html/product/listing")) {

				listinglink = listinglink + productListingUrlModifier;
				url = "http://www.alternate.nl" + listinglink;

				try {
					doc = Jsoup.connect(url).get();
					e = doc.getElementsByClass("productLink") ; 
							//getElementById("listingResult")
					

					for (int j = 0; j < e.size(); j++) {
						String plink = e.get(j).getElementsByTag("a").attr("href");
						System.out.println(plink);
						productlinks.add(plink);

					}

				} catch (Exception exp) {
					exp.printStackTrace();
				}

			}
		}

		final Database db = new Database();
		db.createDB();

		final Crawler crawler = new Crawler();

		for (int i = 0; i < productlinks.size(); i++) {

			try {
				String producturl = productlinks.get(i);
				System.out.println(producturl);
				int startpoint = producturl.indexOf("/html/product/")+14;
				String nr = producturl.substring(startpoint, startpoint
						+ sizeProductNr);
				nr = nr.replace("?", "");
				nr = nr.replace("t", "");
				
				productnr.add(nr);
				System.out.println(nr);
				
				crawler.crawl(nr, db);
				
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}

	}

	private static void parselinks(Elements elements) {
		for (int i = 0; i < elements.size(); i++) {

			String link = elements.get(i).attr("href");

			System.out.println(i + "  " + elements.get(i).attr("href") + "\n");

			if (link.contains("/html/highlights/page")
					|| link.contains("/html/product/")) {

				if (!links.contains(link)) {
					System.out.println("++++++adding link " + links.size());
					links.add(link);
				}
			}

		}

	}

}
