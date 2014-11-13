package tornado.org.neo4j;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphalgo.impl.util.PriorityMap;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.collection.IteratorUtil;

import javax.swing.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        String vraag = query;

        System.out.println("method query\n" + vraag);

        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result;
        result = engine.execute(vraag);
        String dump=result.dumpToString();
        return dump;
    }

    public void createProductNodes(String site, String product, String price, String productNumber, List<String> productAttributes, List<String> productValues) {

        String query = "MERGE (p:Product { name : '" + product + "', productnumber: '" + productNumber + "', price : '" + price + "', date: '" + getDate() + "' })";

        ExecutionEngine engine = new ExecutionEngine(graphDb);
        engine.execute(query);

        query = "MERGE (w:Website { url : '" + site + "' })";

        engine.execute(query);

        query = "MATCH (p:Product),(w:Website) "
                + " WHERE p.name = '" + product +"' AND p.price ='" + price + "' AND w.url = '" + site + "'"
                + " MERGE (p)-[r:BELONGS_TO]->(w) ";

        engine.execute(query);

        if (productAttributes.size() == productValues.size()) {
            for (int i = 0; i < productAttributes.size() && i < productValues.size(); i++) {
                query = "MERGE (a:Attribute { type : '" + productAttributes.get(i) + "', value : '" + productValues.get(i) + "' })";

                engine.execute(query);

                query = "MATCH (p:Product),(a:Attribute) "
                        + " WHERE p.name = '" + product +"' AND p.price ='" + price + "' AND a.type = '" + productAttributes.get(i) + "' AND a.value = '" + productValues.get(i) + "'"
                        + " MERGE (p)-[r:HAS_PROPERTY]->(a) ";
                engine.execute(query);
            }
        }
    }

    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
