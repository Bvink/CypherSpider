package tornado.org;

import javafx.application.Application;
import tornado.org.fx.Gui;
import tornado.org.settings.Settings;


public class Start {

    public static void main(String[] args) throws Exception {

        Settings.setAlternateEndstate(false);
        Settings.setMycomEndstate(false);
        Settings.setParadigitEndstate(false);
        /**
    	FindLinksOnAlternate findLinks = new FindLinksOnAlternate();
    	findLinks.start();
    	FindLinksOnMycom mycom = new FindLinksOnMycom() ;
    	mycom.start();
    	FindLinksOnParadigit onParadigit = new FindLinksOnParadigit() ; 
    	onParadigit.start();
    	**/

        Application.launch(Gui.class, (String[]) null);
    }
}