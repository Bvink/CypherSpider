package tornado.org.cypherspider;

import java.util.List;

public class Product {

    private String site;
    private String productName;
    private String price;
    private String productNumber;
    private List<String> attributes;
    private List<String> values;

    public void setSite(String site) {
        this.site = site;
    }

    public String getSite() {
        return site;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice () {
        return price;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setValues(List<String> productValues) {
        this.values = productValues;
    }

    public List<String> getValues() {
        return values;
    }

}
