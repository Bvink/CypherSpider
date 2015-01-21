package tornado.org.core.api.utils;

import tornado.org.cypherspider.constants.CSConstants;

import java.util.ArrayList;
import java.util.List;

public class Combiner {

    public static StringBuilder combineLists(List<String> productAttributes, List<String> productValues) {
        List<String> combined = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        if (productAttributes.size() == productValues.size()) {
            for (int i = 0; i < productAttributes.size() && i < productValues.size(); i++) {
                combined.add(productAttributes.get(i) + CSConstants.SPACE + productValues.get(i));
            }
        }
        for (String s : combined) {
            sb.append(s);
            sb.append(CSConstants.LINE_SEPERATOR);
        }
        return sb;
    }
}
