package tornado.org;

import javafx.application.Application;
import tornado.org.fx.Gui;


public class Start {

    public static void main(String[] args) throws Exception {

    	FindLinksOnAlternate findLinks = new FindLinksOnAlternate();
    	//findLinks.start(); 
    	FindLinksOnMycom mycom = new FindLinksOnMycom() ;
    	mycom.start();
    	FindLinksOnParadigit onParadigit = new FindLinksOnParadigit() ; 
    	onParadigit.start();
    	
       // Application.launch(Gui.class, (String[]) null);
    }
}