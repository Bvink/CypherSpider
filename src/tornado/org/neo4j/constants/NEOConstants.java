package tornado.org.neo4j.constants;

public class NEOConstants {

    public final static String DB_PATH_WINDOWS =  "c:/Neo4J";
    public final static String DB_PATH_UBUNTU = "/var/lib/neo4j/nucleus";
    public final static String DB_SHUTDOWN_MESSAGE = "graphDB shut down.";

    public final static Boolean SYSTEM_OUTPUT = false;

    public final static String QUERY_ANNOUNCER = "method query\n";
    public final static String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

    public final static String[] PRODUCT_MERGE_QUERY = {"MERGE (p:Product { name : '", "', productnumber: '", "', price : ", ", date: '", "' })"};
    public final static String[] WEBSITE_MERGE_QUERY = {"MERGE (w:Website { url : '", "' })"};
    public final static String[] PRODUCT_WEBSITE_RELATIONSHIP_QUERY = {"MATCH (p:Product),(w:Website) WHERE p.name = '", "' AND p.price =", " AND w.url = '", "' MERGE (p)-[r:BELONGS_TO]->(w) "};
    public final static String[] PRODUCT_ATTRIBUTE_MERGE_QUERY = {"MERGE (a:Attribute { type : '", "', value : '", "' })"};
    public final static String[] PRODUCT_ATTRIBUTE_RELATIONSHIP_QUERY = {"MATCH (p:Product),(a:Attribute) WHERE p.name = '", "' AND p.price =", " AND a.type = '", "' AND a.value = '", "' MERGE (p)-[r:HAS_PROPERTY]->(a) "};

}
