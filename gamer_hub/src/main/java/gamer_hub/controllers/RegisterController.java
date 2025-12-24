package gamer_hub.controllers;

import gamer_hub.service.DataManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController
{
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private Button cancelButton;
    
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
            errorLabel.setMaxWidth(350);
        }
    }

    @FXML
    private void onRegister()
    {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String email = emailField.getText().trim();
        
        // Сбрасываем цвет ошибки
        errorLabel.setStyle("-fx-text-fill: #FF6B6B;");
        
        if (username.isEmpty() || password.isEmpty() || email.isEmpty())
        {
            errorLabel.setText("Заполните все обязательные поля");
            return;
        }
        
        if (!password.equals(confirmPassword))
        {
            errorLabel.setText("Пароли не совпадают");
            return;
        }
        
        if (password.length() < 6)
        {
            errorLabel.setText("Пароль должен содержать минимум 6 символов");
            return;
        }
        
        if (!email.contains("@") || !email.contains("."))
        {
            errorLabel.setText("Введите корректный email адрес");
            return;
        }
        
        if (DataManager.userExists(username))
        {
            errorLabel.setText("Пользователь с таким именем уже существует");
            return;
        }
        
        try
        {
            var user = DataManager.createUser(username, password, email);
            System.out.println("✓ Новый пользователь создан:");
            System.out.println("  Username: " + username);
            System.out.println("  Email: " + email);
            System.out.println("  ID: " + user.getId());
            
            // Меняем текст на зеленый при успехе
            errorLabel.setText("✓ Регистрация успешна! Теперь вы можете войти в систему.");
            errorLabel.setStyle("-fx-text-fill: #4CAF50;");
            
            // Закрываем окно через 2 секунды
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(() -> stage.close());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
        }
        catch (Exception e)
        {
            errorLabel.setText("Ошибка при регистрации: " + e.getMessage());
        }
    }
    
    @FXML
    private void onCancel()
    {
        stage.close();
    }
}