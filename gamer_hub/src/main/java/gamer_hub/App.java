package gamer_hub;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gamer_hub/fxml/login.fxml"));
        Parent root = loader.load();
        
        gamer_hub.controllers.LoginController controller = loader.getController();
        controller.setStage(primaryStage);
        
        Scene scene = new Scene(root, 800, 600);
        
        // Загружаем CSS
        try {
            String cssPath = getClass().getResource("/gamer_hub/css/styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            System.out.println("✓ CSS загружен: " + cssPath);
        } catch (Exception e) {
            System.err.println("✗ Ошибка загрузки CSS: " + e.getMessage());
        }
        
        primaryStage.setTitle("Gamer Hub - Вход");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.show();
    }
    
    public static void main(String[] args)
    {
        launch(args);
    }
}