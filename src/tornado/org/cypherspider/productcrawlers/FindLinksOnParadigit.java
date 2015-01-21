package tornado.org.cypherspider.productcrawlers;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import tornado.org.cypherspider.pagecrawlers.ParadigitCrawler;
import tornado.org.neo4j.ProductDatabase;

import tornado.org.settings.Settings;

public class FindLinksOnParadigit extends Thread {

	private ArrayList<String> Navlinks = new ArrayList<>();
	private ArrayList<String> productlinks = new ArrayList<>();
	private ArrayList<String> productnr = new ArrayList<>();

	private static org.jsoup.nodes.Document doc;
	private Elements e;

	// TODO moet altijd dezelfde object blijven in de hele applicatie
	private static final ProductDatabase productDatabase = new ProductDatabase();

	private static final ParadigitCrawler PARADIGIT_CRAWLER = new ParadigitCrawler() ; 

	// TODO maak nieuwe Crawler voor Mycom
	// private final AlternateCrawler alternateCrawler = new AlternateCrawler();

	private static String url = "http://www.paradigit.nl";

	private static final String hyperlinkXmltag = "a";
	private static final String hyperlinkAttributetag = "href";
	private static final String navXmlClassTag = "PagerContainerTable";
	private static final String productLinkXmlClassTag = "itemlistcombined-productimagecontainer";

	public FindLinksOnParadigit() {

		// wrm moeilijk doen als het makkelijk kan

		Navlinks.add("/catalog/zpr_08ond/06_pccase/default.aspx");
		Navlinks.add("/catalog/zpr_08ond/12_optdriv/default.aspx");
		Navlinks.add("/catalog/zpr_08ond/01_memint/default.aspx");
		Navlinks.add("/catalog/zpr_02ops/01_inthdd/default.aspx");
		Navlinks.add("/catalog/zpr_02ops/01_intssd/default.aspx");
		Navlinks.add("/catalog/zpr_08ond/05_mobo/default.aspx");
		Navlinks.add("/catalog/zpr_08ond/04_cpu/default.aspx");
		Navlinks.add("/catalog/zpr_08ond/24_cpu/default.aspx");
		Navlinks.add("/catalog/zpr_08ond/03_vidcard/default.aspx");
		Navlinks.add("/catalog/zpr_08ond/07_psu/default.aspx");
		Navlinks.add("/catalog/zpr_05swa/01_windows/default.aspx");
		Navlinks.add("/catalog/zpr_08ond/21_bare/default.aspx");
		Navlinks.add("/catalog/zpr_08ond/21_bare/default.aspx");
		Navlinks.add("/catalog/zpr_08ond/14_control/default.aspx");
		Navlinks.add("/catalog/zpr_11kbl/12_firekbl/default.aspx");
		Navlinks.add("/catalog/zpr_08ond/15_sndcard/default.aspx");
		Navlinks.add("/catalog/zpr_08ond/19_tools/default.aspx");
		Navlinks.add("/catalog/zpr_08ond/17_cardrea/default.aspx");
		Navlinks.add("/catalog/zpr_08ond/23_coolers/default.aspx");
		Navlinks.add("/catalog/zpr_08ond/25_kpas/default.aspx");
		Navlinks.add("/catalog/zpr_11kbl/06_satakbl/default.aspx");
		Navlinks.add("/catalog/zpr_03avi/06_tvtuner/default.aspx");
		Navlinks.add("/catalog/zpr_08ond/22_ups/default.aspx");
		Navlinks.add("/catalog/zpr_11kbl/08_pwrkbl/default.aspx");
	}

	public void run() {

		try {
			for (int i = 0; i < Navlinks.size(); i++) {
				doc = Jsoup.connect(url + Navlinks.get(i)).get();
				//System.out.println(doc.toString());

				e = doc.getElementsByClass(navXmlClassTag);
				if (e.size() > 0) {
					e = e.get(0).getElementsByTag(hyperlinkXmltag);

					for (int j = 0; j < e.size(); j++) {
						String link = e.get(j).attr(hyperlinkAttributetag);
						if (!Navlinks.contains(link)) {
							Navlinks.add(link);
						}
					}
				}

				e = doc.getElementsByClass(productLinkXmlClassTag);
				for (int j = 0; j < e.size(); j++) {
					String productlink = e.get(j)
							.getElementsByTag(hyperlinkXmltag).get(0)
							.attr(hyperlinkAttributetag);
					productlinks.add(productlink);
				}
			}

			// parselinks(e);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		insertProducts();

		System.out.println(productlinks.size());
        Settings.setParadigitEndstate(true);

        if (Settings.getEndstate()) { productDatabase.registerShutdownHook(); }

    }

	private void insertProducts() {
		productDatabase.createDB();

		for (int i = 0; i < productlinks.size(); i++) {
			PARADIGIT_CRAWLER.crawl(url + productlinks.get(i), productDatabase);
		}

	}

	public FindLinksOnParadigit(Runnable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public FindLinksOnParadigit(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public FindLinksOnParadigit(ThreadGroup arg0, Runnable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public FindLinksOnParadigit(ThreadGroup arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public FindLinksOnParadigit(Runnable arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public FindLinksOnParadigit(ThreadGroup arg0, Runnable arg1, String arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	public FindLinksOnParadigit(ThreadGroup arg0, Runnable arg1, String arg2,
			long arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

}
