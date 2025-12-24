package gamer_hub.controllers;

import gamer_hub.service.DataManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class AddGameController
{
    @FXML private VBox gamesContainer;
    @FXML private Button trackButton;
    @FXML private Button cancelButton;
    @FXML private Label infoLabel;
    
    private MainController mainController;
    private Stage stage;
    private List<CheckBox> gameCheckboxes = new ArrayList<>();
    
    public void setMainController(MainController mainController)
    {
        this.mainController = mainController;
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }
    
    @FXML
    private void initialize()
    {
        System.out.println("✓ Окно добавления игр инициализировано");
        loadGames();
    }
    
    private void loadGames()
    {
        // Очищаем контейнер
        gamesContainer.getChildren().clear();
        gameCheckboxes.clear();
        
        // Получаем все игры из DataManager
        var allGames = DataManager.getAllGames();
        
        if (allGames.isEmpty())
        {
            Label noGamesLabel = new Label("Нет доступных игр для отслеживания");
            noGamesLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #D3AF85; -fx-padding: 20;");
            gamesContainer.getChildren().add(noGamesLabel);
            trackButton.setDisable(true);
            return;
        }
        
        // Создаем чекбоксы для каждой игры
        for (var game : allGames)
        {
            // Создаем контейнер для игры
            HBox gameItem = new HBox();
            gameItem.setSpacing(15);
            gameItem.setStyle("-fx-background-color: #1A141A; -fx-background-radius: 10; " +
                            "-fx-padding: 15; -fx-border-color: #8E5915; -fx-border-radius: 10; " +
                            "-fx-border-width: 1;");
            
            // Чекбокс для выбора
            CheckBox checkBox = new CheckBox();
            checkBox.setStyle("-fx-text-fill: #D3AF85;");
            checkBox.setUserData(game); // Сохраняем объект игры в чекбоксе
            
            // Добавляем в список для последующего использования
            gameCheckboxes.add(checkBox);
            
            // Информация об игре
            VBox gameInfo = new VBox();
            gameInfo.setSpacing(5);
            
            Label titleLabel = new Label(game.getTitle());
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D3AF85;");
            
            Label genreLabel = new Label("Жанр: " + game.getGenre());
            genreLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8E5915;");
            
            Label descLabel = new Label(game.getDescription());
            descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #D3AF85; -fx-wrap-text: true;");
            descLabel.setMaxWidth(600);
            
            gameInfo.getChildren().addAll(titleLabel, genreLabel, descLabel);
            
            gameItem.getChildren().addAll(checkBox, gameInfo);
            gamesContainer.getChildren().add(gameItem);
        }
        
        infoLabel.setText("Доступно игр: " + allGames.size() + ". Выберите игры для отслеживания.");
    }
    
    @FXML
    private void onTrackClick()
    {
        // Собираем ID выбранных игр
        List<Integer> selectedGameIds = new ArrayList<>();
        List<String> selectedGameTitles = new ArrayList<>();
        
        for (CheckBox checkBox : gameCheckboxes)
        {
            if (checkBox.isSelected())
            {
                var game = (gamer_hub.model.Game) checkBox.getUserData();
                selectedGameIds.add(game.getId());
                selectedGameTitles.add(game.getTitle());
            }
        }
        
        if (selectedGameIds.isEmpty())
        {
            infoLabel.setText("⚠ Выберите хотя бы одну игру для отслеживания!");
            infoLabel.setStyle("-fx-text-fill: #FF6B6B;");
            return;
        }
        
        // Получаем текущего пользователя
        var currentUser = DataManager.getCurrentUser();
        if (currentUser == null)
        {
            infoLabel.setText("Ошибка: пользователь не найден!");
            infoLabel.setStyle("-fx-text-fill: #FF6B6B;");
            return;
        }
        
        try
        {
            // Добавляем игры пользователю через DataManager
            DataManager.addGamesToUser(currentUser.getId(), selectedGameIds);
            
            // Обновляем текущего пользователя в памяти
            currentUser.getTrackedGameIds().addAll(selectedGameIds);
            
            // Выводим информацию в консоль
            System.out.println("✓ Игры добавлены пользователю " + currentUser.getUsername() + ":");
            for (String title : selectedGameTitles)
            {
                System.out.println("  - " + title);
            }
            
            // Показываем сообщение об успехе
            infoLabel.setText("✓ Игры успешно добавлены! Закрытие окна...");
            infoLabel.setStyle("-fx-text-fill: #4CAF50;");
            trackButton.setDisable(true);
            
            // Закрываем окно через 1.5 секунды
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    Platform.runLater(() -> stage.close());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
        }
        catch (Exception e)
        {
            infoLabel.setText("Ошибка при сохранении: " + e.getMessage());
            infoLabel.setStyle("-fx-text-fill: #FF6B6B;");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void onCancelClick()
    {
        System.out.println("✗ Отмена выбора игр");
        stage.close();
    }
}