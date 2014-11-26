import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import tornado.org.cypherspider.AlternateCrawler;
import tornado.org.neo4j.ProductDatabase;

public class FindLinks {
    private static ArrayList<String> links = new ArrayList<>();
    private static ArrayList<String> productlinks = new ArrayList<>();
    private static ArrayList<String> productnr = new ArrayList<>();

    private static String productListingUrlModifier = "&size=500#listingResult";
    private static String url = "http://www.alternate.nl/html/highlights/page.html?hgid=205&tgid=944&tk=7&lk=9276";

	private static int sizeProductNr = 7;

	public final static void main(String[] args) throws IOException {

		org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
		Elements e = doc.getElementById("navTree").getElementsByTag("a");
		// System.out.print(e.toString());

		parselinks(e);

		for (int i = 0; i < links.size(); i++) {
			url = "http://www.alternate.nl" + links.get(i);
			// System.out.println("\n-------   " + url);

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
					e = doc.getElementsByClass("productLink");

					for (int j = 0; j < e.size(); j++) {
						String plink = e.get(j).getElementsByTag("a")
								.attr("href");
						// System.out.println(plink);
						productlinks.add(plink);

					}

				} catch (Exception exp) {
					exp.printStackTrace();
				}

			}
		}

		final ProductDatabase db = new ProductDatabase();
		db.createDB();

		final AlternateCrawler alternateCrawler = new AlternateCrawler();

		for (int i = 0; i < productlinks.size(); i++) {

			try {
				String producturl = productlinks.get(i);
				// System.out.println(producturl);
				int startpoint = producturl.indexOf("/html/product/") + 14;
				String nr = producturl.substring(startpoint, startpoint
						+ sizeProductNr);
				nr = nr.replace("?", "");
				nr = nr.replace("t", "");

				productnr.add(nr);
				// System.out.println(nr);

                alternateCrawler.crawl(nr, db);

			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}

	}

	private static void parselinks(Elements elements) {
		for (int i = 0; i < elements.size(); i++) {

			String link = elements.get(i).attr("href");

			// System.out.println(i + "  " + elements.get(i).attr("href") +
			// "\n");

			if (link.contains("/html/highlights/page")
					|| link.contains("/html/product/")) {

				if (!links.contains(link)) {
					// System.out.println("++++++adding link " + links.size());
					links.add(link);
				}
			}

		}

	}

}
