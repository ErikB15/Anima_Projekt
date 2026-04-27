package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

public class MainGUILauncher extends Application {
//Här sker den "riktiga" launchen av GUI. nu kan vi flytta på Main och lägga till mer launch där. Main bara "Kallar" på denna för att starta GUI.

    public void start(Stage stage){
        try{
           Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("StartScreen.fxml"));
           Scene scene = new Scene(root);
           stage.setScene(scene);
           //stage.setFullScreen(true);
           stage.setResizable(false);
           stage.show();

        } catch(Exception e){
            System.out.println("nu är jag i MainGUILauncher");
            e.printStackTrace();
        }
    }
}