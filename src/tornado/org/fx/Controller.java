package tornado.org.fx;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tornado.org.cypherspider.AlternateCrawler;
import tornado.org.neo4j.ProductDatabase;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML private Button fetchInfoButton;
    @FXML private Button queryButton;
    @FXML private TextField input;
    @FXML private TextArea output;
    @FXML private TextArea query;

    private static final ProductDatabase DB = new ProductDatabase();

    public void initialize(URL url, ResourceBundle resourceBundle) {

        DB.createDB();
        final AlternateCrawler crawl = new AlternateCrawler();

        fetchInfoButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                output.setText("");

                output.setText(crawl.crawl(input.getText(), DB));
            }
        });

        queryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                output.setText(DB.query(query.getText()));
            }
        });

    }

    public static void shutdownDB() {
        DB.registerShutdownHook();
    }

}
