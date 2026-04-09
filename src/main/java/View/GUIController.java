package View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIController {


    @FXML
    private Button logoutButton;
    @FXML
    private AnchorPane scenePane;
    Stage stage;

    @FXML
    private ImageView myImageView;

    private Image myImage;


    // Detta är en metod som körs när vi väl startar/kör vår fxml fil. Vad denna metod gör är att skapa en image (den som ligger i resource mappen).
    // Syftet är att i scenebuilder lade jag till en "imageView", där man kan lägga till valfria nedladdade bilder.
    //vi visar inte bilden direkt men för att myImage inte ska vara null när vi väl ska visa den.
    @FXML
    public void initialize() {
        myImage = new Image(getClass().getResource("/CardGameBoard.jpg").toExternalForm());
    }


    //Detta är en metod jag skapa för att starta nästa meny. Samma princip som start metoden i MyApplication.
    // Vi skapr en loader, vi loadar loadern vi skapade, skapar en ny scene sen visar vi den.
    // Denna metod är anropad från FXML-filen "GuiStartMeny" i resource mappen.
    @FXML
    private void handleStart(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Anima_StartMenu.fxml"));

        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    // Metoden visar själva gameBoarden (bilden som ligger i resource mappen) när man tryckt på Start game knappen i gui:t.
    @FXML
    public void displayGameBoard() {
        myImageView.setImage(myImage);
    }


    @FXML
    public void logout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("LOGOUT");
        alert.setHeaderText("You are about to logout!");
        alert.setContentText("Do you want to save before logging out?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            System.out.println("You have logged out");
            stage.close();
        }
    }
}