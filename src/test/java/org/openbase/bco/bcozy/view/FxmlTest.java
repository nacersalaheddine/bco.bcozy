package org.openbase.bco.bcozy.view;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.Test;
import org.openbase.bco.bcozy.view.mainmenupanes.SettingsPane;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * @author vdasilva
 */
public class FxmlTest extends Application {
    /**
     * Application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsMenu.class);


    @Test
    public void test() {
        CenterPaneTest.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane pane;
        BorderPane main = new BorderPane();
        Scene scene = new Scene(main, 600, 300);
        primaryStage.setScene(scene);


        try {

            URL url = getClass().getClassLoader().getResource("test.fxml");
            if (url == null) {
                throw new RuntimeException("test.fxml not found");
            }

            FXMLLoader loader = new FXMLLoader(url);
            pane = loader.load();
            main.getChildren().addAll(pane);


        } catch (Exception ex) {
            ExceptionPrinter.printHistory("Content could not be loaded", ex, LOGGER);
        }

        primaryStage.show();

    }

}


