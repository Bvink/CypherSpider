package tornado.org.fx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tornado.org.fx.constants.FXConstants;

public class Gui extends Application {

    public void start(Stage stage) throws Exception {

    }

    public Gui() {
        Platform.runLater(new Runnable() {
            public void run() {
                try {
                    final Parent root = FXMLLoader.load(Controller.class.getResource(FXConstants.FXML_RESOURCE_LOCATION));
                    final Stage stage = new Stage() {{
                        setScene(new Scene(root, FXConstants.GUI_WIDTH, FXConstants.GUI_HEIGHT));
                        setTitle(FXConstants.GUI_TITLE);
                        setResizable(false);
                        show();
                    }};

                    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        public void handle(WindowEvent we) {
                            System.out.println(FXConstants.DATABASE_SHUTDOWN_MESSAGE);
                            Controller.shutdownDB();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
