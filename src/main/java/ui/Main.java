package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main extends Application {

    private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("simplerp");

    @Override
    public void start(Stage primaryStage) throws Exception{


        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/main.fxml"));
        MainController c = new MainController();
        loader.setController(c);
        Parent root = loader.load();

        primaryStage.setTitle("Simple ERP");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> entityManagerFactory.close());
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }



}
