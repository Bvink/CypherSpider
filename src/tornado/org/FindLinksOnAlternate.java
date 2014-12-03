package tornado.org;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import tornado.org.cypherspider.AlternateCrawler;
import tornado.org.neo4j.ProductDatabase;

import tornado.org.cypherspider.AlternateCrawler;
import tornado.org.neo4j.ProductDatabase;

/*
 * can ik weer committen
 */
public class FindLinksOnAlternate extends Thread {

	private final CharSequence paternListinLink = "/html/product/listing";
	private final String urlAlternate = "http://www.alternate.nl";
	private final String paternProductLink = "/html/product/";
	private final CharSequence paternNavigationLink = "/html/highlights/page";
	private final String productListingUrlModifier = "&size=500#listingResult";

	private ArrayList<String> links = new ArrayList<>();
	private ArrayList<String> productlinks = new ArrayList<>();
	private ArrayList<String> productnr = new ArrayList<>();

	private static org.jsoup.nodes.Document doc;
	private Elements e;
	private final ProductDatabase productDatabase = new ProductDatabase();

	private final AlternateCrawler alternateCrawler = new AlternateCrawler();

	private static String url = "http://www.alternate.nl/html/highlights/page.html?hgid=205&tgid=944&tk=7&lk=9276";

	private static int sizeProductNr = 7;

	/*
	 * Maakt contact met alternate en begint vervolgens de graph database te
	 * vullen in een aparte thread
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {

		try {
			doc = Jsoup.connect(url).get();
			e = doc.getElementById("navTree").getElementsByTag("a");
			parselinks(e);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (int i = 0; i < links.size(); i++) {
			url = urlAlternate + links.get(i);

			try {
				doc = Jsoup.connect(url).get();
				e = doc.getElementById("navTree").getElementsByTag("a");
				parselinks(e);

			} catch (Exception exp) {
				exp.printStackTrace();
			}

		}

		findProducts();
		insertProducts();

	}

	private void insertProducts() {
		productDatabase.createDB();

		for (int i = 0; i < productlinks.size(); i++) {

			try {
				String producturl = productlinks.get(i);
				int startpoint = producturl.indexOf(paternProductLink) + 14;
				String nr = producturl.substring(startpoint, startpoint
						+ sizeProductNr);
				nr = nr.replace("?", "");
				nr = nr.replace("t", "");

				productnr.add(nr);
				// TODO controlleer wrm hij naa een lange periode een
				// java.systeem.outofmemory error geeft
				alternateCrawler.crawl(nr, productDatabase);

			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}

	}

	private void findProducts() {
		for (int i = 0; i < links.size(); i++) {

			String listinglink = links.get(i);

			if (listinglink.contains(paternListinLink)) {

				listinglink = listinglink + productListingUrlModifier;
				url = "http://www.alternate.nl" + listinglink;

				try {
					doc = Jsoup.connect(url).get();
					e = doc.getElementsByClass("productLink");

					for (int j = 0; j < e.size(); j++) {
						String plink = e.get(j).getElementsByTag("a")
								.attr("href");
						productlinks.add(plink);

					}

				} catch (Exception exp) {
					exp.printStackTrace();
				}

			}
		}

	}

	private void parselinks(Elements elements) {
		for (int i = 0; i < elements.size(); i++) {

			String link = elements.get(i).attr("href");

			if (link.contains(paternNavigationLink)
					|| link.contains(paternProductLink)) {

				if (!links.contains(link)) {
					links.add(link);
				}
			}

		}

	}

}
