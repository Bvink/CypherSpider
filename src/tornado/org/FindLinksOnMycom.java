package tornado.org;

import java.io.IOException;
import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.mozilla.javascript.NativeArray;

import tornado.org.cypherspider.AlternateCrawler;
import tornado.org.neo4j.ProductDatabase;

public class FindLinksOnMycom extends Thread {
	// private final CharSequence paternListinLink = "/html/product/listing";
	private final String urlMycom = "http://m.mycom.nl";
	private final String paternProductLink = "http://www.m.mycom.nl/componenten/";
	private final CharSequence paternNavigationLink = "/componenten/";
	// private final String productListingUrlModifier = "&size=500#listingResult";

	private ArrayList<String> Navlinks = new ArrayList<>();
	private ArrayList<String> productlinks = new ArrayList<>();
	private ArrayList<String> productnr = new ArrayList<>();

	private static org.jsoup.nodes.Document doc;
	private Elements e;
	private final ProductDatabase productDatabase = new ProductDatabase();

	//TODO maak nieuwe Crawler voor Mycom 
	//private final AlternateCrawler alternateCrawler = new AlternateCrawler();

	private static String url = "http://www.mycom.nl/componenten";

	private static final int sizeProductNr = 7;
	private static final String hyperlinkXmltag = "a";
	private static final String hyperlinkAttributetag = "href";
	private static final String navXmlIdTag = "main_content";
	private static final String productLinkXmlClassTag = "product_image";


	public void run() {

		
		try {
			doc = Jsoup.connect(url).get();
			System.out.println(doc.toString());
			
			e = doc.getElementById(navXmlIdTag).getElementsByTag(hyperlinkXmltag); //.getElementsByClass("primary_list bordered_list productcategory_list devider bg_light_gradient");
			System.out.println(doc.toString());
			
			int numberFound = e.size();
			//e = e.get(numberFound-1).getElementsByTag("a");
			
			parselinks(e);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		findProducts();
		
		System.out.println(productlinks.toString());
		
		
		
		//Navlinks.get(0) ; // heeft alle links naar lijsten van catergories
		
		// De product lijst pagina's moeten gerendered worden met javascript 
		// 
		/*
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine se = mgr.getEngineByName("JavaScript");
		try {
		  se.eval(file);
		  NativeArray array = (NativeArray)se.get("array");
		  for(int i = 0; i < array.getLength(); i++){
		    if(array.get(i)!=null){
		      NativeArray elementArray = (NativeArray)array.get(i);
		      System.out.println("Object: " + elementArray);
		      System.out.println("name: " + elementArray.get("name", elementArray));
		      System.out.println("point: " +  elementArray.get("point", elementArray));
		    }
		  }
		}
		catch (ScriptException e) {
		     
		}
		*/
		
		
		
		
		
	}
	
	
	private void findProducts() {
		for (int i = 0; i < Navlinks.size(); i++) {

			String listinglink = Navlinks.get(i);

			if (listinglink.contains(this.paternNavigationLink)) {

				url = this.urlMycom + listinglink;

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
			System.out.println(link);

			if (link.contains(paternNavigationLink)
					|| link.contains(paternProductLink)) {

				if (!Navlinks.contains(link)) {
					Navlinks.add(link);
				}
			}

		}

	}

	
	
	

	public FindLinksOnMycom() {
		// TODO Auto-generated constructor stub
	}

	public FindLinksOnMycom(Runnable target) {
		super(target);
		// TODO Auto-generated constructor stub
	}

	public FindLinksOnMycom(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public FindLinksOnMycom(ThreadGroup group, Runnable target) {
		super(group, target);
		// TODO Auto-generated constructor stub
	}

	public FindLinksOnMycom(ThreadGroup group, String name) {
		super(group, name);
		// TODO Auto-generated constructor stub
	}

	public FindLinksOnMycom(Runnable target, String name) {
		super(target, name);
		// TODO Auto-generated constructor stub
	}

	public FindLinksOnMycom(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
		// TODO Auto-generated constructor stub
	}

	public FindLinksOnMycom(ThreadGroup group, Runnable target, String name,
			long stackSize) {
		super(group, target, name, stackSize);
		// TODO Auto-generated constructor stub
	}

}
