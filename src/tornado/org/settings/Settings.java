package tornado.org.settings;

public class Settings {

    private static boolean alternateEndstate = false;
    private static boolean mycomEndstate = false;
    private static boolean paradigitEndstate = false;
    private static String OS_PATH = "c:/Neo4J";

    public static void setAlternateEndstate(boolean state) {
        alternateEndstate = state;
    }

    public static void setMycomEndstate(boolean state) {
        mycomEndstate = state;
    }

    public static void setParadigitEndstate(boolean state) {
        paradigitEndstate = state;
    }

    public static void setOS(String chosenOS) { OS_PATH = chosenOS; }

    public static boolean getAlternateEndstate() {
        return alternateEndstate;
    }

    public static boolean getMycomEndstate() {
        return mycomEndstate;
    }

    public static boolean getParadigitEndstate() {
        return paradigitEndstate;
    }

    public static boolean getEndstate() {
        return alternateEndstate && mycomEndstate && paradigitEndstate;
    }

    public static String getOS() { return OS_PATH;}
}
