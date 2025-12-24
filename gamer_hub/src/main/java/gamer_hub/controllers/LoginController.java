package gamer_hub.controllers;

import gamer_hub.service.DataManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginController
{
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button registerButton;
    
    private Stage stage;
    
    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    @FXML
    private void initialize()
    {
        // Устанавливаем максимальную ширину для errorLabel
        if (errorLabel != null)
        {
            errorLabel.setMaxWidth(320);
        }
    }
    
    @FXML
    private void onLogin()
    {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty())
        {
            errorLabel.setText("Заполните все поля");
            return;
        }
        
        var user = DataManager.authenticateUser(username, password);
        
        
        if (user != null)
        {
            try
            {
                // Загружаем главное окно
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gamer_hub/fxml/main.fxml"));
                Parent root = loader.load();
                
                Stage mainStage = new Stage();
                mainStage.setTitle("Gamer Hub - Главная");
                Scene mainScene = new Scene(root, 1200, 800);
                
                // Загружаем CSS
                String cssPath = getClass().getResource("/gamer_hub/css/styles.css").toExternalForm();
                mainScene.getStylesheets().add(cssPath);
                
                mainStage.setScene(mainScene);
                mainStage.setMinWidth(1000);
                mainStage.setMinHeight(700);
                
                // Передаем stage в MainController
                MainController mainController = loader.getController();
                mainController.setStage(mainStage);
                
                // Закрываем окно входа
                stage.close();
                
                // Открываем главное окно
                mainStage.show();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                errorLabel.setText("Ошибка загрузки главного окна");
            }
        }
        else
        {
            errorLabel.setText("Неверное имя пользователя или пароль");
            errorLabel.setStyle("-fx-text-fill: #FF6B6B;"); // Возвращаем красный цвет
        }
    }
    
    @FXML
    private void onRegister()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gamer_hub/fxml/register.fxml"));
            Parent root = loader.load();
            
            Stage registerStage = new Stage();
            registerStage.setTitle("Регистрация");
            registerStage.initModality(Modality.APPLICATION_MODAL);
            registerStage.initOwner(stage);
            registerStage.initStyle(StageStyle.UTILITY);
            
            Scene scene = new Scene(root, 500, 550); // Увеличил высоту, чтобы вместить все поля
            
            // ЗАГРУЖАЕМ CSS ДЛЯ ОКНА РЕГИСТРАЦИИ
            String cssPath = getClass().getResource("/gamer_hub/css/styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            registerStage.setScene(scene);
            
            RegisterController controller = loader.getController();
            controller.setStage(registerStage);
            
            registerStage.showAndWait();
            
            // После закрытия окна регистрации можно показать сообщение
            errorLabel.setText("Регистрация завершена! Теперь вы можете войти.");
            errorLabel.setStyle("-fx-text-fill: #4CAF50;");
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            errorLabel.setText("Ошибка открытия окна регистрации");
        }
    }
}