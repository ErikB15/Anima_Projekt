package Code_Package;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MyApplication extends javafx.application.Application {



    //denna metod kan teknsikt sätt ligga i controllern som lägget är just nu sålänge vi ärver Application. Anledning till att den ligger kvar här
    // är för att jag inte orkar flytta den för metoden var skapad i denna klassen från början. I början fanns ingen controller jag skspade den, så det är lite som vi har 2 controllers just nu.
    @Override
    public void start(Stage stage) throws Exception {
        // FXML fil är den filen som fx jobbar i och den som är kopplad till scenebuilder.
        // Här skapar vi en "loader" för den filen som är inne i getResource metoden (den som står innan för " ", man kan byta fil så loadar man en anna fil om man vill
        FXMLLoader loader = new FXMLLoader(MyApplication.class.getResource("/GuiStartMeny.fxml"));

        // Här precis som i java Swing så skapar vi en scene, vi  loadar sen den loadern som vi skapade ovan.
        Scene scene = new Scene(loader.load());

        stage.setScene(scene);
        //vi visar scenen.
        stage.show();
    }
}