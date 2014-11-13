package tornado.org.neo4j;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import tornado.org.cypherspider.Product;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Database {

    private GraphDatabaseService graphDb;
    private final String DB_PATH = "c:/Neo4J";

    public void createDB() {
        this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
    }

    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });

        System.out.println("graphDB shut down.");
    }

    public String query(String query) {

        System.out.println("method query\n" + query);

        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result;
        result = engine.execute(query);
        return result.dumpToString();
    }

    public void createProductNodes(Product product) {

        String productNumber = product.getProductNumber();
        String name = product.getName();
        String price = product.getPrice();
        String site = product.getSite();
        List<String> productAttributes = product.getAttributes();
        List<String> productValues = product.getValues();

        ExecutionEngine engine = new ExecutionEngine(graphDb);

        String query = "MERGE (p:Product { name : '" + name + "', productnumber: '" + productNumber + "', price : '" + price + "', date: '" + getDate() + "' })";
        executeQuery(query, engine);

        query = "MERGE (w:Website { url : '" + site + "' })";
        executeQuery(query, engine);

        query = "MATCH (p:Product),(w:Website) "
                + " WHERE p.name = '" + name + "' AND p.price ='" + price + "' AND w.url = '" + site + "'"
                + " MERGE (p)-[r:BELONGS_TO]->(w) ";

        executeQuery(query, engine);

        if (productAttributes.size() == productValues.size()) {
            for (int i = 0; i < productAttributes.size() && i < productValues.size(); i++) {
                query = "MERGE (a:Attribute { type : '" + productAttributes.get(i) + "', value : '" + productValues.get(i) + "' })";
                executeQuery(query, engine);


                query = "MATCH (p:Product),(a:Attribute) "
                        + " WHERE p.name = '" + name + "' AND p.price ='" + price + "' AND a.type = '" + productAttributes.get(i) + "' AND a.value = '" + productValues.get(i) + "'"
                        + " MERGE (p)-[r:HAS_PROPERTY]->(a) ";
                executeQuery(query, engine);
            }
        }
    }

    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void executeQuery(String query, ExecutionEngine engine) {
        System.out.println("method query\n" + query);
        engine.execute(query);
    }
}
