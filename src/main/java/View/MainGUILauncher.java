package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

public class MainGUILauncher extends Application {
//Här sker den "riktiga" launchen av GUI. nu kan vi flytta på Main och lägga till mer launch där. Main bara "Kallar" på denna för att starta GUI.
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/StartScreen.fxml"));
        Parent root = loader.load();

        GUIManager controller = loader.getController();
        controller.setStage(stage);

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}