package tornado.org.cypherspider.fx;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tornado.org.cypherspider.Crawler;
import tornado.org.neo4j.Database;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML private Button fetchInfoButton;
    @FXML private Button queryButton;
    @FXML private TextField input;
    @FXML private TextArea output;
    @FXML private TextArea query;

    private static final Database db = new Database();

    public void initialize(URL url, ResourceBundle resourceBundle) {

        db.createDB();
        final Crawler crawl = new Crawler();

        fetchInfoButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                output.setText("");

                output.setText(crawl.crawl(input.getText(), db));
            }
        });

        queryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                output.setText(db.query(query.getText()));
            }
        });

    }

    public static void shutdownDB() {
        db.registerShutdownHook();
    }

}
