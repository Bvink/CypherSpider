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

/*
 * TODO uitzoeken wrm dit gebeurt
 * 
 * Exception in thread "main" java.lang.OutOfMemoryError: GC overhead limit exceeded
	at java.util.Arrays.copyOf(Unknown Source)
	at org.neo4j.kernel.impl.nioneo.store.LongerShortString.decode(LongerShortString.java:766)
	at org.neo4j.kernel.impl.nioneo.store.PropertyType$11.readProperty(PropertyType.java:271)
	at org.neo4j.kernel.impl.api.store.DiskLayer.loadAllPropertiesOf(DiskLayer.java:476)
	at org.neo4j.kernel.impl.api.store.DiskLayer.nodeGetAllProperties(DiskLayer.java:418)
	at org.neo4j.kernel.impl.api.store.CacheLayer$2.load(CacheLayer.java:103)
	at org.neo4j.kernel.impl.api.store.CacheLayer$2.load(CacheLayer.java:99)
	at org.neo4j.kernel.impl.core.Primitive.ensurePropertiesLoaded(Primitive.java:89)
	at org.neo4j.kernel.impl.core.Primitive.getProperties(Primitive.java:58)
	at org.neo4j.kernel.impl.api.store.PersistenceCache.nodeGetProperties(PersistenceCache.java:151)
	at org.neo4j.kernel.impl.api.store.CacheLayer.nodeGetAllProperties(CacheLayer.java:272)
	at org.neo4j.kernel.impl.api.StateHandlingStatementOperations.nodeGetAllProperties(StateHandlingStatementOperations.java:739)
	at org.neo4j.kernel.impl.api.StateHandlingStatementOperations.nodeGetProperty(StateHandlingStatementOperations.java:708)
	at org.neo4j.kernel.impl.api.ConstraintEnforcingEntityOperations.nodeGetProperty(ConstraintEnforcingEntityOperations.java:276)
	at org.neo4j.kernel.impl.api.OperationsFacade.nodeGetProperty(OperationsFacade.java:166)
	at org.neo4j.cypher.internal.spi.v2_1.TransactionBoundQueryContext$NodeOperations.getProperty(TransactionBoundQueryContext.scala:146)
	at org.neo4j.cypher.internal.compiler.v2_1.spi.DelegatingOperations.getProperty(DelegatingQueryContext.scala:116)
	at org.neo4j.cypher.internal.compiler.v2_1.spi.ExceptionTranslatingQueryContext$ExceptionTranslatingOperations.org$neo4j$cypher$internal$compiler$v2_1$spi$ExceptionTranslatingQueryContext$ExceptionTranslatingOperations$$super$getProperty(ExceptionTranslatingQueryContext.scala:127)
	at org.neo4j.cypher.internal.compiler.v2_1.spi.ExceptionTranslatingQueryContext$ExceptionTranslatingOperations$$anonfun$getProperty$1.apply(ExceptionTranslatingQueryContext.scala:127)
	at org.neo4j.cypher.internal.compiler.v2_1.spi.ExceptionTranslatingQueryContext.org$neo4j$cypher$internal$compiler$v2_1$spi$ExceptionTranslatingQueryContext$$translateException(ExceptionTranslatingQueryContext.scala:152)
	at org.neo4j.cypher.internal.compiler.v2_1.spi.ExceptionTranslatingQueryContext$ExceptionTranslatingOperations.getProperty(ExceptionTranslatingQueryContext.scala:127)
	at org.neo4j.cypher.internal.compiler.v2_1.spi.DelegatingOperations.getProperty(DelegatingQueryContext.scala:116)
	at org.neo4j.cypher.internal.compiler.v2_1.helpers.MapSupport$PropertyContainerMap$$anonfun$get$1.apply(MapSupport.scala:57)
	at org.neo4j.cypher.internal.compiler.v2_1.helpers.MapSupport$PropertyContainerMap$$anonfun$get$1.apply(MapSupport.scala:57)
	at scala.Option.flatMap(Option.scala:170)
	at org.neo4j.cypher.internal.compiler.v2_1.helpers.MapSupport$PropertyContainerMap.get(MapSupport.scala:57)
	at org.neo4j.cypher.internal.compiler.v2_1.helpers.MapSupport$PropertyContainerMap.get(MapSupport.scala:52)
	at scala.collection.MapLike$class.getOrElse(MapLike.scala:126)
	at org.neo4j.cypher.internal.compiler.v2_1.helpers.MapSupport$PropertyContainerMap.getOrElse(MapSupport.scala:52)
	at org.neo4j.cypher.internal.compiler.v2_1.commands.expressions.Property.apply(Property.scala:38)
	at org.neo4j.cypher.internal.compiler.v2_1.commands.Equals.isMatch(ComparablePredicate.scala:61)
	at org.neo4j.cypher.internal.compiler.v2_1.commands.And.isMatch(Predicate.scala:101)*/
public class ProductDatabase {

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

        String query = "MERGE (p:Product { name : '" + name + "', productnumber: '" + productNumber + "', price : " + price + ", date: '" + getDateTime() + "' })";
        executeQuery(query, engine);

        query = "MERGE (w:Website { url : '" + site + "' })";
        executeQuery(query, engine);

        query = "MATCH (p:Product),(w:Website) "
                + " WHERE p.name = '" + name + "' AND p.price =" + price + " AND w.url = '" + site + "'"
                + " MERGE (p)-[r:BELONGS_TO]->(w) ";

        executeQuery(query, engine);

        if (productAttributes.size() == productValues.size()) {
            for (int i = 0; i < productAttributes.size() && i < productValues.size(); i++) {
                query = "MERGE (a:Attribute { type : '" + productAttributes.get(i) + "', value : '" + productValues.get(i) + "' })";
                executeQuery(query, engine);

                query = "MATCH (p:Product),(a:Attribute) "
                        + " WHERE p.name = '" + name + "' AND p.price =" + price + " AND a.type = '" + productAttributes.get(i) + "' AND a.value = '" + productValues.get(i) + "'"
                        + " MERGE (p)-[r:HAS_PROPERTY]->(a) ";
                executeQuery(query, engine);
            }
        }
    }

    private void executeQuery(String query, ExecutionEngine engine) {
        System.out.println("method query\n" + query);
        engine.execute(query);
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
