package tornado.org.neo4j;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import tornado.org.cypherspider.constants.CSConstants;
import tornado.org.cypherspider.objects.Product;
import tornado.org.neo4j.constants.NEOConstants;
import tornado.org.settings.Settings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProductDatabase {
	// TODO hiervan mag er maar een van zijn anders blokkeert het de database en kan je maar een thread starten.
    private static GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(Settings.getOS());
    private static ExecutionEngine engine;


    public void createDB() {
     //   this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(NEOConstants.DB_PATH);
        engine = new ExecutionEngine(graphDb);
    }

    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });

        System.out.println(NEOConstants.DB_SHUTDOWN_MESSAGE);
    }

    public String query(String query) {

        System.out.println(NEOConstants.QUERY_ANNOUNCER + query);

        //ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result;
        result = engine.execute(query);
        return result.dumpToString();
    }

    public void createAlternateProductNodes(Product product) {

        List<String> productAttributes = product.getAttributes();
        List<String> productValues = product.getValues();
        //TODO ExecutionEngine wordt te vaak aangemaakt, zorgt voor Crash, zie of het niet een globale variable kan worden 
       // ExecutionEngine engine = new ExecutionEngine(graphDb);

        executeQuery(productDelete(product), engine);
        executeQuery(productMerge(product), engine);
        executeQuery(websiteMerge(product), engine);
        executeQuery(productWebsiteRelationship(product), engine);

        if (productAttributes.size() == productValues.size()) {
            for (int i = 0; i < productAttributes.size() && i < productValues.size(); i++) {

                executeQuery(productAttributeMerge(productAttributes.get(i), productValues.get(i)), engine);
                executeQuery(productAttributeRelationship(product, productAttributes.get(i), productValues.get(i)), engine);

            }
        }
    }

    private String productMerge(Product product) {
        // "MERGE (p:Product { name : '" + name + "', productnumber: '" + productNumber + "', price : " + price + ", date: '" + getDateTime() + "' })"
        StringBuilder query = new StringBuilder();
        query.append(NEOConstants.PRODUCT_MERGE_QUERY[0])
                .append(product.getName().replaceAll(CSConstants.BACKSLASH, CSConstants.EMPTY).replaceAll(CSConstants.APOSTROPHE, CSConstants.EMPTY))
                .append(NEOConstants.PRODUCT_MERGE_QUERY[1])
                .append(product.getType())
                .append(NEOConstants.PRODUCT_MERGE_QUERY[2])
                .append(product.getProductNumber())
                .append(NEOConstants.PRODUCT_MERGE_QUERY[3])
                .append(product.getPrice())
                .append(NEOConstants.PRODUCT_MERGE_QUERY[4])
                .append(getDateTime())
                .append(NEOConstants.PRODUCT_MERGE_QUERY[5]);
        return query.toString();
    }

    private String productDelete(Product product) {
        //"MATCH (p:Product)-[r]-() WHERE p.name = '" + name + "' AND p.productnumber = '" + productnumber + "' DELETE p, r"
        StringBuilder query = new StringBuilder();
        query.append(NEOConstants.PRODUCT_DELETE_QUERY[0])
                .append(product.getName())
                .append(NEOConstants.PRODUCT_DELETE_QUERY[1])
                .append(product.getProductNumber())
                .append(NEOConstants.PRODUCT_DELETE_QUERY[2]);
        return query.toString();
    }

    private String websiteMerge(Product product) {
        // "MERGE (w:Website { url : '" + site + "' })";
        StringBuilder query = new StringBuilder();
        query.append(NEOConstants.WEBSITE_MERGE_QUERY[0])
                .append(product.getSite())
                .append(NEOConstants.WEBSITE_MERGE_QUERY[1]);

        return query.toString();
    }

    private String productWebsiteRelationship(Product product) {
        // "MATCH (p:Product),(w:Website) WHERE p.name = '" + name + "' AND p.price =" + price + " AND w.url = '" + site + "' MERGE (p)-[r:BELONGS_TO]->(w) ";
        StringBuilder query = new StringBuilder();
        query.append(NEOConstants.PRODUCT_WEBSITE_RELATIONSHIP_QUERY[0])
                .append(product.getName().replaceAll(CSConstants.BACKSLASH, CSConstants.EMPTY).replaceAll(CSConstants.APOSTROPHE, CSConstants.EMPTY))
                .append(NEOConstants.PRODUCT_WEBSITE_RELATIONSHIP_QUERY[1])
                .append(product.getPrice())
                .append(NEOConstants.PRODUCT_WEBSITE_RELATIONSHIP_QUERY[2])
                .append(product.getSite())
                .append(NEOConstants.PRODUCT_WEBSITE_RELATIONSHIP_QUERY[3]);

        return query.toString();
    }

    private String productAttributeMerge(String attribute, String value) {
        //"MERGE (a:Attribute { type : '" + productAttributes.get(i) + "', value : '" + productValues.get(i) + "' })";
        StringBuilder query = new StringBuilder();
        query.append(NEOConstants.PRODUCT_ATTRIBUTE_MERGE_QUERY[0])
                .append(attribute.replaceAll(CSConstants.BACKSLASH, CSConstants.EMPTY).replaceAll(CSConstants.APOSTROPHE, CSConstants.EMPTY))
                .append(NEOConstants.PRODUCT_ATTRIBUTE_MERGE_QUERY[1])
                .append(value.replaceAll(CSConstants.BACKSLASH, CSConstants.EMPTY).replaceAll(CSConstants.APOSTROPHE, CSConstants.EMPTY))
                .append(NEOConstants.PRODUCT_ATTRIBUTE_MERGE_QUERY[2]);

        return query.toString();
    }

    private String productAttributeRelationship(Product product, String attribute, String value) {
        //"MATCH (p:Product),(a:Attribute) WHERE p.name = '" + name + "' AND p.price =" + price + " AND a.type = '" + productAttributes.get(i) + "' AND a.value = '" + productValues.get(i) + "' MERGE (p)-[r:HAS_PROPERTY]->(a) ";
        StringBuilder query = new StringBuilder();
        query.append(NEOConstants.PRODUCT_ATTRIBUTE_RELATIONSHIP_QUERY[0])
                .append(product.getName().replaceAll(CSConstants.BACKSLASH, CSConstants.EMPTY.replaceAll(CSConstants.APOSTROPHE, CSConstants.EMPTY)))
                .append(NEOConstants.PRODUCT_ATTRIBUTE_RELATIONSHIP_QUERY[1])
                .append(product.getPrice())
                .append(NEOConstants.PRODUCT_ATTRIBUTE_RELATIONSHIP_QUERY[2])
                .append(attribute.replaceAll(CSConstants.BACKSLASH, CSConstants.EMPTY).replaceAll(CSConstants.APOSTROPHE, CSConstants.EMPTY))
                .append(NEOConstants.PRODUCT_ATTRIBUTE_RELATIONSHIP_QUERY[3])
                .append(value.replaceAll(CSConstants.BACKSLASH, CSConstants.EMPTY).replaceAll(CSConstants.APOSTROPHE, CSConstants.EMPTY))
                .append(NEOConstants.PRODUCT_ATTRIBUTE_RELATIONSHIP_QUERY[4]);
        return query.toString();
    }

    private void executeQuery(String query, ExecutionEngine engine) {
        if (NEOConstants.SYSTEM_OUTPUT) {
           System.out.println(NEOConstants.QUERY_ANNOUNCER + query);
        }
        engine.execute(query);
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat(NEOConstants.DATE_FORMAT);
        Date date = new Date();
        return dateFormat.format(date);
    }
}
