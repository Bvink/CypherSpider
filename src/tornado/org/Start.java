package tornado.org;

import javafx.application.Application;
import tornado.org.fx.Gui;


public class Start {

    public static void main(String[] args) throws Exception {

    	// FindLinksOnAlternate findLinks = new FindLinksOnAlternate();
    	//findLinks.run();
    	FindLinksOnMycom mycom = new FindLinksOnMycom() ; 
    	mycom.run();
      //  Application.launch(Gui.class, (String[]) null);
    }
}