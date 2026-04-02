package Code_Package;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class MyApplication extends javafx.application.Application {

    //Denna klass/metod är den som hanterar uppstarten av progrommet. Klassens ansvar är att programstart
    // Controllerns ansvar är mer att regera
    @Override
    public void start(Stage stage) throws Exception {

        // FXML fil är den filen som fx jobbar i och den som är kopplad till scenebuilder.
        // Här skapar vi en "loader" för den filen som är inne i getResource metoden
        // (den som står innan för " ", man kan byta fil så loadar man en annan fil om man vill)
        FXMLLoader loader = new FXMLLoader(MyApplication.class.getResource("/GuiStartMeny.fxml"));

        // Här precis som i java Swing så skapar vi en scene, vi loadar sen den loadern som vi skapade ovan.
        Scene scene = new Scene(loader.load());

        stage.setScene(scene);

        //vi visar scenen.
        stage.show();

        // detta körs när man försöker stänga fönstret (krysset)
        stage.setOnCloseRequest(event -> {

            // vi stoppar default beteendet (att den bara stängs direkt)
            event.consume();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("LOGOUT");
            alert.setHeaderText("You are about to logout!");
            alert.setContentText("Do you want to save before logging out?");

            // om användaren inte trycker OK så stängs inte programmet
            if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }

            // annars stänger vi programmet
            stage.close();
            System.out.println("You have logged out");
        });
    }
}