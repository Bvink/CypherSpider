package tornado.org;

import javafx.application.Application;
import tornado.org.cypherspider.productcrawlers.FindLinksOnAlternate;
import tornado.org.cypherspider.productcrawlers.FindLinksOnMycom;
import tornado.org.cypherspider.productcrawlers.FindLinksOnParadigit;
import tornado.org.fx.Gui;
import tornado.org.neo4j.constants.NEOConstants;
import tornado.org.settings.Settings;


public class Start {

    public static void main(String[] args) throws Exception {

        Settings.setAlternateEndstate(false);
        Settings.setMycomEndstate(false);
        Settings.setParadigitEndstate(false);

        if (args[0] == null) { args[0] = "help"; }

        switch (args[0].toLowerCase()) {
            case "windows":
                Settings.setOS(NEOConstants.DB_PATH_WINDOWS);
                setupCrawler(args[1]);
                break;
            case "ubuntu":
                Settings.setOS(NEOConstants.DB_PATH_UBUNTU);
                setupCrawler(args[1]);
                break;
            default:
                System.out.println("Please supply an OS!");
                System.out.println("windows");
                System.out.println("ubuntu");
                break;

        }
    }

    private static void setupCrawler(String arg) {
        switch (arg.toLowerCase()) {
            case "crawl":
                startAlternateCrawler();
                //startMycomCrawler();
                startParadigitCrawler();
                System.out.println("Crawl mode go!");
                break;
            case "alternate":
                startAlternateCrawler();
                System.out.println("Alternate mode go!");
                break;
            case "paradigit":
                startParadigitCrawler();
                System.out.println("Paradigit mode go!");
                break;
            case "debug":
                Application.launch(Gui.class, (String[]) null);
                System.out.println("Debug mode go!");
                break;
            default:
                System.out.println("Please supply an argument!");
                System.out.println("crawl");
                System.out.println("alternate");
                System.out.println("paradigit");
                System.out.println("debug");
                break;
        }
    }

    private static void startAlternateCrawler() {
        FindLinksOnAlternate alternate = new FindLinksOnAlternate();
        alternate.start();
    }

    private static void startMycomCrawler() {
        FindLinksOnMycom mycom = new FindLinksOnMycom();
        mycom.start();
    }

    private static void startParadigitCrawler() {
        FindLinksOnParadigit paradigit = new FindLinksOnParadigit();
        paradigit.start();
    }
}