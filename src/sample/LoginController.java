package sample;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

// ********************************   Classe LoginControlle     *********
// l'utilisateur ne peut pas accéder au page de principale que s'il saisie
// un nom d'utilisateur et un mot de passe corrects

public class LoginController implements Initializable {

    @FXML // Champ de saisir du nom
    private JFXTextField userName;

    @FXML //Champ de saisir du mot de passe
    private JFXPasswordField passWorld;

    @FXML // Pour afficher le message d'erreur .
    private Label labelErreur;

    Parent root;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            root = FXMLLoader.load(getClass().getResource("matrix.fxml"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
    }

    @FXML
    private void login()  {
        String user = userName.getText();
        String pass = passWorld.getText();

        if(user.equals("fpo") && pass.equals("2020")){
            Main.primaryStage.hide();
           Scene scene = new Scene(root);
           Main.primaryStage.setScene(scene);
           Main.primaryStage.setResizable(true);
           Main.primaryStage.setMaximized(true);
           Main.primaryStage.show();
        }else{
            labelErreur.setText("Login ou mot de passe incorrect");

        }
    }

    @FXML
    public void exit(){
        System.exit(0);
    }



}
