package tornado.org;

import javafx.application.Application;
import tornado.org.fx.Gui;
import tornado.org.neo4j.constants.NEOConstants;
import tornado.org.settings.Settings;

import java.util.List;


public class Start {

    public static void main(String[] args) throws Exception {

        Settings.setAlternateEndstate(false);
        Settings.setMycomEndstate(false);
        Settings.setParadigitEndstate(false);

        switch (args[0].toLowerCase()) {
            case "windows":
                Settings.setOS(NEOConstants.DB_PATH_WINDOWS);
                break;
            case "ubuntu":
                Settings.setOS(NEOConstants.DB_PATH_UBUNTU);
                break;
            default:
                break;

        }

        switch (args[1].toLowerCase()) {
            case "crawl":
                startAlternateCrawler();
                //startMycomCrawler();
                startParadigitCrawler();
                break;
            case "alternate":
                startAlternateCrawler();
                break;
            case "paradigit":
                startParadigitCrawler();
                break;
            case "debug":
                Application.launch(Gui.class, (String[]) null);
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