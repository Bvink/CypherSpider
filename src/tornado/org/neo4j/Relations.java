package tornado.org.neo4j;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

public class Relations {

    public enum Labels implements Label {
        Product,
        Attribute,
        Website;
    }

    public enum ProductValues implements RelationshipType {

        HAS_PROPERTY,
        BELONGS_TO;

        public static final String FROM = "from";
    }


}
