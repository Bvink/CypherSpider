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

	private static final CharSequence paternListinLink = "/html/product/listing";
	private static final String urlAlternate = "http://www.alternate.nl";
	private static final String paternProductLink = "/html/product/";
	private static final CharSequence paternNavigationLink = "/html/highlights/page";
	private static final String productListingUrlModifier = "&size=500#listingResult";

	private static ArrayList<String> links = new ArrayList<>();
	private static ArrayList<String> productlinks = new ArrayList<>();
	private static ArrayList<String> productnr = new ArrayList<>();

	private static org.jsoup.nodes.Document doc;
	private static Elements e;
	private static final ProductDatabase productDatabase = new ProductDatabase();

	private static final AlternateCrawler alternateCrawler = new AlternateCrawler();
	private static final String urlBase = "http://www.alternate.nl";
	private static final int offsetPaternProductLink = 14;

	private static String url = "http://www.alternate.nl/html/highlights/page.html?hgid=205&tgid=944&tk=7&lk=9276";

	private static final int sizeProductNr = 7;
	private static final String productLinkXmlClassTag = "productLink";
	private static final String navXmlIdTag = "navTree";
	private static final String hyperlinkXmltag = "a";
	private static final String hyperlinkAttributetag = "href";
	

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
			e = doc.getElementById(navXmlIdTag).getElementsByTag(hyperlinkXmltag);
			parselinks(e);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (int i = 0; i < links.size(); i++) {
			url = urlAlternate + links.get(i);

			try {
				doc = Jsoup.connect(url).get();
				e = doc.getElementById(navXmlIdTag).getElementsByTag(hyperlinkXmltag);
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
				int startpoint = producturl.indexOf(paternProductLink) + offsetPaternProductLink;
				String nr = producturl.substring(startpoint, startpoint
						+ sizeProductNr);
				cleanProductnr(nr);
				productnr.add(nr);
				// TODO controlleer wrm hij naa een lange periode een
				// java.systeem.outofmemory error geeft
				alternateCrawler.crawl(nr, productDatabase);

			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}

	}

	private void cleanProductnr(String productNr) {
		productNr = productNr.replace("?", "");
		productNr = productNr.replace("t", "");

	}

	private void findProducts() {
		for (int i = 0; i < links.size(); i++) {

			String listinglink = links.get(i);

			if (listinglink.contains(paternListinLink)) {

				listinglink = listinglink + productListingUrlModifier;
				url = urlBase + listinglink;

				try {
					doc = Jsoup.connect(url).get();
					e = doc.getElementsByClass(productLinkXmlClassTag);

					for (int j = 0; j < e.size(); j++) {
						String plink = e.get(j).getElementsByTag(hyperlinkXmltag)
								.attr(hyperlinkAttributetag);
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

			String link = elements.get(i).attr(hyperlinkAttributetag);

			if (link.contains(paternNavigationLink)
					|| link.contains(paternProductLink)) {

				if (!links.contains(link)) {
					links.add(link);
				}
			}

		}

	}

}
